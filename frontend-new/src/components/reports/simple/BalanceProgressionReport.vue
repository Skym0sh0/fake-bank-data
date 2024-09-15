<script setup lang="ts">
import Waiter from "../../misc/Waiter.vue";
import {BalanceDataPoint, ReportsApi} from "@api/index.ts"
import {computed, inject, nextTick, onBeforeUnmount, onMounted, ref, useTemplateRef} from "vue";
import {apiRefKey} from "../../../keys.ts";
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import {XYChart} from "@amcharts/amcharts4/charts";
import {setInitialZoom} from "../../../utils/ZoomUtils.ts";
import {DateTime} from "luxon";

const api: ReportsApi | undefined = inject(apiRefKey)?.reportsApi;

const {height} = defineProps<{ height: number }>();

const target2 = `balance-progression-report-chart-target-div`
const target = useTemplateRef("balance-progression-report-chart-target-div");
const width = ref(null)
const chartRef = ref<XYChart | null>(null)
const isLoading = ref(false)
const data = ref<BalanceDataPoint[]>([])

const isReportPresent = computed(() => {
  return data.value.length > 0;
});

const getWidth = computed(() => {
  if (!width.value)
    return "100%"

  return `${width.value}px`
});

const getHeight = computed(() => {
  return `${height}px`;
});

function draw() {
  chartRef.value?.dispose()

  if (!target.value) {
    console.log("before nexttick")
    nextTick(() => draw())
    console.log("no element")
    return;
  }

  if (!isReportPresent.value)
    return;

  const chart: XYChart = am4core.create(target.value.$el, am4charts.XYChart)

  chart.data = data.value.flatMap(rec => {
    if (!rec.date || rec.balanceInCents === undefined)
      return [];

    return [{
      date: DateTime.fromISO(rec.date).toJSDate(),
      value: rec.balanceInCents / 100.0,
    }];
  })

  console.log("draw", chart.data)

  const dateAxis = chart.xAxes.push(new am4charts.DateAxis())
  dateAxis.title.text = "Datum"
  dateAxis.renderer.grid.template.location = 0.5

  const valueAxis = chart.yAxes.push(new am4charts.ValueAxis())
  valueAxis.dataFields.value = "value"
  // valueAxis.dataFields["value"] = "value"
  valueAxis.title.text = "Kontostand"
  valueAxis.numberFormatter.numberFormat = {
    currency: "EUR",
    style: "currency",
    minimumFractionDigits: 2,
    maximumFractionDigits: 3
  }
  valueAxis.numberFormatter.intlLocales = "de-DE"

  const series = chart.series.push(new am4charts.StepLineSeries())
  series.name = "Kontostand über die Zeit"
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

  chart.exporting.menu = new am4core.ExportMenu();

  setInitialZoom(chart, dateAxis)

  chartRef.value = chart;
}

function loadData() {
  console.log("load data")
  isLoading.value = true

  api?.fetchBalanceProgressionReport()
    .then(res => {
      data.value = res.data ?? []

      // console.log("loaded data", res.data)

      nextTick(() => draw())
    })
    .catch(e => console.log(e))
    .finally(() => isLoading.value = false)
}

onBeforeUnmount(() => {
  chartRef.value?.dispose()
})

loadData()
onMounted(() => {
  console.log("mounted")
})
</script>

<template>
  <div :style="{width: getWidth, height: getHeight}">
    <waiter :is-loading="isLoading">
      <div id="balance-progression-report-chart-target-div"
           ref="balance-progression-report-chart-target-div"
           class="h-100 w-100"/>
    </waiter>
  </div>
</template>

<style scoped>

</style>