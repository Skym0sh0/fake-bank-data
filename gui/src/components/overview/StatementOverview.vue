<template>
    <b-card>
        <h1>Statement Overview</h1>

        <b-btn @click="initiallyLoadData">Refresh</b-btn>

        <b-table striped hover responsive="sm"
                 id="bank-statements-table"
                 primary-key="id"
                 :fields="['index', 'date', 'balance', 'show_details']"
                 :items="statements">
            <template v-slot:cell(show_details)="row">
                <b-button :id="`bank-statement-details-btn-${row.item.id}`"
                          size="sm"
                          class="mr-2"
                          variant="info"
                          @click="row.toggleDetails">
                    {{row.detailsShowing ? 'Hide' : ' Show'}} Transactions
                </b-button>
            </template>

            <template v-slot:row-details="row">
                <statement-table-details :id="`bank-statement-details-${row.item.id}`"
                                         :bank-statement="row.item"/>
            </template>
        </b-table>
    </b-card>
</template>

<script>
    import {api} from "../../api/RegularIncomeAPI";
    import StatementTableDetails from "./StatementTableDetails";

    export default {
        name: "StatementOverview",
        components: {StatementTableDetails},
        data() {
            return {
                errorMessage: '',
                selectedIndex: null,
                statements: [],
            }
        },
        methods: {
            initiallyLoadData() {
                api.getAllStatements()
                    .then(res => {
                        this.statements = res.data.sort((a, b) => b.date.localeCompare(a.date)).map((val, idx) => {
                            val.index = idx
                            return val
                        })
                    })
                    .catch(e => this.errorMessage += e)
            },
        },
        created() {
            this.initiallyLoadData()
        }
    }
</script>

<style scoped>
</style>
