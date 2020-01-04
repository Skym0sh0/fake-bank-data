<template>
    <b-card>
        <b-card v-if="statements" class="mb-2">
            <div class="chart">
                <raw-statements-report :statements="statements"/>
            </div>
        </b-card>

        <b-card v-if="incomeExpenses">
            <div class="chart">
                <income-expense-report :incomeExpenses="incomeExpenses"/>
            </div>
        </b-card>
    </b-card>
</template>

<script>
    import {api} from "../../api/RegularIncomeAPI";
    import RawStatementsReport from "./RawStatementsReport";
    import IncomeExpenseReport from "./IncomeExpenseReport";

    export default {
        name: "ReportOverview",
        components: {
            IncomeExpenseReport,
            RawStatementsReport,
        },
        data() {
            return {
                statements: null,
                incomeExpenses: null,
            }
        },
        mounted() {
            api.fetchStatementsReport()
                .then(res => this.statements = res.data)
                .catch(e => console.log(e))

            api.fetchIncomeExpenseReport()
                .then(res => this.incomeExpenses = res.data)
                .catch(e => console.log(e))
        },
    }
</script>

<style scoped>
</style>