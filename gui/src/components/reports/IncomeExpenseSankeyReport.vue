<template>
    <div :style="{width: getWidth, height: getHeight}">
        <waiter :is-loading="isLoading">
            <div :id="target" class="h-100 w-100"/>
        </waiter>
    </div>
</template>

<script>
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import am4themes_animated from "@amcharts/amcharts4/themes/animated";
import {api} from "@/api/RegularIncomeAPI";
import Waiter from "@/components/misc/Waiter.vue";

am4core.useTheme(am4themes_animated);

export default {
    name: "IncomeExpenseSankeyReport",
    components: {Waiter},
    props: {
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
            incomeExpensesSankey: [],
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
        processedData() {
            return this.incomeExpensesSankey.map(dp => ({
                from: dp.fromCategory || "Budget",
                to: dp.toCategory || "Budget",
                amount: dp.amountInCents / 100.0,
            }));
        },
    },
    methods: {
        loadData() {
            this.isLoading = true;

            api.getReports().fetchIncomeExpenseFlowReport()
                .then(res => this.incomeExpensesSankey = res.flows)
                .catch(e => console.error(e))
                .finally(() => {
                    this.isLoading = false;

                    this.$nextTick(() => this.draw())
                })
        },
        draw() {
            if (this.chart)
                this.chart.dispose()

            if (!this.isReportPresent)
                return

            const chart = am4core.create(this.target, am4charts.SankeyDiagram)

            // chart.orientation = "vertical";

            chart.data = this.processedData
            //chart.data.map(c=>`${c.from} -> ${c.to} => ${c.amount}`).forEach(c=> console.log(c))
            console.table(chart.data)
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

            const linkTemplate = chart.links.template;
            // linkTemplate.inert = true;
            linkTemplate.colorMode = "toNode";
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
