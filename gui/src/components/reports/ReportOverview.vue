<template>
    <v-card id="report-overview-root-card">
        <v-card v-for="(graph, idx) in allGraphs"
                :key="idx"
                :id="`report-overview-graph-${idx}`"
                class="ma-2">
            <v-skeleton-loader :id="`report-overview-graph-loader-${idx}`"
                               :loading="graph.loadingCondition()"
                               :height="chartHeight"
                               type="image@3">
                <keep-alive>
                    <component :is="graph.component"
                               v-bind="graph.props"
                               :height="chartHeight"/>
                </keep-alive>
            </v-skeleton-loader>
        </v-card>
    </v-card>
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
                chartHeight: 600,
            }
        },
        computed: {
            allGraphs() {
                return [
                    {
                        component: RawStatementsReport,
                        loadingCondition: () => !this.statements,
                        props: {statements: this.statements},
                    },
                    {
                        component: IncomeExpenseReport,
                        loadingCondition: () => !this.incomeExpenses,
                        props: {incomeExpenses: this.incomeExpenses},
                    },
                ]
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