<template>
    <div class="transactions">
        <h1>Transaction Overview</h1>

        <AddTransaction v-on:add-transaction="addTransaction"/>

        <br/>

        <table>
            <tr>
                <th></th>
                <!--<th>ID</th>-->
                <th>Date</th>
                <th>Income</th>
                <th>Expenses</th>
                <th>Reason</th>
                <th>Periodic</th>
                <th>Actions</th>
            </tr>
            <tr v-bind:key="transaction.id" v-for="(transaction, idx) in sortedTransactions">
                <td>{{sortedTransactions.length - idx}}</td>
                <td>
                    <Datepicker v-model="transaction.date" format="yyyy-MM-dd"
                                @input="saveChange(transaction.id, 'date', transaction.date)"/>
                </td>
                <td class="income">
                    <input type="text" v-model="transaction.amount"
                           @change="saveChange(transaction.id, 'amount', transaction.amount)"/>
                </td>
                <td class="expenses">
                    <input type="text" v-model="transaction.amount"
                           @change="saveChange(transaction.id, '*amount', transaction.amount)"/>
                </td>
                <td>
                    <input type="text" v-model="transaction.reason"
                           @change="saveChange(transaction.id, 'reason', transaction.reason)"/>
                </td>
                <td>
                    <input type="checkbox" v-model="transaction.periodic"
                           @change="saveChange(transaction.id, 'periodic', transaction.periodic)"/>
                </td>
                <td>
                    <input type="button" class="del" value="X"
                           @click="$emit('delete-transaction', transaction)"/>
                </td>
            </tr>
        </table>
    </div>
</template>

<script>
    import Datepicker from 'vuejs-datepicker';
    import AddTransaction from "./AddTransaction";

    export default {
        name: 'TransactionOverview',
        props: ["transactions"],
        methods: {
            saveChange(id, field, newValue) {
                const update = {
                    id: id,
                    field: field,
                    value: newValue
                }

                this.$emit("update-transaction", update)
            },
            formatAmount(amount) {
                return `${parseFloat(amount / 100).toFixed(2)} €`
            },
            formatIncome(amount) {
                return amount < 0 ? '' : this.formatAmount(amount)
            },
            formatExpenses(amount) {
                return amount > 0 ? '' : this.formatAmount(amount)
            },
            addTransaction(newTransaction) {
                this.$emit("add-transaction", newTransaction)
            }
        },
        computed: {
            sortedTransactions() {
                return [...this.transactions].sort((a, b) => a.date <= b.date ? +1 : -1)
            }
        },
        components: {
            AddTransaction,
            Datepicker
        }
    }
</script>

<style scoped>
    .transactions {
        background: aliceblue;
    }

    table {
        width: 100%;
    }

    .income {
        color: green;
    }

    .expenses {
        color: red;
    }

    input[type=button] {
        display: inline-block;
        padding: 5px 12px;
        cursor: pointer;
    }

    input.save {
        background: aqua;
    }

    input.del {
        background: sandybrown;
    }

    input[type=button]:hover {
        background: #666;
    }
</style>
