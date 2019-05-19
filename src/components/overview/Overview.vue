<template>
    <div>
        <h1>Overview</h1>

        <input type="button" @click="initiallyLoadData" value="Refresh"/>

        <p class="error" v-if="errorMessage">{{errorMessage}}</p>

        <TransactionOverview v-bind:transactions="transactions" v-on:add-transaction="addTransaction"
                             v-on:delete-transaction="deleteTransaction"
                             v-on:update-transaction="updateTransaction"/>

        <StatementOverview v-bind:statements="statements"/>
    </div>
</template>

<script>
    import axios from "axios";
    import TransactionOverview from "./TransactionOverview";
    import StatementOverview from "./StatementOverview";
    import uuid from "uuid";

    const client = axios.create({
        baseURL: "https://my-json-server.typicode.com/Skym0sh0/fake-bank-data",
        timeout: 1500,
        headers: {
            correlationid: uuid.v4()
        },
    })


    export default {
        name: "Overview",
        components: {StatementOverview, TransactionOverview},
        data() {
            return {
                errorMessage: '',
                transactions: [],
                statements: []
            }
        },
        methods: {
            initiallyLoadData() {
                client.get('transactions')
                    .then(res => this.transactions = res.data)
                    .catch(e => this.errorMessage += e)

                client.get('statements')
                    .then(res => this.statements = res.data)
                    .catch(e => this.errorMessage += e)
            },
            addTransaction(newTransaction) {
                client.post('transactions', newTransaction)
                    .then(res => this.transactions = [res.data, ...this.transactions])
                    .catch(e => this.errorMessage = e)
            },
            updateTransaction(update) {
                client.patch(`transactions/${update.id}`, update)
                    .then(res => {
                        const updatedTransaction = res.data

                        let transaction = this.transactions.find(t => t.id === updatedTransaction.id)

                        transaction.id = updatedTransaction.id
                        transaction.date = updatedTransaction.date
                        transaction.amount = updatedTransaction.amount
                        transaction.reason = updatedTransaction.reason
                        transaction.periodic = updatedTransaction.periodic
                    })
                    .catch(e => this.errorMessage = e)
            },
            deleteTransaction(transaction) {
                client.delete(`transactions/${transaction.id}`)
                    .then(() => this.transactions = this.transactions.filter(t => t.id !== transaction.id))
                    .catch(e => this.errorMessage = e)
            }
        },
        created() {
            this.initiallyLoadData()
        }
    }
</script>

<style scoped>
    .error {
        background: #ff0000;
    }
</style>
