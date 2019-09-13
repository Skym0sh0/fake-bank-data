import moment from "moment";

class MoneyFormatter {
    constructor() {
        this.frmt = new Intl.NumberFormat('de-DE', {
            style: 'currency',
            currency: 'EUR',
        })
    }

    formatCents(amount) {
        if (!amount)
            return null


        return this.frmt.format(amount / (100))
    }
}

class DateFormatter {
    constructor() {
    }

    formatDate(date) {
        if (!date)
            return null

        const transformed = moment(date)

        return transformed.format("ddd YYYY-MM-DD")
    }
}

const moneyFormat = new MoneyFormatter()
const dateFormat = new DateFormatter()

export {
    MoneyFormatter,
    DateFormatter,
    moneyFormat,
    dateFormat
}
