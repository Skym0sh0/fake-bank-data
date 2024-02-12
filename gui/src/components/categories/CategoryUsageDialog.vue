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
                 @hidden="reset"
                 @hide="checkToHide">
            <template v-slot:modal-footer>
                <div class="w-100 d-flex justify-content-between">
                    <b-btn-group>
                        <b-button v-if="!isEditing"
                                  @click="isEditing = true"
                                  variant="danger">
                            Bearbeiten
                        </b-button>
                    </b-btn-group>

                    <b-btn-group>
                        <b-button v-if="isEditing"
                                  @click="reset"
                                  variant="danger">
                            Abbrechen
                        </b-button>

                        <b-button v-if="isEditing"
                                  @click="save"
                                  :disabled="!changedTurnovers.length || !!currentLoadingRowId"
                                  variant="primary">
                            Speichern
                        </b-button>

                        <b-button v-if="!isEditing"
                                  @click="reset"
                                  variant="primary">
                            Ok
                        </b-button>
                    </b-btn-group>
                </div>
            </template>

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
                                        :loading="row.item.id === currentLoadingRowId"
                                        :flatted-categories="flattedCategories"
                                        :categories-by-id="categoriesById"
                                        :categories-by-name="categoriesByName"
                                        @createCategory="name => onCreateCategory(row.item.id, name)"/>
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
import {normalizeCategory} from "@/util/Normalizer";

export default {
    name: "CategoryUsageDialog",
    components: {
        CategoryInput,
        TableCellMonetary,
        TableCellDescription,
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
            isEditing: false,
            isLoading: false,
            referencedRows: null,
            originalValues: null,
            allCategories: null,
            currentLoadingRowId: null,
        }
    },
    methods: {
        onOpenModal() {
            this.loadData()

            this.$refs["modal-turnovers"].show();
        },
        reset() {
            this.isEditing = false
            this.isLoading = false
            this.referencedRows = null
            this.allCategories = null
            this.currentLoadingRowId = null

            this.$refs["modal-turnovers"].hide();
        },
        checkToHide(e) {
            const mustNotBeClosed = this.changedTurnovers.length || (
                this.isEditing && !(
                    e.trigger === null // abort button was pressed
                    || e.trigger === 'headerclose' // X Button in header was pressed
                ));
            if (mustNotBeClosed) {
                e.preventDefault()
            }
        },
        onCreateCategory(id, categoryName) {
            const normalized = normalizeCategory({
                name: categoryName,
            });

            this.currentLoadingRowId = id

            api.getCategories()
                .postCategory(normalized)
                .then(res => {
                    this.referencedRows.forEach(row => {
                        if (row.id === id)
                            row.categoryId = res.id;
                    })

                    this.allCategories.push(res)
                })
                .finally(() => this.currentLoadingRowId = null)
        },
        save() {
            this.isLoading = true

            api.getTurnovers().batchPatchTurnovers(this.changedTurnovers)
                .then(() => this.reset())
                .finally(() => this.isLoading = false)
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
                .then(rows => {
                    this.referencedRows = rows
                    this.originalValues = rows.map(this.rowToChangeObject)
                })
        },
        rowToChangeObject(row) {
            return {
                id: row.id,
                categoryId: row.categoryId,
            }
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
                this.isEditing ?
                    {
                        key: "newCategory",
                        label: "Neue Kategorie",
                    } : undefined,
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
        changedTurnovers() {
            const all = this.items.map(this.rowToChangeObject)
            return all.filter(
                row => !(this.originalValues || [])
                    .find(orig => orig.id === row.id && orig.categoryId === row.categoryId)
            )
        },
    },
}
</script>


<style scoped>

</style>
