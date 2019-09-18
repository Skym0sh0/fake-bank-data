<template>
    <b-card>
        <h1>Statement Overview</h1>

        <b-row align-h="between">
            <b-col cols="1">
                <b-button class="mb-2"
                          v-b-toggle="'statement-file-upload-collapse'"
                          variant="primary">
                    {{true ? 'Upload' : 'Abort Upload'}}
                </b-button>
            </b-col>

            <b-col class="col-auto">
                <b-button-group>
                    <b-btn id="statement-overview-refresh"
                           class="mb-2"
                           variant="primary"
                           @click="initiallyLoadData">
                        Refresh
                    </b-btn>

                    <b-btn id="statement-overview-create-new-statement"
                           class="mb-2"
                           variant="secondary"
                           @click="toStatementDialog(null)">
                        Create
                    </b-btn>
                </b-button-group>
            </b-col>
        </b-row>

        <b-collapse id="statement-file-upload-collapse"
                    ref="statement-file-upload-collapse"
                    visible>
            <b-row>
                <b-col>
                    <statements-file-upload @uploadSucceeded="uploadSuccess"/>
                </b-col>
            </b-row>
        </b-collapse>

        <div class="mt-2">
            <b-table striped hover bordered small responsive="sm"
                     id="bank-statements-table"
                     primary-key="id"
                     :fields="['index', 'date', 'current_balance', 'actions']"
                     :items="statements">

                <template v-slot:cell(index)="row">
                    {{row.index + 1}}
                </template>

                <template v-slot:cell(date)="row">
                    {{new Date(row.item.date).toLocaleDateString()}}
                </template>

                <template v-slot:cell(current_balance)="row">
                    {{formatBalance(row.item.balance)}}
                </template>

                <template v-slot:cell(actions)="row">
                    <b-button :id="`bank-statement-details-btn-${row.item.id}`"
                              size="sm"
                              class="mr-2"
                              variant="info"
                              @click="row.toggleDetails">
                        {{row.detailsShowing ? 'Hide' : ' Show'}} Transactions
                    </b-button>

                    <b-button :id="`bank-statement-edit-btn-${row.item.id}`"
                              size="sm"
                              variant="success"
                              @click="toStatementDialog(row.item.id)">
                        Edit Transaction
                    </b-button>
                </template>

                <template v-slot:row-details="row">
                    <statement-table-details :id="`bank-statement-details-${row.item.id}`"
                                             :index="row.index"
                                             :bank-statement="row.item"/>
                </template>
            </b-table>
        </div>
    </b-card>
</template>

<script>
    import {api} from "../../api/RegularIncomeAPI"
    import StatementTableDetails from "./StatementTableDetails"
    import {moneyFormat} from '../../util/Formatters'
    import * as uuid from "uuid";
    import StatementsFileUpload from "./StatementsFileUpload";

    export default {
        name: "StatementOverview",
        components: {StatementsFileUpload, StatementTableDetails},
        data() {
            return {
                errorMessage: '',
                statements: [],
            }
        },
        methods: {
            initiallyLoadData() {
                api.getAllStatements()
                    .then(res => {
                        this.statements = res.sort((a, b) => b.date.localeCompare(a.date))
                    })
                    .catch(e => this.errorMessage += e)
            },
            formatBalance(amount) {
                return moneyFormat.formatCents(amount)
            },
            toStatementDialog(id) {
                const isNew = id == null
                const realId = id ? id : uuid.v4()

                this.$router.push({name: "statement-edit", params: {id: realId}, query: {isNew: isNew,}})
            },
            uploadSuccess() {
                this.initiallyLoadData()

                this.$refs['statement-file-upload-collapse'].toggle()
            },
        },
        created() {
            this.initiallyLoadData()
        }
    }
</script>

<style scoped>
</style>
