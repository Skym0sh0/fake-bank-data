<template>
    <div>
        <div v-show="isReportPresent" ref="chartDiv" :style="{width: getWidth, height: getHeight}"/>
    </div>
</template>

<script>
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import am4themes_animated from "@amcharts/amcharts4/themes/animated";
import {api} from "@/api/RegularIncomeAPI";

am4core.useTheme(am4themes_animated);

export default {
    name: "IncomeExpenseSankeyReport",
    props: {
        incomeExpensesSankey: {
            type: Array,
            required: true,
        },
        height: {
            type: Number,
            required: true,
        },
    },
    data() {
        return {
            width: null,
            chart: null,
            categories: null,
        }
    },
    watch: {
        incomeExpensesSankey() {
            this.draw()
        },
    },
    computed: {
        isReportPresent() {
            return this.incomeExpensesSankey && this.incomeExpensesSankey.length > 0
        },
        getWidth() {
            if (!this.width)
                return '100%'

            return `${this.width}px`
        },
        getHeight() {
            return `${this.height}px`
        },
        getData() {
            if (!this.categories)
                return [];

            return this.incomeExpensesSankey.map(dp => ({
                from: this.categories[dp.fromCategoryId] || "Budget",
                to: this.categories[dp.toCategoryId] || "Budget",
                amount: Math.abs(dp.amountInCents / 100.0),
            }));
        },
    },
    methods: {
        loadCategories() {
            return api.getCategories()
                .fetchCategoryTree()
                .then(tree => {
                    const traverse = (c) => {
                        return [c, ...c.subCategories.flatMap(child => traverse(child))]
                    };

                    const flatMap = tree.flatMap(c => traverse(c)).reduce((prev, cur) => ({
                        ...prev,
                        [cur.id]: cur.name
                    }));
                    console.log("loaded", flatMap)
                    this.categories = flatMap
                })
                .catch(e => console.log(e))
        },
        draw() {
            if (this.chart)
                this.chart.dispose()

            if (!this.isReportPresent)
                return

            console.log("draw chart", this.categories)
            const chart = am4core.create(this.$refs.chartDiv, am4charts.SankeyDiagram)

            // chart.orientation = "vertical";

            chart.data = this.getData
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
        this.loadCategories()
            .then(() => this.draw())
    }
}
</script>

<style scoped>

</style>
