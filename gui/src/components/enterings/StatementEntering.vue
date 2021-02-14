<template>
  <b-container fluid>
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
                               class="statement-selection"/>
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
                         @click="setDateToToday">
                    &#128198;
                  </b-btn>
                  <b-tooltip target="statement-date-input-btn-today">
                    Set to Today
                  </b-tooltip>

                  <b-form-input id="statement-date-input"
                                ref="statement-date-input"
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
                            horizontal>
                <monetary-input id="statement-balance-input"
                                v-model="statement.balance"
                                :state="!$v.statement.balance.$invalid"/>
              </b-form-group>
            </b-col>
          </b-row>
        </b-card>

        <b-card>
          <h4>Transactions</h4>

          <b-btn variant="primary"
                 class="mb-2"
                 @click="addNewTransaction(0)"
                 :disabled="!statement.previousStatement.id">
            +
          </b-btn>

          <b-table bordered small striped sticky-header
                   :fields="['index', 'date', 'amount', 'periodic', 'reason', 'actions']"
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
                            v-model="row.item.date"/>
            </template>

            <template v-slot:cell(amount)="row">
              <monetary-input :id="`transactions-table-input-amount-${row.index}`"
                              :ref="`transactions-table-input-amount-${row.index}`"
                              size="sm"
                              :state="!$v.statement.transactions.$each.$iter[row.index].amount.$invalid"
                              v-model="row.item.amount"
                              placeholder="Amount"/>
            </template>

            <template v-slot:cell(periodic)="row">
              <b-form-checkbox :id="`transactions-table-input-periodic-${row.index}`"
                               :ref="`transactions-table-input-periodic-${row.index}`"
                               size="sm"
                               :state="!$v.statement.transactions.$each.$iter[row.index].isPeriodic.$invalid"
                               v-model="row.item.isPeriodic"/>
            </template>

            <template v-slot:cell(reason)="row">
              <!-- <b-form-input :id="`transactions-table-input-reason-${row.index}`"
                             v-if="false"
                             :ref="`transactions-table-input-reason-${row.index}`"
                             type="text"
                             size="sm"
                             :list="`transactions-table-input-reason-datalist-${row.index}`"
                             :state="!$v.statement.transactions.$each.$iter[row.index].reason.$invalid"
                             v-model="row.item.reason"
                             placeholder="Reason"/>

               <b-datalist :id="`transactions-table-input-reason-datalist-${row.index}`"
                           :options="categories.map(c => c.name)"/>
                           -->

              <treeselect :id="`transactions-table-input-category-select-${row.index}`"
                          v-model="row.item.category"
                          :multiple="false"
                          :options="categories"
                          :normalizer="treeNodeNormalizer"
                          :open-on-focus="true"
                          :show-count="true"
                          :append-to-body="true"
                          value-format="id"
              />
            </template>

            <template v-slot:cell(actions)="row">
              <b-button-group size="sm" class="mr-2">
                <b-btn :id="`transactions-table-input-add-new-transaction-before-${row.index}`"
                       variant="primary"
                       @click="addNewTransaction(row.index)">
                  &#8593;+
                </b-btn>
                <b-btn :id="`transactions-table-input-add-new-transaction-after-${row.index}`"
                       variant="primary"
                       @click="addNewTransaction(row.index + 1)">
                  &#8595;+
                </b-btn>
              </b-button-group>

              <b-button-group size="sm">
                <b-btn :id="`transactions-table-input-delete-transaction-${row.index}`"
                       variant="secondary"
                       @click="deleteTransaction(row.index)">
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
                <b-tooltip target="transaction-sum-deviation" triggers="hover">
                  asfhjsdfhj
                </b-tooltip>
              </b-form-group>
            </b-col>
          </b-row>
        </b-card>

        <b-row align-h="end" class="mt-2">
          <b-col cols="auto" class="col-auto">
            <b-btn-group>
              <b-btn :variant="isExpectedTransactionSumMatching ? 'primary' : 'danger'"
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
import {api} from "@/api/RegularIncomeAPI"
import {dateFormat, moneyFormat} from '@/util/Formatters'
import moment from "moment";
import {normalizeStatement} from "@/util/Normalizer";
import * as uuid from "uuid";
import {integer, required} from 'vuelidate/dist/validators.min'
import {validationMixin} from 'vuelidate'
import MonetaryInput from "./MonetaryInput";
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'

export default {
  name: "StatementEntering",
  components: {
    MonetaryInput,
    Treeselect
  },
  props: {
    id: {
      required: true,
      type: String,
    },
  },
  data() {
    return {
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
          reason: {
            required,
          },
        },
      },
    }
  },
  methods: {
    treeNodeNormalizer(node) {
      const hasChildren = node => !!(node && node.subCategories && node.subCategories.length)

      return {
        id: node.id,
        label: node.name,
        children: hasChildren(node) ? node.subCategories : undefined,
      }
    },
    loadEntity() {
      if (!this.isNew) {
        api.readStatement(this.id)
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
      }
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
    loadReasons() {
      api.getCategories()
          .fetchCategoryTree()
          .then(res => {
            this.categories = res

            console.log(this.categories)
          })
    },
    loadOtherEntities() {
      this.loadReasons()
      return this.loadStatements()
    },
    saveModel() {
      const normalizedStatement = normalizeStatement(this.statement)

      api.postStatement(normalizedStatement)
          .then(res => {
            this.isNew = false

            this.$router.replace({
              name: "statement-edit",
              params: {id: res.data.id},
            }).catch(() => {
            })

            this.loadStatements()
                .then(() => this.loadEntity())
          })
          .catch(e => {
            // eslint-disable-next-line
            console.log('Post failed', e)
          })
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
        reason: null,
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
        return null

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
    this.loadOtherEntities()
        .then(() => this.loadEntity())

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
