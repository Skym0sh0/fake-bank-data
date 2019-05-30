import axios from "axios";
import uuid from "uuid";

class RegularIncomeAPI {
    constructor(baseUrl) {
        if (!baseUrl)
            throw Error("baseUrl must be set")

        this.baseUrl = baseUrl

        this.client = axios.create({
            baseURL: baseUrl,
            timeout: 1500,
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
        // return this.client.post(`transactions/${t.id}`, t)
        return this.client.post(`transactions`, t)
    }

    patchTransaction(t) {
        return this.client.patch(`transactions/${t.id}`, t)
    }

    deleteTransaction(id) {
        return this.client.delete(`transactions/${id}`)
    }

    getAllStatements() {
        return this.client.get('statements')
    }
}

const api = new RegularIncomeAPI("https://my-json-server.typicode.com/Skym0sh0/fake-bank-data")

export {
    RegularIncomeAPI,
    api
}
