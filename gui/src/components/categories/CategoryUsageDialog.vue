<template>
    <div>
        <b-button id="usage-count"
                  variant="success"
                  :pill="true"
                  size="sm"
                  @click="onOpenModal"
                  v-b-tooltip.hover
                  :title="`Diese Kategorie wird in ${category.usageCount} Transaktionen benutzt.`">
            {{ category.usageCount }}
        </b-button>

        <b-modal ref="modal-turnovers"
                 :scrollable="true"
                 :centered="true"
                 title="Turnovers, die diese Kategorie verwenden"
                 :ok-only="true">
            <waiter :is-loading="!referencedRows">
                <b-table :striped="true"
                         :hover="true"
                         :responsive="true"
                         :small="true"
                         :bordered="false"
                         :items="items"
                         :fields="columns">
                    <template v-slot:cell(amountInCents)="row">
                        <table-cell-monetary :value="row.item.amountInCents" class="float-right"/>
                    </template>

                    <template v-slot:cell(description)="row">
                        <table-cell-description :index="row.index" :value="row.item.description"/>
                    </template>
                </b-table>
            </waiter>
        </b-modal>
    </div>
</template>

<script>

import {api} from "@/api/RegularIncomeAPI";
import Waiter from "@/components/misc/Waiter.vue";
import TableCellDescription from "@/components/turnovers/TableCellDescription.vue";
import TableCellMonetary from "@/components/turnovers/TableCellMonetary.vue";

export default {
    name: "CategoryUsageDialog",
    components: {TableCellMonetary, TableCellDescription, Waiter},
    props: {
        category: {
            type: Object,
            required: true,
        }
    },
    data() {
        return {
            referencedRows: null,
        }
    },
    methods: {
        onOpenModal() {
            this.loadReferencedRows()

            this.$refs["modal-turnovers"].show();
        },
        loadReferencedRows() {
            api.getTurnovers()
                .fetchTurnoversForCategory(this.category.id)
                .then(rows => this.referencedRows = rows)
        },
    },
    computed: {
        columns() {
            return [
                {
                    key: "date",
                    label: "Datum",
                    sortable: true,
                },
                {
                    key: "recipient",
                    label: "Empfänger",
                    sortable: true,
                },
                {
                    key: "amountInCents",
                    label: "Summe",
                    sortable: true,
                },
                {
                    key: "description",
                    label: "Beschreibung",
                    sortable: true,
                },
            ];
        },
        items() {
            return this.referencedRows || [];
        },
    },
}
</script>


<style scoped>

</style>