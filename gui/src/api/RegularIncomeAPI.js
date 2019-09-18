import axios from "axios";
import uuid from "uuid";
import {denormalizeStatement, denormalizeTransaction} from "../util/Normalizer";

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

    getTransactionsForStatement(stmtId) {
        return this.getClient().get(`statements/${stmtId}/transactions`)
            .then(res => res.data.map(stmt => denormalizeTransaction(stmt)))
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
}

const api = new RegularIncomeAPI("http://localhost:8081/")

export {
    RegularIncomeAPI,
    api
}
