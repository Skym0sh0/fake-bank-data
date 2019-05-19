<template>
    <div>
        <p>Add new Transaction</p>

        <form>
            <datepicker class="datepicker" v-model="transaction.date" format="yyyy-MM-dd" :monday-first="true"
                        placeholder="Date"
                        :open-date="new Date()"/>

            <label for="amount">{{amountLabel}}</label>
            <input type="number" id="amount" v-model="transaction.amount" placeholder="Income/Expense..."/>

            <input type="checkbox" v-model="transaction.periodic" id="periodic"/>
            <label for="periodic">{{periodicLabel}}</label>

            <label for="reason">Reason</label>
            <input type="text" v-model="transaction.reason" id="reason" placeholder="Reason"/>

            <input type="submit" class="btn" @click="addNewTransaction" value="Add"/>
        </form>
    </div>
</template>

<script>
    import Datepicker from 'vuejs-datepicker';
    import uuid from "uuid";
    import moment from "moment";

    export default {
        name: "AddTransaction",
        data() {
            return {
                transaction: {},

            }
        },
        computed: {
            periodicLabel() {
                return this.transaction.periodic ? "Periodic" : "Unique"
            },
            amountLabel() {
                if (!this.transaction.amount || this.transaction.amount == 0)
                    return "Amount"

                return this.transaction.amount > 0 ? "Income" : "Expense"
            }
        },
        methods: {
            resetData() {
                this.transaction = {
                    id: uuid.v4(),
                    date: moment().add((Math.random() * 10) - 5, 'days').toDate(),
                    amount: ((Math.random() * 25000) - 12500).toFixed(2),
                    reason: "my example " + (Math.random() * 1000).toFixed(0),
                    periodic: Math.random() < 0.5
                }
            },
            addNewTransaction(e) {
                e.preventDefault()

                const transaction = {
                    id: this.transaction.id,
                    date: this.transaction.date,
                    amount: this.transaction.amount,
                    reason: this.transaction.reason,
                    periodic: this.transaction.periodic
                }

                this.$emit('add-transaction', transaction)

                this.resetData()
            }
        },
        mounted() {
            this.resetData()
        },
        components: {
            Datepicker
        }
    }
</script>

<style scoped>
    .datepicker {
        float: left;
        padding: 5px 5px;
    }

    input {
        padding: 5px 5px;
    }

    label {
        padding: 5px 5px;
    }

    .btn {
        display: inline-block;
        border: none;
        background: #aaaaaa;
        color: #fff;
        padding: 5px 20px;
        cursor: pointer;
    }

    .btn:hover {
        background: #666;
    }
</style>