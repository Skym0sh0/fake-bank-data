import axios from "axios";
import uuid from "uuid";

class RegularIncomeAPI {
    constructor(baseUrl) {
        if (!baseUrl)
            throw Error("baseUrl must be set")

        this.baseUrl = baseUrl
    }

    getClient() {
        return axios.create({
            baseURL: this.baseUrl,
            // timeout: 1500,
            headers: {
                correlationid: uuid.v4()
            },
        })
    }

    getAllTransactions() {
        return this.getClient().get('transactions')
    }

    getTransaction(id) {
        return this.getClient().get(`transactions/${id}`)
    }

    createTransaction(t) {
        return this.getClient().post(`transactions`, t)
    }

    patchTransaction(t) {
        const patch = {
            [t.field]: t.value
        };

        return this.getClient().patch(`transactions/${t.id}`, patch)
    }

    deleteTransaction(id) {
        return this.getClient().delete(`transactions/${id}`)
    }

    getTransactionsForStatement(stmtId) {
        return this.getClient().get(`statements/${stmtId}/transactions`)
    }

    getStatementSummary(stmtId) {
        return this.getClient().get(`statements/${stmtId}/summary`)
    }

    getAllStatements() {
        return this.getClient().get('statements')
    }

    postStatement(stmt) {
        return this.getClient().post(`statements/${stmt.id}`, stmt)
    }
}

const api = new RegularIncomeAPI("http://localhost:8081/")

export {
    RegularIncomeAPI,
    api
}
