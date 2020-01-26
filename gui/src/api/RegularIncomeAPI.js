import axios from "axios";
import uuid from "uuid";
import {denormalizeCategory, denormalizeReason, denormalizeStatement, denormalizeTransaction} from "../util/Normalizer";

class RegularIncomeAPI {
    constructor(baseUrl) {
        if (!baseUrl)
            throw Error(`baseUrl '${baseUrl}' must be set`)

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

    getTransactionsForStatement(stmtId) {
        return this.getClient().get(`statements/${stmtId}/transactions`)
            .then(res => res.data.map(stmt => denormalizeTransaction(stmt)))
    }

    getReasonsForTransactions() {
        return this.getClient().get('transactions/reasons')
            .then(res => res.data.map(rsn => denormalizeReason(rsn)))
    }

    getStatementSummary(stmtId) {
        return this.getClient().get(`statements/${stmtId}/summary`)
    }

    getAllStatements() {
        return this.getClient().get('statements')
            .then(res => res.data.map(stmt => denormalizeStatement(stmt)))
    }

    readStatement(id) {
        return this.getClient().get(`statements/${id}`)
            .then(res => denormalizeStatement(res.data))
    }

    deleteStatement(id) {
        return this.getClient().delete(`statements/${id}`)
    }

    postStatement(stmt) {
        return this.getClient().post(`statements/${stmt.id}`, stmt)
    }

    putFileToImport(file) {
        const formData = new FormData()
        formData.append('file', file)

        return this.getClient().put(
            'statements/import',
            formData,
            {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
        )
    }

    fetchStatementsReport(begin, end) {
        return this.getClient().get('reports/statements', {
            params: {
                begin: begin,
                end: end,
            }
        }).then(res => res.data)
    }

    fetchIncomeExpenseReport() {
        return this.getClient().get('reports/monthly-income-expenses')
            .then(res => res.data)
    }

    fetchCategories() {
        return this.getClient().get('categories')
            .then(res => res.data.map(c => denormalizeCategory(c)))
    }
}

const api = new RegularIncomeAPI(process.env.VUE_APP_CONFIG_BACKEND_URL)

export {
    RegularIncomeAPI,
    api
}
