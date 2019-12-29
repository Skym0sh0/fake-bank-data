<template>
    <div v-show="records">
        <div class="border chart">
            <c3-charts :records="records"/>
        </div>

        <div class="mb-2"/>

        <div class="border chart">
            <d3-charts :records="records"/>
        </div>
    </div>
</template>

<script>
    import D3Charts from "./d3Charts";
    import C3Charts from "./c3Charts";
    import {api} from "../../api/RegularIncomeAPI";

    export default {
        name: "ReportOverview",
        components: {
            C3Charts,
            D3Charts
        },
        data() {
            return {
                records: null,
            }
        },
        mounted() {
            api.fetchStatementsReport()
                .then(res => this.records = res.data)
                .catch(e => console.log(e))
        },
    }
</script>

<style scoped>
    .chart {
        width: 1200px;
        height: 400px;
    }
</style>