<template>
    <v-container fluid>
        <v-row justify="end">
            <v-col :cols="3">
                <v-item-group>
                    <v-item>
                        <v-btn :disabled="areAllChartsOpen"
                               color="secondary"
                               @click="showAll()" x-small>
                            Show all
                        </v-btn>
                    </v-item>
                    <v-item>
                        <v-btn :disabled="areNoChartsOpen"
                               color="accent"
                               @click="hideAll()" x-small>
                            Show none
                        </v-btn>
                    </v-item>
                </v-item-group>
            </v-col>
        </v-row>

        <v-row>
            <v-col>
                <v-expansion-panels id="report-overview-chart-panels"
                                    v-model="openCharts"
                                    focusable multiple
                                    class="pa-2">
                    <v-expansion-panel v-for="graph in charts"
                                       :key="graph.id"
                                       :id="`report-overview-graph-panel-${graph.id}`"
                                       class="ma-2">

                        <v-expansion-panel-header :id="`report-overview-graph-panel-header-${graph.id}`">
                            {{graph.title}}
                        </v-expansion-panel-header>

                        <v-expansion-panel-content :id="`report-overview-graph-panel-content-${graph.id}`" eager>
                            <keep-alive>
                                <v-skeleton-loader :id="`report-overview-graph-loader-${graph.id}`"
                                                   :loading="graph.loadingCondition()"
                                                   :height="chartHeight"
                                                   type="image@3">
                                    <component :id="`report-overview-graph-${graph.id}`"
                                               :is="graph.component"
                                               v-bind="graph.props"
                                               :height="chartHeight"/>
                                </v-skeleton-loader>
                            </keep-alive>
                        </v-expansion-panel-content>
                    </v-expansion-panel>
                </v-expansion-panels>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
    import {api} from "../../api/RegularIncomeAPI";
    import RawStatementsReport from "./RawStatementsReport";
    import IncomeExpenseReport from "./IncomeExpenseReport";
    import IncomeExpenseSankeyReport from "@/components/reports/IncomeExpenseSankeyReport.vue";

    export default {
        name: "ReportOverview",
        components: {
            IncomeExpenseReport,
            RawStatementsReport,
        },
        data() {
            return {
                statements: [],
                incomeExpenses: [],
                incomeExpensesSankey: [],
                chartHeight: 800,
                openCharts: [],
                charts: [],
            }
        },
        methods: {
            showAll() {
                this.hideAll()
                this.openCharts.push(...this.charts.map(c => c.id))
            },
            hideAll() {
                this.openCharts.splice(0, this.openCharts.length)
            },
        },
        computed: {
            areAllChartsOpen() {
                return this.openCharts.length === this.charts.length
            },
            areNoChartsOpen() {
                return this.openCharts.length === 0
            },
        },
        mounted() {
            api.getReports().fetchStatementsReport()
                .then(res => {
                    this.statements.splice(0, this.statements.length)
                    return this.statements.push(...res.data);
                })
                .catch(e => console.log(e))

            api.getReports().fetchIncomeExpenseReport()
                .then(res => {
                    this.incomeExpenses.splice(0, this.incomeExpenses.length)
                    return this.incomeExpenses.push(...res.data);
                })
                .catch(e => console.log(e))

            api.getReports().fetchIncomeExpenseFlowReport()
                .then(res => {
                    this.incomeExpensesSankey.splice(0, this.incomeExpensesSankey.length)
                    return this.incomeExpensesSankey.push(...res.flows)
                })
                .catch(e => console.log(e))
        },
        created() {
            this.charts.push({
                title: 'Bank Statement Report',
                component: RawStatementsReport,
                loadingCondition: () => !this.statements,
                props: {statements: this.statements},
            })

            this.charts.push({
                title: 'Income & Expense Report',
                component: IncomeExpenseReport,
                loadingCondition: () => !this.incomeExpenses,
                props: {incomeExpenses: this.incomeExpenses},
            })

            this.charts.push({
                title: 'Income & Expense Sankey Report',
                component: IncomeExpenseSankeyReport,
                loadingCondition: () => !this.incomeExpensesSankey,
                props: {incomeExpensesSankey: this.incomeExpensesSankey},
            })

            this.charts.forEach((chart, idx) => chart.id = idx)

            this.openCharts.push(...this.charts.map(c => c.id).filter((id, idx) => idx < 1))
        },
    }
</script>

<style scoped>
</style>
