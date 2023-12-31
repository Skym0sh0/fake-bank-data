<template>
    <b-container fluid>
        <waiting-indicator :is-loading="isLoading"/>

        <b-form>
            <b-card>
                <h3>{{ isNew ? 'Create' : 'Edit' }} Statement</h3>

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
                                               class="statement-selection"
                                               :disabled="disabled"/>
                            </b-form-group>
                        </b-col>

                        <b-col>
                            <b-form-group id="previous-statement-balance-input-form-group"
                                          label-cols="4"
                                          label="Previous Balance"
                                          label-for="previous-statement-balance-input"
                                          horizontal>
                                <monetary-input id="previous-statement-balance-input"
                                                :state="true"
                                                :value="statement.previousStatement.balance"
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
                                           @click="setDateToToday"
                                           :disabled="disabled">
                                        &#128198;
                                    </b-btn>
                                    <b-tooltip target="statement-date-input-btn-today">
                                        Set to Today
                                    </b-tooltip>

                                    <b-form-input id="statement-date-input"
                                                  ref="statement-date-input"
                                                  type="date"
                                                  v-model="statement.date"
                                                  :state="!$v.statement.date.$invalid"
                                                  :disabled="disabled"/>
                                </b-input-group>
                            </b-form-group>
                        </b-col>

                        <b-col>
                            <b-form-group id="statement-balance-input-form-group"
                                          label-cols="4"
                                          label="Balance"
                                          label-for="statement-balance-input"
                                          horizontal>
                                <monetary-input id="statement-balance-input"
                                                v-model="statement.balance"
                                                :state="!$v.statement.balance.$invalid"
                                                :disabled="disabled"/>
                            </b-form-group>
                        </b-col>
                    </b-row>
                </b-card>

                <b-card>
                    <h4>Transactions</h4>

                    <b-btn variant="primary"
                           class="mb-2"
                           @click="addNewTransaction(0)"
                           :disabled="!statement.previousStatement.id || disabled">
                        +
                    </b-btn>

                    <b-table bordered small striped sticky-header
                             :fields="['index', 'date', 'amount', 'periodic', 'category', 'actions']"
                             :items="sortedTransactions">
                        <template v-slot:cell(index)="row">
                            <span :id="`transactions-table-index-${row.index}`">
                                {{ row.index + 1 }}
                            </span>
                        </template>

                        <template v-slot:cell(date)="row">
                            <b-form-input :id="`transactions-table-input-date-${row.index}`"
                                          :ref="`transactions-table-input-date-${row.index}`"
                                          type="date"
                                          size="sm"
                                          :state="!$v.statement.transactions.$each.$iter[row.index].date.$invalid"
                                          v-model="row.item.date"
                                          :disabled="disabled"/>
                        </template>

                        <template v-slot:cell(amount)="row">
                            <monetary-input :id="`transactions-table-input-amount-${row.index}`"
                                            :ref="`transactions-table-input-amount-${row.index}`"
                                            size="sm"
                                            :state="!$v.statement.transactions.$each.$iter[row.index].amount.$invalid"
                                            v-model="row.item.amount"
                                            placeholder="Amount"
                                            :disabled="disabled"/>
                        </template>

                        <template v-slot:cell(periodic)="row">
                            <b-form-checkbox :id="`transactions-table-input-periodic-${row.index}`"
                                             :ref="`transactions-table-input-periodic-${row.index}`"
                                             size="sm"
                                             :state="!$v.statement.transactions.$each.$iter[row.index].isPeriodic.$invalid"
                                             v-model="row.item.isPeriodic"
                                             :disabled="disabled"/>
                        </template>

                        <template v-slot:cell(category)="row">
                            <category-input v-if="categories && categories.length"
                                            :id="`transactions-table-input-category-select-${row.index}`"
                                            v-model="row.item.category"
                                            @createCategory="onCreateCategory"
                                            :options="categories"
                                            :state="!$v.statement.transactions.$each.$iter[row.index].category.$invalid"
                                            @input="onRowChange(row.index)"
                                            :disabled="disabled"
                            />
                        </template>

                        <template v-slot:cell(actions)="row">
                            <b-button-group size="sm">
                                <b-btn :id="`transactions-table-input-add-new-transaction-before-${row.index}`"
                                       variant="primary"
                                       @click="addNewTransaction(row.index)"
                                       :disabled="disabled">
                                    &#8593;+
                                </b-btn>
                                <b-btn :id="`transactions-table-input-add-new-transaction-after-${row.index}`"
                                       variant="primary"
                                       @click="addNewTransaction(row.index + 1)"
                                       :disabled="disabled">
                                    &#8595;+
                                </b-btn>

                                <b-btn :id="`transactions-table-input-delete-transaction-${row.index}`"
                                       variant="danger"
                                       @click="deleteTransaction(row.index)"
                                       class="ml-1"
                                       :disabled="disabled">
                                    &#128465;
                                </b-btn>
                            </b-button-group>
                        </template>
                    </b-table>

                    <b-row>
                        <b-col>
                            <b-form-group :label-cols="6"
                                          label-size="sm"
                                          label-for="expected-transaction-sum"
                                          label="Expected Sum"
                                          horizontal>
                                <b-form-input id="expected-transaction-sum"
                                              :value="formatBalance(calculateExpectedTransactionSum)"
                                              :state="!!calculateExpectedTransactionSum"
                                              size="sm"
                                              disabled/>
                            </b-form-group>
                        </b-col>
                        <b-col>
                            <b-form-group :label-cols="6"
                                          label-size="sm"
                                          label-for="actual-transaction-sum"
                                          label="Actual Sum"
                                          horizontal>
                                <b-form-input id="actual-transaction-sum"
                                              :value="formatBalance(calculateActualTransactionSum)"
                                              :state="!!calculateActualTransactionSum"
                                              size="sm"
                                              disabled/>
                            </b-form-group>
                        </b-col>

                        <b-col>
                            <b-form-group :label-cols="6"
                                          label-size="sm"
                                          label-for="transaction-sum-deviation"
                                          label="Deviation"
                                          horizontal>
                                <b-form-input id="transaction-sum-deviation"
                                              :value="formatBalance(calculateTransactionDeviation)"
                                              :state="isExpectedTransactionSumMatching"
                                              size="sm"
                                              disabled/>
                            </b-form-group>
                        </b-col>
                    </b-row>
                </b-card>

                <b-row align-h="end" class="mt-2">
                    <b-col cols="auto" class="col-auto">
                        <b-btn-group>
                            <b-btn :variant="isExpectedTransactionSumMatching ? 'primary' : 'danger'"
                                   @click="saveModel"
                                   :disabled="$v.statement.$invalid || disabled">
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
import {api} from "@/api/RegularIncomeAPI"
import {dateFormat, moneyFormat} from '@/util/Formatters'
import moment from "moment";
import {normalizeCategory, normalizeStatement} from "@/util/Normalizer";
import * as uuid from "uuid";
import {integer, required} from 'vuelidate/dist/validators.min'
import {validationMixin} from 'vuelidate'
import MonetaryInput from "./MonetaryInput";
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import CategoryInput from "@/components/enterings/CategoryInput.vue";
import WaitingIndicator from "@/components/misc/WaitingIndicator.vue";

