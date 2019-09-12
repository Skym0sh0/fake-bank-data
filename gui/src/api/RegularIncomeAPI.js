import axios from "axios";
import uuid from "uuid";

class RegularIncomeAPI {
    constructor(baseUrl) {
        if (!baseUrl)
            throw Error("baseUrl must be set")

        this.baseUrl = baseUrl

        this.client = axios.create({
            baseURL: baseUrl,
            // timeout: 1500,
            headers: {
                correlationid: uuid.v4()
            },
        })
    }

    getAllTransactions() {
        return this.client.get('transactions')
    }

    getTransaction(id) {
        return this.client.get(`transactions/${id}`)
    }

    createTransaction(t) {
        return this.client.post(`transactions`, t)
    }

    patchTransaction(t) {
        const patch = {
            [t.field]: t.value
        };

        return this.client.patch(`transactions/${t.id}`, patch)
    }

    deleteTransaction(id) {
        return this.client.delete(`transactions/${id}`)
    }

    getTransactionsForStatement(stmtId) {
        return this.client.get(`statements/${stmtId}/transactions`)
    }

    getStatementSummary(stmtId) {
        return this.client.get(`statements/${stmtId}/summary`)
    }

    getAllStatements() {
        return this.client.get('statements')
    }
}

const api = new RegularIncomeAPI("http://localhost:8081/")

export {
    RegularIncomeAPI,
    api
}
