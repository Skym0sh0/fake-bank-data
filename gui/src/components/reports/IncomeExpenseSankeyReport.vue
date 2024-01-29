<template>
    <v-card>
        <v-card-title>
            Sankey
        </v-card-title>

        <v-card-subtitle class="d-flex justify-content-between">
            <span>{{ helptext }}</span>
            <span>{{ timespanText }}</span>
        </v-card-subtitle>

        <v-card-text>
            <v-sheet :height="getHeight" :width="getWidth">
                <waiter :is-loading="isLoading">
                    <div class="h-100 w-100">
                        <div v-if="!isReportPresent" class="h-100 w-100 m-auto">
                            Keine Daten vorhanden
                        </div>
                        <div v-else :id="target" class="h-100 w-100"/>
                    </div>
                </waiter>
            </v-sheet>
        </v-card-text>
    </v-card>
</template>

<script>
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import am4themes_animated from "@amcharts/amcharts4/themes/animated";
import {api} from "@/api/RegularIncomeAPI";
import Waiter from "@/components/misc/Waiter.vue";
import _ from "lodash";
import {MonthIndexToName} from "@/util/months";

am4core.useTheme(am4themes_animated);

export default {
    name: "IncomeExpenseSankeyReport",
    components: {Waiter},
    props: {
        depth: {
            type: Number,
            required: false,
        },
        year: {
            type: Number,
            required: false,
        },
        month: {
            type: Number,
            required: false,
        },
        height: {
            type: Number,
            required: true,
        },
    },
    data() {
        return {
            target: `chart-target-div-${Math.round(Math.random() * 1000000)}`,
            width: null,
            chart: null,
            isLoading: false,
            sankeyData: null,
        }
    },
    computed: {
        isReportPresent() {
            return this.incomeExpensesSankey.length > 0
        },
        getWidth() {
            if (!this.width)
                return '100%'

            return `${this.width}px`
        },
        getHeight() {
            return `${this.height}px`
        },
        incomeExpensesSankey() {
            if (!this.sankeyData)
                return [];

            return (this.sankeyData.flows || [])
                .filter(dp => !this.depth || dp.depthLevel < this.depth);
        },
        rootCategories() {
            const categories = new Set(this.incomeExpensesSankey.flatMap(dp => [dp.fromCategory, dp.toCategory]))
            const permutate = candidate => {
                while (categories.has(candidate))
                    candidate = "_" + candidate;
                return candidate;
            }

            return {
                income: permutate("Einkommen"),
                expense: permutate("Ausgaben"),
            }
        },
        processedData() {
            const totalIncome = this.incomeExpensesSankey.filter(dp => dp.fromCategory && !dp.toCategory)
                .reduce((prev, cur) => prev + cur.amountInCents, 0);

            const connectionIncomeExpense = {
                from: this.rootCategories.income,
                to: this.rootCategories.expense,
                amount: totalIncome / 100.0,
            };

            return [
                connectionIncomeExpense,
                ...this.incomeExpensesSankey.map(dp => {
                    return {
                        from: dp.fromCategory || this.rootCategories.expense,
                        to: dp.toCategory || this.rootCategories.income,
                        amount: Math.abs(dp.amountInCents) / 100.0,
                    };
                })
            ];
        },
        helptext() {
            const prefix = "Alle Ein- und Ausgaben"
            const levels = `aufgefächert auf bis zu ${this.depth} Ebene(n)`;

            if (this.year && this.month)
                return `${prefix} für ${MonthIndexToName[this.month]} ${this.year} ${levels}`;
            if (this.year)
                return `${prefix} für ${this.year} ${levels}`;

            return `${prefix} ${levels}`
        },
        timespanText() {
            if (!this.sankeyData)
                return "";

            return `Von ${this.sankeyData.start} bis ${this.sankeyData.end}`;
        },
    },
    watch: {
        year(newValue, oldValue) {
            if (newValue === oldValue)
                return;

            this.reload();
        },
        month(newValue, oldValue) {
            if (newValue === oldValue)
                return;

            this.reload();
        },
    },
    methods: {
        reload() {
            _.debounce(() => {
                this.loadData()
            }, 500)();
        },
        loadData() {
            if (this.depth < 0)
                return;

            this.isLoading = true;

            api.getReports().fetchIncomeExpenseFlowYearReport(this.year, this.month)
                .then(res => this.sankeyData = res)
                .catch(e => console.error(e))
                .finally(() => {
                    this.isLoading = false;

                    this.$nextTick(() => this.draw())
                })
        },
        draw() {
            if (this.chart) {
                this.chart.dispose()
                this.chart = null
            }

            if (!this.isReportPresent) {
                return
            }

            const chart = am4core.create(this.target, am4charts.SankeyDiagram)

            // chart.orientation = "vertical";

            chart.data = this.processedData
            chart.dataFields.fromName = "from";
            chart.dataFields.toName = "to";
            chart.dataFields.value = "amount";
            // chart.dataFields.color = "nodeColor";
            chart.sortBy = "value";

            chart.paddingRight = 30;

            const nodeTemplate = chart.nodes.template;
            // nodeTemplate.inert = true;
            // nodeTemplate.clickable = true;
            // nodeTemplate.draggable = true;
            // nodeTemplate.readerTitle = "Drag me!";
            // nodeTemplate.showSystemTooltip = true;
            // nodeTemplate.width = 50;
            // nodeTemplate.nameLabel.label
            nodeTemplate.nameLabel.label.hideOversized = true;
            // nodeTemplate.nameLabel.label.textAlign = "middle";

            const linkTemplate = chart.links.template;
            // linkTemplate.inert = true;
            // linkTemplate.tension = 0.5;
            // linkTemplate.controlPointDistance = 0.1;
            linkTemplate.colorMode = "toNode";

            chart.exporting.menu = new am4core.ExportMenu();

            this.chart = chart
        },
    },
    beforeDestroy() {
        if (this.chart)
            this.chart.dispose()
    },
    mounted() {
        this.loadData()
    }
}
</script>

<style scoped>

</style>