export default {
    name: "StatementEntering",
    components: {
        WaitingIndicator,
        CategoryInput,
        MonetaryInput,
    },
    props: {
        id: {
            required: true,
            type: String,
        },
        disabled: {
            type: Boolean,
            default: true,
        },
    },
    data() {
        return {
            isLoading: false,
            previousStatementOptions: [],
            categories: [],
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
                integer,
            },
            transactions: {
                $each: {
                    date: {
                        required,
                    },
                    amount: {
                        required,
                        integer,
                        notNull: val => parseInt(val) !== 0,
                    },
                    isPeriodic: {
                        required,
                    },
                    category: {
                        required,
                    },
                },
            },
        }
    },
    methods: {
        onRowChange(rowIdx) {
            this.$v.statement.transactions.$each.$iter[rowIdx].$touch();
        },
        loadEntity() {
            if (this.isNew)
                return Promise.resolve();

            return api.readStatement(this.id)
                .then(svrStmt => {
                    this.statement.date = svrStmt.date
                    this.statement.balance = svrStmt.balance

                    if (svrStmt.previousStatement) {
                        const selected = this.previousStatementOptions.filter(stmt => stmt.id === svrStmt.previousStatement.id)

                        if (selected.length > 0)
                            this.statement.previousStatement = selected[0]
                    }

                    this.statement.transactions = svrStmt.transactions.sort((a, b) => {
                        return -moment.utc(a.date).diff(moment.utc(b.date))
                    })
                })
        },
        loadStatements() {
            return api.getAllStatements()
                .then(res => {
                    this.previousStatementOptions = res

                    this.previousStatementOptions.sort((a, b) => {
                        return -moment.utc(a.date).diff(moment.utc(b.date))
                    })

                    if (this.previousStatementOptions.length > 0)
                        this.statement.previousStatement = this.previousStatementOptions[0]

                    return this.previousStatementOptions
                })
        },
        loadCategories() {
            return api.getCategories()
                .fetchCategoryTree()
                .then(res => {
                    this.categories = res
                    return this.categories;
                })
        },
        onCreateCategory(categoryName) {
            const normalized = normalizeCategory({
                name: categoryName,
            });

            this.isLoading = true;

            return api.getCategories()
                .postCategory(normalized)
                .then(() => this.loadCategories())
                .finally(() => this.isLoading = false)
        },
        saveModel() {
            const normalizedStatement = normalizeStatement(this.statement)

            this.isLoading = true;

            return api.postStatement(normalizedStatement)
                .then(res => {
                    this.isNew = false

                    this.$router.replace({
                        name: "statement-edit",
                        params: {id: res.data.id},
                    }).catch(() => {
                    })

                    return this.loadStatements()
                        .then(() => this.loadEntity())
                })
                .catch(e => {
                    // eslint-disable-next-line
                    console.log('Post failed', e)
                })
                .finally(() => this.isLoading = false)
        },
        abort() {
            this.$router.back()
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
                category: null,
            })

            this.$nextTick(() => this.$refs[`transactions-table-input-date-${index}`].focus())
        },
        deleteTransaction(index) {
            this.statement.transactions.splice(index, 1)
        },
        formatBalance(amount) {
            return moneyFormat.formatCents(amount)
        },
    },
    computed: {
        sortedTransactions() {
            return this.statement.transactions
        },
        prevStatementOptions() {
            const isSameID = stmt => stmt.id === this.id
            const isSuccessorID = stmt => stmt.previousStatement && stmt.previousStatement.id === this.id

            return this.previousStatementOptions.map(stmt => {
                const isBefore = (a, b) => a.diff(b) < 0
                const indexIndicator = this.statement.date ? (isBefore(moment(this.statement.date), stmt.date) ? "\u2193" : "\u2191") : "#"

                const text = `${indexIndicator} ${dateFormat.formatDate(stmt.date)} [${moneyFormat.formatCents(stmt.balance)}]`

                const isNew = this.isNew
                const isSame = isSameID(stmt)
                const isSuccessor = isSuccessorID(stmt)

                const isNotSameOrSuccessor = !isSame && !isSuccessor

                return {
                    text: text,
                    value: stmt,
                    order: moment.utc(stmt.date).utc(),
                    disabled: !(isNew || isNotSameOrSuccessor),
                }
            })
        },
        calculateExpectedTransactionSum() {
            if (!this.statement.balance || !this.statement.previousStatement)
                return 0

            return this.statement.balance - this.statement.previousStatement.balance
        },
        calculateActualTransactionSum() {
            return this.statement.transactions.map(t => t.amount)
                .filter(x => !!x)
                .map(x => parseInt(x))
                .reduce((a, b) => a + b, 0)
        },
        calculateTransactionDeviation() {
            return this.calculateExpectedTransactionSum - this.calculateActualTransactionSum
        },
        isExpectedTransactionSumMatching() {
            return this.calculateTransactionDeviation === 0
        },
    },
    mounted() {
        this.isLoading = true;
        Promise.all([this.loadCategories(), this.loadStatements()])
            .then(() => this.loadEntity())
            .finally(() => this.isLoading = false)

        this.$refs['statement-date-input'].focus()
    },
    created() {
        this.isNew = (this.$route.query.isNew && JSON.parse(this.$route.query.isNew)) || false

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
