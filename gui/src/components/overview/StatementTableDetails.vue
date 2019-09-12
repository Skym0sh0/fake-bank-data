<template>
    <div>
        <h5>Summary</h5>
        <b-table :id="`state-table-details-summary-table-${bankStatement.id}`"
                 :fields="['type', 'count', 'total', 'average', 'median', 'min', 'max']"
                 :items="summary">
            <template v-slot:cell(type)="row">
                <b>
                    {{row.item.type}}
                </b>
            </template>

            <template v-slot:cell(count)="row">
                <span>
                    {{row.value}}
                </span>
            </template>

            <template v-slot:cell()="row">
                <span>
                    {{formatBalance(row.value)}}
                </span>
            </template>
        </b-table>

        <h5>Transactions</h5>
        <b-table :id="`statement-table-details-transactions-table-${bankStatement.id}`"
                 :fields="['row', 'date', 'income', 'expense', 'periodic', 'reason']"
                 :items="transactions">
            <template v-slot:cell(row)="row">
                {{row.index + 1}}
            </template>

            <template v-slot:cell(date)="row">
                {{new Date(row.item.date).toLocaleDateString()}}
            </template>

            <template v-slot:cell(income)="row">
            <span v-if="row.item.amount >= 0" class="income">
                {{formatBalance(row.item.amount)}}
            </span>
            </template>

            <template v-slot:cell(expense)="row">
            <span v-if="row.item.amount < 0" class="expense">
                {{formatBalance(row.item.amount)}}
            </span>
            </template>

            <template v-slot:cell(periodic)="row">
            <span v-if="row.item.isPeriodic" class="periodic">
                &#10003;
            </span>
                <span v-else></span>
            </template>

            <template v-slot:cell(reason)="row">
            <span>
                {{row.item.reason}}
            </span>
            </template>
        </b-table>
    </div>
</template>

<script>
    import {api} from "../../api/RegularIncomeAPI"
    import {moneyFormat} from '../../util/Formatters'

    export default {
        name: "StatementTableDetails",
        props: {
            bankStatement: {
                required: true,
                type: Object,
            }
        },
        data() {
            return {
                transactions: [],
                summaries: {income: {}, expense: {}},
            }
        },
        methods: {
            loadData() {
                api.getStatementSummary(this.bankStatement.id)
                    .then(res => this.summaries = res.data)
                    .catch(e => {
                        // eslint-disable-next-line
                        console.log(e)
                    })

                api.getTransactionsForStatement(this.bankStatement.id)
                    .then(res => this.transactions = res.data)
                    .catch(e => {
                        // eslint-disable-next-line
                        console.log(e)
                    })
            },
            formatBalance(amount) {
                return moneyFormat.formatCents(amount)
            },
        },
        computed: {
            summary() {
                return [
                    {type: 'Incomes', ...this.summaries.income},
                    {type: 'Expenses', ...this.summaries.expense},
                    {type: 'Total', ...this.summaries.total},
                ]
            }
        },
        mounted() {
            this.loadData()
        }
    }
</script>

<style scoped>
    span.income {
        color: forestgreen;
    }

    span.expense {
        color: red;
    }

    span.periodic {
        color: darkblue;
    }
</style>