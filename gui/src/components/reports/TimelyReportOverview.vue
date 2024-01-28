<template>
    <v-card>
        <v-card-title>
            Timely Reports
        </v-card-title>

        <v-card-subtitle v-if="isDataQueryable">
            <v-row>
                <v-col :cols="4" class="py-0 d-flex justify-content-around align-items-center">
                    <div>
                        Transaktionen: {{ basicInfo.numberOfTransactions }}
                    </div>
                    <div>
                        Kategorien: {{ basicInfo.numberOfUsedCategories }}
                    </div>
                </v-col>

                <v-col :cols="8" class="py-0">
                    <timebox-selector v-if="basicInfo"
                                      v-model="select"
                                      :earliest="basicInfo.earliest"
                                      :latest="basicInfo.latest"
                                      :max-depth="basicInfo.maxDepth"
                    />
                </v-col>
            </v-row>
        </v-card-subtitle>

        <v-card-text>
            <income-expense-sankey-report v-if="isDataQueryable"
                                          :height="800"
                                          :select="select"/>

            <div v-if="!isDataQueryable">
                No data present
            </div>
        </v-card-text>

        <waiting-indicator :is-loading="isLoading"/>
    </v-card>
</template>

<script>
import {api} from "@/api/RegularIncomeAPI";
import IncomeExpenseSankeyReport from "@/components/reports/IncomeExpenseSankeyReport.vue";
import TimeboxSelector from "@/components/reports/TimeboxSelector.vue";
import WaitingIndicator from "@/components/misc/WaitingIndicator.vue";
import moment from "moment";

export default {
    name: "TimelyReportOverview",
    components: {WaitingIndicator, TimeboxSelector, IncomeExpenseSankeyReport},
    data() {
        return {
            isLoading: false,
            basicInfo: null,
            select: {
                depth: null,
                year: null,
                month: null,
            },
        }
    },
    methods: {
        loadBasicInfo() {
            this.isLoading = true

            api.getReports().fetchBasicInfo()
                .then(res => {
                    this.basicInfo = {
                        earliest: moment(res.earliestTransaction),
                        latest: moment(res.latestTransaction),
                        maxDepth: Number(res.maxDepthOfCategories),
                        numberOfTransactions: res.numberOfTransactions,
                        numberOfUsedCategories: res.numberOfUsedCategories,
                    };

                    this.select.depth = Math.min(2, this.basicInfo.maxDepth)
                    this.select.year = this.basicInfo.latest.year()
                    this.select.month = null
                })
                .finally(() => this.isLoading = false)
        },
    },
    computed: {
        isDataQueryable() {
            return !!this.basicInfo && this.basicInfo.numberOfTransactions > 0 && !!this.basicInfo.earliest && !!this.basicInfo.latest;
        },
    },
    mounted() {
        this.loadBasicInfo()
    }
}
</script>


<style scoped>

</style>