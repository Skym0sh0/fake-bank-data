<script setup lang="ts">

import {GraphDataPoint} from "./CategoryVolumeGraph.vue";
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import {XYChart} from "@amcharts/amcharts4/charts";
import {computed, onBeforeUnmount, onMounted, ref, useTemplateRef} from "vue";
import * as _ from "lodash";

const {data} = defineProps<{
  data: GraphDataPoint[];
}>();

const target = useTemplateRef("category-graph")

const chartRef = ref<XYChart | null>(null);

const processedData = computed(() => {
  return _.sortBy(data, x => x.date.getTime())
})

function draw() {
  reset()

  const chart = am4core.create(target.value, am4charts.XYChart)

  chart.data = processedData.value

  const dateAxis = chart.xAxes.push(new am4charts.DateAxis())
  dateAxis.title.text = "Datum"
  dateAxis.renderer.grid.template.location = 0.5

  const valueAxis = chart.yAxes.push(new am4charts.ValueAxis())
  // valueAxis.dataFields.value = "value"
  valueAxis.title.text = "Volumen"
  valueAxis.numberFormatter.numberFormat = {
    currency: "EUR",
    style: "currency",
    minimumFractionDigits: 2,
    maximumFractionDigits: 3
  }
  valueAxis.numberFormatter.intlLocales = "de-DE"

  {
    const series = chart.series.push(new am4charts.ColumnSeries())
    series.name = "Einkommen"
    series.stacked = true
    series.dataFields.dateX = "date"
    series.dataFields.valueY = "income"
    series.tooltipText = "{dateX}: {valueY}"
    series.legendSettings.valueText = ""
    series.legendSettings.itemValueText = "[bold]{valueY}[/]"
    series.legendSettings.labelText = "{name}"
    series.legendSettings.itemLabelText = "{dateX}: "
    series.minBulletDistance = 10
    series.bullets.push(new am4charts.CircleBullet()).circle.radius = 2
  }
  {
    const col = am4core.color("#FF4547")
    const series = chart.series.push(new am4charts.ColumnSeries())
    series.name = "Ausgaben"
    series.stacked = true
    series.columns.template.fill = col;
    series.columns.template.stroke = col.brighten(-0.1);
    series.dataFields.dateX = "date"
    series.dataFields.valueY = "expense"
    series.tooltipText = "{dateX}: {valueY}"
    series.legendSettings.valueText = ""
    series.legendSettings.itemValueText = "[bold]{valueY}[/]"
    series.legendSettings.labelText = "{name}"
    series.legendSettings.itemLabelText = "{dateX}: "
    series.minBulletDistance = 10
    const bullets = series.bullets.push(new am4charts.CircleBullet())
    bullets.circle.radius = 2
    bullets.circle.fill = col
    bullets.circle.stroke = col
  }
  {
    const col = am4core.color("#000000")
    const series = chart.series.push(new am4charts.LineSeries())
    series.name = "Budget"
    series.stacked = false
    // series.columns.template.fill = col;
    // series.columns.template.stroke = col.brighten(-0.1);
    series.dataFields.dateX = "date"
    series.dataFields.valueY = "budget"
    series.tooltipText = "{dateX}: {valueY}"
    series.legendSettings.valueText = ""
    series.legendSettings.itemValueText = "[bold]{valueY}[/]"
    series.legendSettings.labelText = "{name}"
    series.legendSettings.itemLabelText = "{dateX}: "
    series.minBulletDistance = 10
    const bullets = series.bullets.push(new am4charts.CircleBullet())
    bullets.circle.radius = 2
    bullets.circle.fill = col
    bullets.circle.stroke = col
  }

  chart.cursor = new am4charts.XYCursor();
  chart.cursor.behavior = "zoomXY"

  chart.scrollbarX = new am4core.Scrollbar()
  chart.scrollbarY = new am4core.Scrollbar()

  chart.legend = new am4charts.Legend()
  chart.legend.position = "bottom"

  chart.exporting.menu = new am4core.ExportMenu();

  chartRef.value = chart
}

function reset() {
  chartRef.value?.dispose()
  chartRef.value = null
}

onBeforeUnmount(() => reset());

onMounted(() => {
  draw();
})
</script>

<template>
  <div>
    <div ref="category-graph" style="width: 100%; height: 50vh"/>
  </div>
</template>
