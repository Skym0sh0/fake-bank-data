import moment from "moment";

function formatBytes(bytes, decimals = 2) {
    if (bytes === 0) return '0 Bytes';

    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];

    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

class MoneyFormatter {
    constructor() {
        this.frmt = new Intl.NumberFormat('de-DE', {
            style: 'currency',
            currency: 'EUR',
        })
    }

    formatCents(amount) {
        if (!amount && amount !== 0)
            return null

        return this.frmt.format(amount / (100))
    }
}

class DateFormatter {
    constructor() {
    }

    formatIsoDate(date) {
        if (!date)
            return null

        const transformed = moment(date)

        return transformed.format("YYYY-MM-DD")
    }

    formatDate(date) {
        if (!date)
            return null

        const transformed = moment(date)

        return transformed.format("ddd YYYY-MM-DD")
    }

    formatTimestamp(ts) {
        if (!ts)
            return null

        const transformed = moment(ts)

        return transformed.format("YYYY-MM-DD HH:mm:ss")
    }
}

const moneyFormat = new MoneyFormatter()
const dateFormat = new DateFormatter()

export {
    MoneyFormatter,
    DateFormatter,
    moneyFormat,
    dateFormat,
    formatBytes
}
