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

const moneyFormat = new MoneyFormatter()

export {
    MoneyFormatter,
    moneyFormat
}
