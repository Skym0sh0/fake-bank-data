<template>
    <div>
        <div v-show="isReportPresent" ref="chartDiv" :style="{width: getWidth, height: getHeight}"/>
    </div>
</template>

<script>
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import {moneyFormat} from "@/util/Formatters";
import am4themes_animated from "@amcharts/amcharts4/themes/animated";
import {setInitialZoom} from "./ZoomUtil";

am4core.useTheme(am4themes_animated);

export default {
    name: "IncomeExpenseReport",
    props: {
        incomeExpenses: {
            type: Array,
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
        }
    },
    watch: {
        incomeExpenses() {
            this.draw()
        },
    },
    computed: {
        isReportPresent() {
            return this.incomeExpenses && this.incomeExpenses.length > 0
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

            const chart = am4core.create(this.$refs.chartDiv, am4charts.XYChart)

            chart.data = this.incomeExpenses.map(d => {
                const inc = parseInt(d.incomeInCents)
                const exp = parseInt(d.expenseInCents)
                const diff = inc - exp

                return {
                    month: new Date(d.month),
                    income: inc / 100.0,
                    expense: exp / 100.0,
                    difference: diff / 100.0,
                    isAlerting: function () {
                        return this.difference < 0
                    },
                }
            })

            const dateAxis = chart.xAxes.push(new am4charts.DateAxis())
            dateAxis.title.text = "Months"
            dateAxis.dateFormatter.dateFormat = "MMMM yyyy"
            dateAxis.renderer.grid.template.location = 0
            dateAxis.renderer.minGridDistance = 60
            dateAxis.baseInterval = {timeUnit: "month", count: 1}

            const valueAxis = chart.yAxes.push(new am4charts.ValueAxis())
            valueAxis.title.text = "Income"
            valueAxis.dataFields = "value"
            valueAxis.numberFormatter.numberFormat = moneyFormat.getStyle()
            valueAxis.numberFormatter.intlLocales = "de-DE"
            valueAxis.cursorTooltipEnabled = false
            valueAxis.tooltip.disabled = true

            const incomeSeries = chart.series.push(new am4charts.LineSeries())
            incomeSeries.name = "Income per Month"
            incomeSeries.dataFields.dateX = "month"
            incomeSeries.dataFields.valueY = "income"
            // incomeSeries.dataFields.openValueY = "expense"
            incomeSeries.sequencedInterpolation = true
            incomeSeries.tooltipText = "{dateX}: {valueY}"
            incomeSeries.stroke = chart.colors.getIndex(0)
            incomeSeries.strokeWidth = 3
            incomeSeries.minBulletDistance = 15
            incomeSeries.fill = incomeSeries.stroke
            incomeSeries.fillOpacity = 0.3
            incomeSeries.tensionX = 0.9
            const incomeBullets = incomeSeries.bullets.push(new am4charts.CircleBullet())
            incomeBullets.circle.radius = 3

            const expenseSeries = chart.series.push(new am4charts.LineSeries())
            expenseSeries.name = "Expenses per Month"
            expenseSeries.dataFields.dateX = "month"
            expenseSeries.dataFields.valueY = "expense"
            expenseSeries.dataFields.openValueY = "income"
            expenseSeries.sequencedInterpolation = true
            expenseSeries.tooltipText = "{dateX}: -{valueY}"
            expenseSeries.stroke = chart.colors.getIndex(1).brighten(0.1)
            expenseSeries.strokeWidth = 3
            // expenseSeries.fill = expenseSeries.stroke
            // expenseSeries.fillOpacity = 1.0
            expenseSeries.minBulletDistance = 15
            expenseSeries.tensionX = 0.9
            const expenseBullet = expenseSeries.bullets.push(new am4charts.CircleBullet())
            expenseBullet.circle.radius = 3

            chart.cursor = new am4charts.XYCursor();
            chart.cursor.xAxis = dateAxis
            chart.cursor.behavior = "zoomXY"

            chart.scrollbarX = new am4core.Scrollbar()
            chart.scrollbarY = new am4core.Scrollbar()

            chart.legend = new am4charts.Legend()

            this.findTooNegativeDifferenceRanges(chart, dateAxis)
                .forEach(range => {
                    const seriesRange = dateAxis.createSeriesRange(expenseSeries)

                    seriesRange.date = range.begin
                    seriesRange.endDate = range.end

                    // seriesRange.contents.stroke = am4core.color("#FF0000")
                    seriesRange.contents.fill = expenseSeries.stroke
                    seriesRange.contents.fillOpacity = 0.8
                })

            setInitialZoom(chart, dateAxis)

            this.chart = chart
        },
        findTooNegativeDifferenceRanges(chart, axis) {
            const findStartTime = (i, current) => {
                if (i < 1)
                    return current.month

                const previous = chart.data[i - 1]

                const previousPoint = {
                    x: am4core.time.round(previous.month, axis.baseInterval.timeUnit, axis.baseInterval.count).getTime() + axis.baseDuration / 2,
                    y1: previous.income,
                    y2: previous.expense,
                }
                const currentPoint = {
                    x: am4core.time.round(current.month, axis.baseInterval.timeUnit, axis.baseInterval.count).getTime() + axis.baseDuration / 2,
                    y1: current.income,
                    y2: current.expense,
                }

                const intersection = am4core.math.getLineIntersection(
                    {x: previousPoint.x, y: previousPoint.y1}, {x: currentPoint.x, y: currentPoint.y1},
                    {x: previousPoint.x, y: previousPoint.y2}, {x: currentPoint.x, y: currentPoint.y2},
                )

                return new Date(Math.round(intersection.x))
            }

            const ranges = []

            let tooHighExpensesRange = null
            for (let i = 0; i < chart.data.length; i++) {
                const current = chart.data[i]

                const startTime = findStartTime(i, current)

                if (current.isAlerting()) {
                    if (!tooHighExpensesRange) {
                        tooHighExpensesRange = {begin: new Date(startTime), end: null}
                    }
                } else {
                    if (tooHighExpensesRange) {
                        tooHighExpensesRange.end = new Date(startTime)
                        ranges.push(tooHighExpensesRange)

                        tooHighExpensesRange = null
                    }
                }
            }

            if (tooHighExpensesRange) {
                const last = chart.data[chart.data.length - 1]
                tooHighExpensesRange.end = new Date(last.month.getTime() + axis.baseDuration / 2);

                ranges.push(tooHighExpensesRange)

                tooHighExpensesRange = null
            }

            return ranges
        }
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