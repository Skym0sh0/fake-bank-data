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
            <waiter :is-loading="isLoading">
                <category-graph :data="graphData"/>
            </waiter>
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
        }
    },
    computed: {
        graphData() {
            return (this.referencedRows || []).map(rec => ({
                date: new Date(rec.date),
                value: rec.amountInCents / 100.0,
            }))
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
                .fetchTurnoversForCategory(this.category.id)
                .then(rows => {
                    this.referencedRows = rows
                })
        },
    },
}
</script>

<style scoped>

</style>
