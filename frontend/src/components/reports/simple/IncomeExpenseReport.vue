<script setup lang="ts">
import Waiter from "../../misc/Waiter.vue";
import {computed, inject, nextTick, onMounted, onUnmounted, ref, useTemplateRef} from "vue";
import {MonthlyIncomeExpenseDataPoint, ReportsApi} from "@api/api.ts";
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import {DateAxis, XYChart} from "@amcharts/amcharts4/charts";
import {apiRefKey} from "../../../keys.ts";
import {DateTime} from "luxon";

const api: ReportsApi | undefined = inject(apiRefKey)?.reportsApi;

const {height} = defineProps<{ height: number }>();

const width = ref(null)

const target = useTemplateRef<HTMLDivElement>("income-expense-report-chart-target-div");

const isLoading = ref(false)
const chartRef = ref<XYChart | null>(null)
const incomeExpenses = ref<MonthlyIncomeExpenseDataPoint[]>([])

const getWidth = computed(() => {
  if (!width.value)
    return "100%"

  return `${width.value}px`
});

const getHeight = computed(() => {
  return `${height}px`;
});

type DataPoint = {
  month: Date;
  income: number;
  expense: number;
  difference: number;
  isAlerting: boolean;
}

const processedData = computed<DataPoint[]>(() => {
  return incomeExpenses.value.flatMap(d => {
    if (!d.incomeInCents || !d.expenseInCents || !d.month)
      return [];

    const inc = d.incomeInCents
    const exp = d.expenseInCents
    const diff = inc - exp

    return [{
      month: DateTime.fromISO(d.month).toJSDate(),
      income: inc / 100.0,
      expense: exp / 100.0,
      difference: diff / 100.0,
      isAlerting: diff / 100.0 < 0,
    }]
  })
})

const isReportPresent = computed(() => {
  return processedData.value.length > 0;
});

type ExpensesRange = {
  begin?: Date | null;
  end?: Date | null;
};

function findTooNegativeDifferenceRanges(chart: XYChart, axis: DateAxis): ExpensesRange[] {
  const findStartTime = (i: number, current: DataPoint): Date => {
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

  const ranges: ExpensesRange[] = []

  let tooHighExpensesRange: ExpensesRange | null = null
  for (let i = 0; i < chart.data.length; i++) {
    const current = chart.data[i]

    const startTime = findStartTime(i, current)

    if (current.isAlerting) {
      if (!tooHighExpensesRange) {
        tooHighExpensesRange = {
          begin: new Date(startTime),
          end: null
        }
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

function draw() {
  chartRef.value?.dispose()

  if (!isReportPresent.value || !target.value)
    return;

  const chart = am4core.create(target.value, am4charts.XYChart)

  chart.data = processedData.value

  const dateAxis = chart.xAxes.push(new am4charts.DateAxis())
  dateAxis.title.text = "Monate"
  dateAxis.dateFormatter.dateFormat = "MMMM yyyy"
  dateAxis.renderer.grid.template.location = 0
  dateAxis.renderer.minGridDistance = 60
  dateAxis.baseInterval = {timeUnit: "month", count: 1}

  const valueAxis = chart.yAxes.push(new am4charts.ValueAxis())
  valueAxis.title.text = "Einkommen und Ausgaben"
  valueAxis.dataFields.data = "value"
  valueAxis.numberFormatter.numberFormat = {
    currency: "EUR",
    style: "currency",
    minimumFractionDigits: 2,
    maximumFractionDigits: 3
  }
  valueAxis.numberFormatter.intlLocales = "de-DE"
  valueAxis.cursorTooltipEnabled = false
  // valueAxis.tooltip.disabled = true

  const incomeSeries = chart.series.push(new am4charts.LineSeries())
  incomeSeries.name = "Einkommen pro Monat"
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
  expenseSeries.name = "Ausgaben pro Monat"
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

  // Due to a bug (???) in am4 creating a XYCursor yields masses of errors on disposing the component
  // chart.cursor = new am4charts.XYCursor();
  // chart.cursor.xAxis = dateAxis
  // chart.cursor.behavior = "zoomXY"

  chart.scrollbarX = new am4core.Scrollbar()
  chart.scrollbarY = new am4core.Scrollbar()

  chart.legend = new am4charts.Legend()

  chart.exporting.menu = new am4core.ExportMenu();

  findTooNegativeDifferenceRanges(chart, dateAxis)
    .forEach(range => {
      const seriesRange = dateAxis.createSeriesRange(expenseSeries)

      seriesRange.date = range.begin!
      seriesRange.endDate = range.end!

      // seriesRange.contents.stroke = am4core.color("#FF0000")
      seriesRange.contents.fill = expenseSeries.stroke
      seriesRange.contents.fillOpacity = 0.8
    })

  // setInitialZoom(chart, dateAxis)

  chartRef.value = chart
}

function loadData() {
  if (!api)
    return;

  isLoading.value = true;

  api.fetchMonthlyIncomeExpenseReport()
    .then(res => {
      incomeExpenses.value = res.data.data ?? [];
    })
    .catch(e => console.error(e))
    .finally(() => {
      isLoading.value = false;

      nextTick(() => draw())
    })
}

onUnmounted(() => {
  chartRef.value?.dispose()
  chartRef.value = null
})

onMounted(() => {
  loadData()
})
</script>

<template>
  <div :style="{width: getWidth, height: getHeight}">
    <waiter :is-loading="isLoading">
      <div id="income-expense-report-chart-target-div"
           ref="income-expense-report-chart-target-div"
           class="h-100 w-100"/>
    </waiter>
  </div>
</template>
