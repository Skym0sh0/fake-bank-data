<template>
    <div>
        <waiting-indicator :is-loading="isLoading"/>

        <b-card>
            <h1>Statement Overview</h1>

            <b-row align-h="between">
                <b-col cols="1">
                    <legacy-xls-upload v-if="statements.length === 0 && !isLoading"
                                       @uploadSucceeded="uploadSuccess"
                                       :disabled="disabled"/>
                </b-col>

                <b-col class="col-auto">
                    <b-button-group>
                        <b-btn id="statement-overview-refresh"
                               class="mb-2"
                               variant="secondary"
                               @click="initiallyLoadData">
                            Refresh
                        </b-btn>

                        <b-btn id="statement-overview-create-new-statement"
                               class="mb-2"
                               variant="primary"
                               @click="toStatementDialog(null)"
                               :disabled="disabled">
                            Create
                        </b-btn>
                    </b-button-group>
                </b-col>
            </b-row>

            <div class="mt-2">
                <b-table striped hover bordered small responsive="sm"
                         id="bank-statements-table"
                         primary-key="id"
                         :fields="['index', 'date', 'current_balance', 'volume', 'difference', 'actions']"
                         :items="getStatements">

                    <template v-slot:cell(index)="row">
                        {{ row.index + 1 }}
                    </template>

                    <template v-slot:cell(date)="row">
                        {{ formatDate(row.item.date) }}
                    </template>

                    <template v-slot:cell(current_balance)="row">
                        {{ formatBalance(row.item.balance) }}
                    </template>

                    <template v-slot:cell(volume)="row">
                        {{ formatBalance(row.item.volume) }}
                    </template>

                    <template v-slot:cell(difference)="row">
                        <div v-if="row.item.hasDeviation">
                            {{ formatBalance(row.item.deviation) }}
                        </div>
                    </template>

                    <template v-slot:cell(actions)="row">
                        <b-button-group size="sm">
                            <b-button :id="`bank-statement-details-btn-${row.item.id}`"
                                      class="mr-2"
                                      variant="info"
                                      @click="row.toggleDetails">
                                {{ row.detailsShowing ? 'Hide' : ' Expand' }}
                            </b-button>

                            <b-button :id="`bank-statement-edit-btn-${row.item.id}`"
                                      class="mr-2"
                                      variant="success"
                                      @click="toStatementDialog(row.item.id)">
                                Edit
                            </b-button>

                            <b-button :id="`bank-statement-delete-btn-${row.item.id}`"
                                      variant="danger"
                                      :disabled="row.index !== 0 || disabled"
                                      @click="$bvModal.show(`bank-statement-delete-modal-${row.item.id}`)">
                                Delete
                            </b-button>

                            <b-modal :id="`bank-statement-delete-modal-${row.item.id}`"
                                     @ok="deleteStatement(row.item.id)"
                                     title="Confirm Statement Deletion">
                                <div>
                                    Do you really want to delete the Statement
                                    '<span class="confirm-date">{{ formatDate(row.item.date) }}</span>' ?
                                </div>
                            </b-modal>
                        </b-button-group>
                    </template>

                    <template v-slot:row-details="row">
                        <statement-table-details :id="`bank-statement-details-${row.item.id}`"
                                                 :index="row.index"
                                                 :bank-statement="row.item"/>
                    </template>
                </b-table>
            </div>
        </b-card>
    </div>
</template>

<script>
import {api} from "../../api/RegularIncomeAPI"
import StatementTableDetails from "./StatementTableDetails.vue"
import {dateFormat, moneyFormat} from '../../util/Formatters'
import * as uuid from "uuid";
import LegacyXlsUpload from "@/components/enterings/LegacyXlsUpload.vue";
import WaitingIndicator from "@/components/misc/WaitingIndicator.vue";

export default {
    name: "StatementOverview",
    components: {
        WaitingIndicator,
        LegacyXlsUpload,
        StatementTableDetails
    },
    props: {
        disabled: {
            type: Boolean,
            default: true,
        },
    },
    data() {
        return {
            errorMessage: '',
            isLoading: false,
            statements: [],
        }
    },
    computed: {
        getStatements() {
            return this.statements.map(stmt => {
                return {
                    ...stmt,
                    deviation: this.calculateStatementDeviation(stmt),
                    hasDeviation: this.calculateStatementDeviation(stmt) !== 0,
                    _cellVariants: {
                        balance: stmt.balance < 0 ? 'danger' : null,
                        volume: stmt.volume < 0 ? 'warning' : null,
                        difference: this.calculateStatementDeviation(stmt) !== 0 ? 'danger' : null,
                    },
                }
            })
        },
    },
    methods: {
        initiallyLoadData() {
            this.isLoading = true;

            return api.getAllStatements()
                .then(res => {
                    this.statements = res
                })
                .catch(e => this.errorMessage += e)
                .finally(() => this.isLoading = false)
        },
        formatBalance(amount) {
            return moneyFormat.formatCents(amount)
        },
        formatDate(date) {
            return dateFormat.formatIsoDate(date)
        },
        toStatementDialog(id) {
            const isNew = id == null
            const realId = id ? id : uuid.v4()

            this.$router.push({name: "statement-edit", params: {id: realId}, query: {isNew: isNew,}})
        },
        deleteStatement(id) {
            this.isLoading = true;

            return api.deleteStatement(id)
                .then(() => this.initiallyLoadData())
                .finally(() => this.isLoading = false)
        },
        uploadSuccess() {
            return this.initiallyLoadData()
        },
        calculateStatementDeviation(stmt) {
            const prev = (stmt.previousStatement && stmt.previousStatement.balance) || 0

            return stmt.balance - (prev + stmt.volume)
        }
    },
    created() {
        this.initiallyLoadData()
    }
}
</script>

<style scoped>
.confirm-date {
    color: red;
}

.error-text {
    color: red;
}
</style>
