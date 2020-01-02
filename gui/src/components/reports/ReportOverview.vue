<template>
    <div v-show="statements">
        <div class="chart">
            <raw-statements-report :statements="statements"/>
        </div>
    </div>
</template>

<script>
    import {api} from "../../api/RegularIncomeAPI";
    import RawStatementsReport from "./RawStatementsReport";

    export default {
        name: "ReportOverview",
        components: {
            RawStatementsReport,
        },
        data() {
            return {
                statements: null,
            }
        },
        mounted() {
            api.fetchStatementsReport()
                .then(res => this.statements = res.data)
                .catch(e => console.log(e))
        },
    }
</script>

<style scoped>
</style>