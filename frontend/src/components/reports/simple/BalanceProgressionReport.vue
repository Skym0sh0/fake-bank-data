<script setup lang="ts">
import Waiter from "../../misc/Waiter.vue";
import {BalanceDataPoint} from "@api/index.ts"
import {computed, nextTick, onMounted, onUnmounted, ref, useTemplateRef} from "vue";
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import {XYChart} from "@amcharts/amcharts4/charts";
import {DateTime} from "luxon";
import {useApi} from "../../../store/use-api.ts";
import {useNotification} from "../../../store/use-notification.ts";

const api = useApi()
const notification = useNotification();

const {height} = defineProps<{ height: number }>();

const width = ref(null)

const target = useTemplateRef<HTMLDivElement>("balance-progression-report-chart-target-div");

const isLoading = ref(false)
const chartRef = ref<XYChart | null>(null)
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

const preparedData = computed(() => {
  return data.value.flatMap(rec => {
    if (!rec.date || rec.balanceInCents === undefined)
      return [];

    return [{
      date: DateTime.fromISO(rec.date).toJSDate(),
      value: rec.balanceInCents / 100.0,
    }];
  })
})

function draw() {
  chartRef.value?.dispose()

  if (!isReportPresent.value)
    return;

  if (!target.value) {
    console.error("Target element is not yet present. Is it hidden due to conditional rendering/loading?")
    return;
  }

  const chart: XYChart = am4core.create(target.value, am4charts.XYChart)

  chart.data = preparedData.value

  const dateAxis = chart.xAxes.push(new am4charts.DateAxis())
  dateAxis.title.text = "Datum"
  dateAxis.renderer.grid.template.location = 0.5

  const valueAxis = chart.yAxes.push(new am4charts.ValueAxis())
  valueAxis.dataFields.data = "value"
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

  // Due to a bug (???) in am4 creating a XYCursor yields masses of errors on disposing the component
  // chart.cursor = new am4charts.XYCursor();
  // chart.cursor.behavior = "zoomXY"

  chart.scrollbarX = new am4core.Scrollbar()
  chart.scrollbarY = new am4core.Scrollbar()

  chart.legend = new am4charts.Legend()
  chart.legend.position = "bottom"

  chart.exporting.menu = new am4core.ExportMenu();

  // setInitialZoom(chart, dateAxis)

  chartRef.value = chart;
}

function loadData() {
  isLoading.value = true

  api.reportsApi.fetchBalanceProgressionReport()
    .then(res => {
      data.value = res.data.data ?? []
    })
    .catch(e => notification.notifyError("Report konnte nicht geladen werden", e))
    .finally(() => {
      isLoading.value = false;

      nextTick(() => draw())
    })
}

onUnmounted(() => {
  chartRef.value?.dispose()
})

onMounted(() => {
  loadData()
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
