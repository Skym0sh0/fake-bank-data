import {DateAxis, XYChart} from "@amcharts/amcharts4/charts";
import {DateTime} from "luxon";

export function setInitialZoom(chart: XYChart, dateAxis: DateAxis) {
  chart.events.on('ready', () => {
    let now = DateTime.now();

    const start = now
      .minus({years: 3, months: 1});

    const end = now
      .plus({months: 1});

    dateAxis.zoomToDates(start.toJSDate(), end.toJSDate())
  })
}
