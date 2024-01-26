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
import am4themes_amcharts from "@amcharts/amcharts4/themes/amcharts";
import {moneyFormat} from "@/util/Formatters";
import {setInitialZoom} from "./ZoomUtil";
import {api} from "@/api/RegularIncomeAPI";
import Waiter from "@/components/misc/Waiter.vue";

am4core.useTheme(am4themes_amcharts);

export default {
    name: "BalanceProgressionReport",
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
            data: [],
        }
    },
    computed: {
        isReportPresent() {
            return this.data.length > 0
        },
        getWidth() {
            if (!this.width)
                return '100%'

            return `${this.width}px`
        },
        getHeight() {
            return `${this.height}px`
        },
    },
    methods: {
        loadData() {
            this.isLoading = true;

            api.getReports().fetchBalanceProgressionReport()
                .then(res => {
                    this.data = res.data;
                    this.isLoading = false

                    this.$nextTick(() => this.draw())
                })
                .catch(e => console.log(e))
                .finally(() => this.isLoading = false)
        },
        draw() {
            if (this.chart)
                this.chart.dispose()

            if (!this.isReportPresent)
                return

            const chart = am4core.create(this.target, am4charts.XYChart)

            chart.data = this.data.map(rec => ({
                date: new Date(rec.date),
                value: rec.balanceInCents / 100.0,
            }))

            const dateAxis = chart.xAxes.push(new am4charts.DateAxis())
            dateAxis.title.text = "Dates"
            dateAxis.renderer.grid.template.location = 0.5

            const valueAxis = chart.yAxes.push(new am4charts.ValueAxis())
            valueAxis.dataFields.value = "value"
            valueAxis.title.text = "Balance"
            valueAxis.numberFormatter.numberFormat = moneyFormat.getStyle()
            valueAxis.numberFormatter.intlLocales = "de-DE"

            const series = chart.series.push(new am4charts.StepLineSeries())
            series.name = "Balance over Time"
            series.dataFields.dateX = "date"
            series.dataFields.valueY = "value"

            series.tooltipText = "{dateX}: {valueY}"

            series.legendSettings.valueText = ""
            series.legendSettings.itemValueText = "[bold]{valueY}[/]"
            series.legendSettings.labelText = "{name}"
            series.legendSettings.itemLabelText = "{dateX}: "

            series.minBulletDistance = 10
            series.bullets.push(new am4charts.CircleBullet()).circle.radius = 2

            chart.cursor = new am4charts.XYCursor();
            chart.cursor.behavior = "zoomXY"

            chart.scrollbarX = new am4core.Scrollbar()
            chart.scrollbarY = new am4core.Scrollbar()

            chart.legend = new am4charts.Legend()
            chart.legend.position = "bottom"

            setInitialZoom(chart, dateAxis)

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
