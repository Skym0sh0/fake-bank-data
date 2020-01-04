<template>
    <div v-show="isReportPresent" ref="chartDiv" :style="{width: getWidth, height: getHeight}">
        bla bla bla
        {{incomeExpenses.length}}
    </div>
</template>

<script>
    import * as am4core from "@amcharts/amcharts4/core";
    import * as am4charts from "@amcharts/amcharts4/charts";
    import {moneyFormat} from "../../util/Formatters";
    import am4themes_animated from "@amcharts/amcharts4/themes/animated";
    import * as moment from "moment";

    am4core.useTheme(am4themes_animated);

    export default {
        name: "IncomeExpenseReport",
        props: {
            incomeExpenses: {
                type: Array,
            },
        },
        data() {
            return {
                width: null,
                height: 800,
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

                const valueAxisRange = valueAxis.axisRanges.create()
                valueAxisRange.value = 400
                valueAxisRange.bullet = new am4core.Triangle()
                valueAxisRange.bullet.width = 15;
                valueAxisRange.bullet.height = 11;
                valueAxisRange.bullet.fill = am4core.color("#c00");
                valueAxisRange.grid.disabled = true
                valueAxisRange.bullet.horizontalCenter = "left"
                valueAxisRange.bullet.rotation = 90

                const incomeSeries = chart.series.push(new am4charts.LineSeries())
                incomeSeries.name = "Income per Month"
                incomeSeries.dataFields.dateX = "month"
                incomeSeries.dataFields.valueY = "income"
                incomeSeries.tooltipText = "{dateX}: {valueY}"
                incomeSeries.stroke = /*chart.colors.getIndex(0)*/ am4core.color("#00FF00").brighten(-0.0)
                incomeSeries.strokeWidth = 3
                incomeSeries.minBulletDistance = 15
                const incomeBullets = incomeSeries.bullets.push(new am4charts.CircleBullet())
                incomeBullets.fill = am4core.color("#FFFFFF")
                incomeBullets.strokeWidth = 3

                const expenseSeries = chart.series.push(new am4charts.LineSeries())
                expenseSeries.name = "Expenses per Month"
                expenseSeries.dataFields.dateX = "month"
                expenseSeries.dataFields.valueY = "expense"
                expenseSeries.tooltipText = "{dateX}: -{valueY}"
                expenseSeries.stroke = /*chart.colors.getIndex(1)*/ am4core.color("#FFFF00").brighten(-0.1)
                expenseSeries.strokeWidth = 3
                expenseSeries.minBulletDistance = 15
                const expenseBullet = expenseSeries.bullets.push(new am4charts.CircleBullet())
                expenseBullet.fill = am4core.color("#FFFFFF")
                expenseBullet.strokeWidth = 3

                chart.cursor = new am4charts.XYCursor();
                chart.cursor.xAxis = dateAxis
                chart.cursor.behavior = "zoomXY"

                chart.scrollbarX = new am4core.Scrollbar()
                chart.scrollbarY = new am4core.Scrollbar()

                chart.legend = new am4charts.Legend()

                const zeroIncomeRange = valueAxis.createSeriesRange(incomeSeries)
                zeroIncomeRange.value = 0
                zeroIncomeRange.endValue = valueAxisRange.value
                zeroIncomeRange.contents.stroke = am4core.color("#000000")

                this.findTooNegativeDifferenceRanges(chart, dateAxis, expenseSeries)
                    .forEach(range => {
                        range.contents.stroke = am4core.color("#FF0000")
                    })

                chart.events.on("ready", () => {
                    const start = moment("2020-06-17")
                        .add(-3, 'years')
                        .month(0).date(0)
                        .hour(0).minute(0).second(0).millisecond(0)
                        .add(-1, "months")

                    const end = moment()
                        .add(1, "months")

                    dateAxis.zoomToDates(start.toDate(), end.toDate())
                })

                this.chart = chart
            },
            findTooNegativeDifferenceRanges(chart, axis, series) {
                const ranges = []

                let tooHighExpensesRange = null
                for (let i = 0; i < chart.data.length; i++) {
                    const current = chart.data[i]

                    if (current.isAlerting()) {
                        if (!tooHighExpensesRange) {
                            tooHighExpensesRange = axis.createSeriesRange(series)
                            tooHighExpensesRange.date = current.month
                        }
                    } else {
                        if (tooHighExpensesRange) {
                            tooHighExpensesRange.endDate = current.month
                            ranges.push(tooHighExpensesRange)

                            tooHighExpensesRange = null
                        }
                    }
                }

                if (tooHighExpensesRange) {
                    tooHighExpensesRange.endDate = chart.data[chart.data.length - 1].month
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