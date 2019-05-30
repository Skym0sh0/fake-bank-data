<template>
    <div class="transactions">
        <h1>Transaction Overview</h1>

        <AddTransaction v-on:add-transaction="addTransaction"/>

        <br/>

        <table>
            <tr>
                <th>Actions</th>
                <th></th>
                <!--<th>ID</th>-->
                <th>Date</th>
                <th>Income</th>
                <th>Expenses</th>
                <th>Periodic</th>
                <th>Reason</th>
            </tr>
            <tr v-bind:key="transaction.id" v-for="(transaction, idx) in sortedTransactions">
                <td>
                    <span>
                        <span v-if="transaction.isDirty" class="save dirty">X</span>
                        <span v-else class="save clean">&#10003;</span>

                        <input type="button" class="del" value="X"
                               @click="$emit('delete-transaction', transaction)"/>
                    </span>
                </td>
                <td>{{sortedTransactions.length - idx}}</td>
                <td>
                    <Datepicker v-model="transaction.date" format="yyyy-MM-dd"
                                @input="setDirty(transaction); saveChange(transaction, 'date', transaction.date)"/>
                </td>
                <td class="income monetary amount">
                    <input class="monetary amount"
                           type="text" v-model="transaction.amount"
                           @keypress="setDirty(transaction)"
                           @change="saveChange(transaction, 'amount', transaction.amount)"
                           v-bind:disabled="transaction.amount < 0"/>
                </td>
                <td class="expenses">
                    <input class="monetary amount"
                           type="text" v-model="transaction.amount"
                           @keypress="setDirty(transaction)"
                           @change="saveChange(transaction, 'amount', transaction.amount)"
                           v-bind:disabled="transaction.amount >= 0"/>
                </td>
                <td>
                    <input type="checkbox" v-model="transaction.periodic"
                           @keypress="setDirty(transaction)"
                           @change="saveChange(transaction, 'periodic', transaction.periodic)"/>
                </td>
                <td>
                    <input type="text" v-model="transaction.reason"
                           @keypress="setDirty(transaction)"
                           @change="saveChange(transaction, 'reason', transaction.reason)"/>
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
            saveChange(transaction, field, newValue) {
                const update = {
                    id: transaction.id,
                    field: field,
                    value: newValue
                }

                this.$emit("update-transaction", update)

                transaction.isDirty = false
            },
            setDirty(transaction) {
                transaction.isDirty = true
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
        },
        mounted() {
            this.transactions.forEach(t => t.isDirty = false)
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

    span.save.dirty {
        background: indianred;
    }

    span.save.clean {
        background: limegreen;
    }

    input.del {
        background: sandybrown;
    }

    input[type=button]:hover {
        background: #666;
    }
</style>
