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

        <b-modal ref="modal-turnovers"
                 :scrollable="true"
                 :centered="true"
                 :title="`Turnovers mit Kategorie ${category.name}`"
                 :ok-only="true">
            <waiter :is-loading="isLoading">
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

                    <template v-slot:cell(newCategory)="row">
                        <category-input :id="`${row.item.id}`"
                                        v-model="row.item.categoryId"
                                        :flatted-categories="flattedCategories"
                                        :categories-by-id="categoriesById"
                                        :categories-by-name="categoriesByName"/>
                    </template>
                </b-table>
            </waiter>
        </b-modal>
    </div>
</template>

<script>

import {api} from "@/api/RegularIncomeAPI";
import Waiter from "@/components/misc/Waiter.vue";
import TableCellDescription from "@/components/turnovers/table/TableCellDescription.vue";
import TableCellMonetary from "@/components/turnovers/table/TableCellMonetary.vue";
import CategoryInput from "@/components/misc/CategoryInput.vue";
import {
    flatCategoryTreeWithParentChain,
    mapCategoriesById,
    mapCategoriesByName
} from "@/components/turnovers/category-helpers";

export default {
    name: "CategoryUsageDialog",
    components: {CategoryInput, TableCellMonetary, TableCellDescription, Waiter},
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
            allCategories: null,
        }
    },
    methods: {
        onOpenModal() {
            this.loadData()

            this.$refs["modal-turnovers"].show();
        },
        loadData() {
            this.isLoading = true;

            Promise.all([this.loadCategories(), this.loadReferencedRows()])
                .finally(() => this.isLoading = false)
        },
        loadCategories() {
            this.allCategories = null

            return api.getCategories()
                .fetchCategoryTree()
                .then(cats => this.allCategories = cats)
        },
        loadReferencedRows() {
            this.referencedRows = null;

            return api.getTurnovers()
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
                {
                    key: "newCategory",
                    label: "Neue Kategorie",
                },
            ];
        },
        items() {
            return this.referencedRows || [];
        },
        flattedCategories() {
            return flatCategoryTreeWithParentChain(this.allCategories, parents => parents.join(" > "))
        },
        categoriesById() {
            return mapCategoriesById(this.flattedCategories)
        },
        categoriesByName() {
            return mapCategoriesByName(this.flattedCategories)
        },
    },
}
</script>


<style scoped>

</style>