<template>
    <b-container fluid>
        <b-form>
            <b-card>
                <h3>{{isNew ? 'Create' : 'Edit'}} Statement</h3>

                <b-card class="mb-2">
                    <h4>Statement Information</h4>

                    <b-row>
                        <b-col>
                            <b-form-group id="previous-statement-select-form-group"
                                          label-cols="4"
                                          label="Previous Bank Statement"
                                          label-for="previous-statement-select"
                                          horizontal>
                                <b-form-select id="previous-statement-select"
                                               :options="prevStatementOptions"
                                               v-model="statement.previousStatement"
                                               :state="!$v.statement.previousStatement.$invalid"
                                               class="statement-selection"/>
                            </b-form-group>
                        </b-col>

                        <b-col>
                            <b-form-group id="previous-statement-balance-input-form-group"
                                          label-cols="4"
                                          label="Previous Balance"
                                          label-for="previous-statement-balance-input"
                                          horizontal
                                          :description="formatBalance(statement.previousStatement.balance)">
                                <b-form-input id="previous-statement-balance-input"
                                              type="number"
                                              :formatter="formatBalance"
                                              v-model="statement.previousStatement.balance"
                                              disabled/>
                            </b-form-group>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col>
                            <b-form-group id="statement-date-input-form-group"
                                          label="Date"
                                          label-cols="4"
                                          label-for="statement-date-input"
                                          horizontal>
                                <b-input-group>
                                    <b-btn id="statement-date-input-btn-today"
                                           class="mr-2"
                                           size="sm"
                                           :variant="!$v.statement.date.$invalid ? 'success' : 'danger'"
                                           @click="setDateToToday">
                                        &#128198;
                                    </b-btn>
                                    <b-tooltip target="statement-date-input-btn-today">
                                        Set to Today
                                    </b-tooltip>

                                    <b-form-input id="statement-date-input"
                                                  type="date"
                                                  v-model="statement.date"
                                                  :state="!$v.statement.date.$invalid"/>
                                </b-input-group>
                            </b-form-group>
                        </b-col>

                        <b-col>
                            <b-form-group id="statement-balance-input-form-group"
                                          label-cols="4"
                                          label="Balance"
                                          label-for="statement-balance-input"
                                          horizontal
                                          :description="`In Euro: ${formatBalance(statement.balance)}`">
                                <b-form-input id="statement-balance-input"
                                              type="number"
                                              v-model="statement.balance"
                                              :formatter="inputMoneyFormat"
                                              :state="!$v.statement.balance.$invalid"/>
                            </b-form-group>
                        </b-col>
                    </b-row>
                </b-card>

                <b-card>
                    <h4>Transactions</h4>

                    <b-btn variant="primary"
                           class="mb-2"
                           @click="addNewTransaction(0)">
                        +
                    </b-btn>

                    <b-table bordered small striped sticky-header
                             :fields="['order', 'index', 'date', 'amount', 'periodic', 'reason', 'actions']"
                             :items="sortedTransactions">
                        <template v-slot:cell(order)="row">
                            <div v-if="false">
                                <b-btn class="mr-1"
                                       size="sm"
                                       variant="primary"
                                       @click="shiftTransaction(row.index, -1)"
                                       disabled>
                                    &#8593;
                                </b-btn>

                                <b-btn size="sm"
                                       variant="primary"
                                       @click="shiftTransaction(row.index, +1)"
                                       disabled>
                                    &#8595;
                                </b-btn>
                            </div>
                        </template>

                        <template v-slot:cell(index)="row">
                            <span>
                                {{row.index + 1}}
                            </span>
                        </template>

                        <template v-slot:cell(date)="row">
                            <b-form-input type="date"
                                          size="sm"
                                          :state="!!row.item.date"
                                          v-model="row.item.date"/>
                        </template>

                        <template v-slot:cell(amount)="row">
                            <b-form-input type="number"
                                          size="sm"
                                          :state="!!row.item.amount"
                                          v-model="row.item.amount"/>
                        </template>

                        <template v-slot:cell(periodic)="row">
                            <b-form-checkbox size="sm"
                                             v-model="row.item.isPeriodic"/>
                        </template>

                        <template v-slot:cell(reason)="row">
                            <b-form-input type="text"
                                          size="sm"
                                          :state="!!row.item.reason"
                                          v-model="row.item.reason"/>
                        </template>

                        <template v-slot:cell(actions)="row">
                            <span class="mr-2">
                                <b-btn size="sm"
                                       variant="primary"
                                       @click="addNewTransaction(row.index)">
                                    &#8593;+
                                </b-btn>
                                <b-btn size="sm"
                                       variant="primary"
                                       @click="addNewTransaction(row.index + 1)">
                                    &#8595;+
                                </b-btn>
                            </span>

                            <b-btn size="sm"
                                   variant="secondary"
                                   @click="deleteTransaction(row.index)">
                                &#128465;
                            </b-btn>
                        </template>
                    </b-table>
                </b-card>

                <b-row align-h="end" class="mt-2">
                    <b-col cols="auto">
                        <b-btn-group>
                            <b-btn variant="primary"
                                   @click="saveModel"
                                   :disabled="$v.statement.$invalid">
                                Save
                            </b-btn>
                            <b-btn variant="secondary"
                                   @click="abort">
                                Cancel
                            </b-btn>
                        </b-btn-group>
                    </b-col>
                </b-row>
            </b-card>
        </b-form>
    </b-container>
