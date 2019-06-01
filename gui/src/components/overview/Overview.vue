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
    import TransactionOverview from "./TransactionOverview";
    import StatementOverview from "./StatementOverview";
    import {api} from "../../api/RegularIncomeAPI";

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
                api.getAllTransactions()
                    .then(res => this.transactions = res.data)
                    .catch(e => this.errorMessage += e)

                api.getAllStatements()
                    .then(res => this.statements = res.data)
                    .catch(e => this.errorMessage += e)
            },
            addTransaction(newTransaction) {
                api.createTransaction(newTransaction)
                    .then(res => this.transactions = [res.data, ...this.transactions])
                    .catch(e => this.errorMessage = e)
            },
            updateTransaction(update) {
                api.patchTransaction(update)
                    .then(res => {
                        const updatedTransaction = res.data

                        const transaction = this.transactions.find(t => t.id === updatedTransaction.id)

                        transaction.id = updatedTransaction.id
                        transaction.date = updatedTransaction.date
                        transaction.amountInCent = updatedTransaction.amountInCent
                        transaction.reason = updatedTransaction.reason
                        transaction.periodic = updatedTransaction.periodic
                    })
                    .catch(e => this.errorMessage = e)
            },
            deleteTransaction(transaction) {
                api.deleteTransaction(transaction.id)
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
