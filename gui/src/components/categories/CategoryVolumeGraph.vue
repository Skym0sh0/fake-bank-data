<template>
    <div>
        <slot name="button" :clickCallback="onOpenModal">
            <b-button id="usage-count"
                      variant="success"
                      :pill="true"
                      size="sm"
                      @click="onOpenModal"
                      v-b-tooltip.hover
                      :title="`Diese Kategorie wird in ${category.usageCount} Transaktionen benutzt.`">
                {{ category.usageCount }}
            </b-button>
        </slot>

        <b-modal ref="modal-turnovers-graph"
                 :scrollable="true"
                 :centered="true"
                 :title="`Turnovers Graph für Kategorie ${category.name}`"
                 :ok-only="true">
            <v-container :fluid="true" class="mw-100 p-0">
                <v-row>
                    <v-col>
                        <waiter :is-loading="isLoading">
                            <category-graph :data="graphData"/>
                        </waiter>
                    </v-col>
                </v-row>

                <v-row>
                    <v-col>
                        <v-select v-model="grouping"
                                  :items="groupingKeys"
                                  label="Zeitraum Gruppierung"/>
                    </v-col>

                    <v-col>
                        <v-switch v-model="includeSubcategories"
                                  label="Unterkategorien"
                                  :disabled="true"/>
                    </v-col>
                </v-row>
            </v-container>
        </b-modal>
    </div>
</template>

<script>
import Waiter from "@/components/misc/Waiter.vue";
import {api} from "@/api/RegularIncomeAPI";
import CategoryGraph from "@/components/categories/CategoryGraph.vue";

export default {
    name: "CategoryVolumeGraph",
    components: {
        CategoryGraph,
        Waiter
    },
    props: {
        category: {
            type: Object,
            required: true,
        }
    },
    data() {
        return {
            isLoading: false,
            referencedRows: null,
            grouping: "DAY",
            includeSubcategories: false,
        }
    },
    computed: {
        groupingKeys() {
            return [
                {value: "DAY", text: "Tag",},
                {value: "MONTH", text: "Monat",},
                {value: "YEAR", text: "Jahr",},
            ];
        },
        graphData() {
            return (this.referencedRows || []).map(rec => ({
                date: new Date(rec.date),
                value: rec.amountInCents / 100.0,
            }))
        },
    },
    watch: {
        grouping() {
            this.loadData()
        },
        includeSubcategories() {
            this.loadData()
        },
    },
    methods: {
        onOpenModal() {
            this.loadData()

            this.$refs["modal-turnovers-graph"].show();
        },
        loadData() {
            this.isLoading = true;

            Promise.all([this.loadReferencedRows()])
                .finally(() => this.isLoading = false)
        },
        loadReferencedRows() {
            this.referencedRows = null;

            return api.getTurnovers()
                .fetchTurnoversReportForCategory(this.category.id, this.grouping)
                .then(res => {
                    this.referencedRows = res.datapoints
                })
        },
    },
}
</script>

<style scoped>

</style>