</template>

<script>
    import {api} from "../../api/RegularIncomeAPI"
    import {dateFormat, moneyFormat} from '../../util/Formatters'
    import moment from "moment";
    import {denormalizeStatement, normalizeStatement} from "../../util/Normalizer";
    import * as uuid from "uuid";
    import {required} from 'vuelidate/dist/validators.min'
    import {validationMixin} from 'vuelidate'

    export default {
        name: "StatementEntering",
        props: {
            id: {
                required: true,
                type: String,
            },
        },
        data() {
            return {
                previousStatementOptions: [],
                isNew: false,
                statement: {},
            }
        },
        validations: {
            statement: {
                previousStatement: {
                    required,
                },
                date: {
                    required,
                },
                balance: {
                    required,
                },
            }
        },
        methods: {
            loadStatements() {
                api.getAllStatements()
                    .then(res => {
                        this.previousStatementOptions = res.data.filter(stmt => this.isNew || stmt.id !== this.id)
                            .map(s => {
                                s.date = moment(s.date)
                                return s
                            })

                        this.previousStatementOptions.sort((a, b) => -a.date.diff(b.date))

                        if (this.previousStatementOptions.length > 0)
                            this.statement.previousStatement = this.previousStatementOptions[0]
                    })
            },
            saveModel() {
                const normalizedStatement = normalizeStatement(this.statement)

                api.postStatement(normalizedStatement)
                    .then(res => {
                        this.$router.replace({
                            name: "statement-edit",
                            params: {id: res.data.id},
                        })
                    })
                    .catch(e => {
                        // eslint-disable-next-line
                        console.log('Post failed', e)
                    })
            },
            abort() {
                this.$router.back()
            },
            inputMoneyFormat(value, event) {
                if (event.data === ',')
                    return value * 100

                return value
            },
            setDateToToday() {
                this.statement.date = dateFormat.formatIsoDate(moment())
            },
            addNewTransaction(index) {
                this.statement.transactions.splice(index, 0, {
                    id: uuid.v4(),
                    date: null,
                    amount: null,
                    isPeriodic: false,
                    reason: null,
                })
            },
            deleteTransaction(index) {
                this.statement.transactions.splice(index, 1)
            },
            shiftTransaction(index, offset) {
                const newIndex = index + offset

                // eslint-disable-next-line
                console.log('Swap', index, newIndex)
            },
            formatBalance(amount) {
                if (!amount)
                    return ""

                return moneyFormat.formatCents(amount)
            },
        },
        computed: {
            sortedTransactions() {
                return this.statement.transactions
            },
            prevStatementOptions() {
                return this.previousStatementOptions.map(stmt => {
                    const isBefore = (a, b) => a.diff(b) < 0
                    const indexIndicator = this.statement.date ? (isBefore(moment(this.statement.date), stmt.date) ? "\u2193" : "\u2191") : "#"

                    const text = `${indexIndicator} ${dateFormat.formatDate(stmt.date)} [${moneyFormat.formatCents(stmt.balance)}]`

                    return {
                        text: text,
                        value: stmt,
                        order: stmt.date.utc()
                    }
                })
            },
        },
        mounted() {
            this.loadStatements()
        },
        created() {
            this.isNew = this.$route.query.isNew || false

            this.$set(this, 'statement', {
                id: this.id,
                date: null,
                balance: null,
                previousStatement: {
                    date: null,
                    balance: null,
                },
                transactions: [],
            })

            if (!this.isNew) {
                api.readStatement(this.id)
                    .then(res => {
                        const svrStmt = denormalizeStatement(res.data)

                        this.$set(this.statement, 'date', svrStmt.date)
                        this.$set(this.statement, 'balance', svrStmt.balance)
                    })
            }
        },
        mixins: [
            validationMixin,
        ],
    }
</script>

<style scoped>
    .statement-selection {
        font-family: monospace;
    }
</style>
