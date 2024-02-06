<template>
    <div>
        <div :id="target" style="width: 100%; height: 50vh">
        </div>
    </div>
</template>

<script>
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import {moneyFormat} from "@/util/Formatters";

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
    methods: {
        draw() {
            if (this.chart)
                this.chart.dispose()

            const chart = am4core.create(this.target, am4charts.XYChart)

            chart.data = this.data.map(rec => ({
                date: new Date(rec.date),
                value: rec.amountInCents / 100.0,
            }))

            const dateAxis = chart.xAxes.push(new am4charts.DateAxis())
            dateAxis.title.text = "Datum"
            // dateAxis.renderer.grid.template.location = 0.5

            const valueAxis = chart.yAxes.push(new am4charts.ValueAxis())
            valueAxis.dataFields.value = "value"
            valueAxis.title.text = "Volumen"
            valueAxis.numberFormatter.numberFormat = moneyFormat.getStyle()
            valueAxis.numberFormatter.intlLocales = "de-DE"


            const series = chart.series.push(new am4charts.ColumnSeries())
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
