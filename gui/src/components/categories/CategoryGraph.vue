<template>
    <div>
        <div v-if="processedData.length" :id="target" style="width: 100%; height: 50vh"/>

        <div v-else>
            Keine Daten vorhanden
        </div>
    </div>
</template>

<script>
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import {moneyFormat} from "@/util/Formatters";
import _ from "lodash";

export default {
    name: "CategoryGraph",
    props: {
        data: {
            type: Array,
            required: true,
        }
    },
    data() {
        return {
            target: "chart-target-div-for-category",
            chart: null,
        }
    },
    watch: {
        data() {
            this.draw()
        },
    },
    computed: {
        processedData() {
            return _.sortBy(this.data, x => x.date.getTime())
        },
    },
    methods: {
        draw() {
            this.reset()

            const chart = am4core.create(this.target, am4charts.XYChart)

            chart.data = this.processedData

            const dateAxis = chart.xAxes.push(new am4charts.DateAxis())
            dateAxis.title.text = "Datum"
            dateAxis.renderer.grid.template.location = 0.5

            const valueAxis = chart.yAxes.push(new am4charts.ValueAxis())
            valueAxis.dataFields.value = "value"
            valueAxis.title.text = "Volumen"
            valueAxis.numberFormatter.numberFormat = moneyFormat.getStyle()
            valueAxis.numberFormatter.intlLocales = "de-DE"

            const series = chart.series.push(new am4charts.LineSeries())
            series.name = "Ausgaben"
            series.stacked = true
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

            this.chart = chart
        },
        reset() {
            if (this.chart)
                this.chart.dispose()
            this.chart = null
        },
    },
    beforeDestroy() {
        this.reset();
    },
    mounted() {
        this.draw()
    }
}
</script>

<style scoped>

</style>
