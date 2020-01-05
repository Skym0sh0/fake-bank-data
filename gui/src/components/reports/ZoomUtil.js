import * as moment from "moment";

function setInitialZoom(chart, dateAxis) {
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
}

export {
    setInitialZoom,
}
