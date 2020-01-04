<template>
    <div>
        <div v-show="isReportPresent" :id="target" :style="{width: getWidth, height: getHeight}"/>
    </div>
</template>

<script>
    import * as am4core from "@amcharts/amcharts4/core";
    import * as am4charts from "@amcharts/amcharts4/charts";
    import am4themes_amcharts from "@amcharts/amcharts4/themes/amcharts";
    import {moneyFormat} from "../../util/Formatters";

    am4core.useTheme(am4themes_amcharts);

    export default {
        name: "RawStatementsReport",
        props: {
            statements: {
                type: Array,
            },
        },
        data() {
            return {
                target: `chart-target-div-${Math.round(Math.random() * 1000000)}`,
                width: null,
                height: 600,
                chart: null,
            }
        },
        watch: {
            statements() {
                this.draw()
            },
        },
        computed: {
            isReportPresent() {
                return this.statements && this.statements.length > 0
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
            draw() {
                if (this.chart)
                    this.chart.dispose()

                if (!this.isReportPresent)
                    return

                const chart = am4core.create(this.target, am4charts.XYChart)

                chart.data = this.statements.map(rec => ({
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

                chart.scrollbarX = new am4charts.XYChartScrollbar()
                chart.scrollbarX.series.push(series)
                chart.scrollbarY = new am4core.Scrollbar()

                chart.legend = new am4charts.Legend()
                chart.legend.position = "bottom"

                this.chart = chart
            },
        },
        beforeDestroy() {
            if (this.chart)
                this.chart.dispose()
        },
        mounted() {
            this.draw()
        }
    }
</script>

<style scoped>
</style>