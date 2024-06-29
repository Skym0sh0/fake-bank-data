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
                            {{ graph.title }}
                        </v-expansion-panel-header>

                        <v-expansion-panel-content :id="`report-overview-graph-panel-content-${graph.id}`" eager>
                            <keep-alive>
                                <component :id="`report-overview-graph-${graph.id}`"
                                           :is="graph.component"
                                           :height="chartHeight"/>
                            </keep-alive>
                        </v-expansion-panel-content>
                    </v-expansion-panel>
                </v-expansion-panels>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import BalanceProgressionReport from "./BalanceProgressionReport.vue";
import IncomeExpenseReport from "./IncomeExpenseReport.vue";

export default {
    name: "ReportOverview",
    components: {
        IncomeExpenseReport,
        BalanceProgressionReport,
    },
    data() {
        return {
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
        this.charts.push({
            title: 'Kontostand Entwicklung',
            component: BalanceProgressionReport,
        })

        this.charts.push({
            title: 'Einkommen und Ausgaben',
            component: IncomeExpenseReport,
        })

        this.charts.forEach((chart, idx) => chart.id = idx)

        this.openCharts.push(...this.charts.map(c => c.id).filter((id, idx) => idx < 1))
    },
}
</script>

<style scoped>
</style>
