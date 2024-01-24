<template>
    <b-table :striped="true"
             :hover="true"
             :bordered="true"
             :items="value"
             :fields="fields"
             :responsive="true"
             primary-key="checksum"
             :filter="filters"
             :filter-function="doFilter"
             :small="true"
             :foot-clone="value.length > 25"
             :tbody-tr-class="rowClass">

        <template v-slot:head(originalImportable)>
            <div class="d-flex justify-content-start align-items-center">
                <v-btn :icon="true"
                       :x-small="!filters.onlyImportables"
                       @click="filters.onlyImportables = !filters.onlyImportables">
                    <v-icon color="success">
                        {{ filters.onlyImportables ? 'mdi-filter-check' : 'mdi-filter' }}
                    </v-icon>
                </v-btn>
            </div>
        </template>

        <template v-slot:head(importable)>
            <b-checkbox @input="onSelectImportable" :checked="true"/>
        </template>
        <template v-slot:head(categoryId)="item">
            <div class="d-flex justify-content-between align-items-end">
                {{ item.label }}

                <v-btn :icon="true"
                       :x-small="true"
                       @click="filters.onlyMissingCategories = !filters.onlyMissingCategories">
                    <v-icon>
                        {{ filters.onlyMissingCategories ? 'mdi-filter-check' : 'mdi-filter' }}
                    </v-icon>
                </v-btn>
            </div>
        </template>

        <template v-slot:cell(checksum)="row">
            {{ row.index + 1 }}
        </template>

        <template v-slot:cell(importable)="row">
            <b-checkbox v-model="row.item.importable"
                        :disabled="!row.item.originalImportable"/>
        </template>

        <template v-slot:cell(amount)="row">
            <table-cell-monetary :value="row.item.amountInCents" class="float-right"/>
        </template>

        <template v-slot:cell(categoryId)="row">
            <category-input :id="`csv-category-input-${row.index}`"
                            v-model="row.item.categoryId"
                            @createCategory="onCreateCategory"
                            :flatted-categories="flattedCategories"
                            :categories-by-id="categoriesById"
                            :categories-by-name="categoriesByName"
                            :required="row.item.importable"
                            :disabled="!row.item.importable"/>
        </template>

        <template v-slot:cell(suggestedCategory)="row">
            <b-button v-if="row.item.suggestedCategory && !categoriesByName[row.item.suggestedCategory]"
                      size="sm"
                      @click="onCreateSuggestedCategory(row.item.suggestedCategory)"
                      :disabled="!row.item.importable"
                      class="p-0"
                      variant="info">
                <b-icon icon="plus" font-scale="1"/>
            </b-button>

            {{ row.item.suggestedCategory }}
        </template>

        <template v-slot:cell(Description)="row">
            <table-cell-description :index="row.index" :value="row.item.description"/>
        </template>
    </b-table>
</template>

<script>
import TableCellDescription from "@/components/turnovers/TableCellDescription.vue";
import TableCellMonetary from "@/components/turnovers/TableCellMonetary.vue";
import CategoryInput from "@/components/misc/CategoryInput.vue";
import {
    flatCategoryTreeWithParentChain,
    mapCategoriesById,
    mapCategoriesByName
} from "@/components/turnovers/category-helpers";

export default {
    name: "TurnoverPreviewTable",
    components: {
        CategoryInput,
        TableCellMonetary,
        TableCellDescription,
    },
    props: {
        value: {
            required: true,
            type: Array,
        },
        categories: {
            type: Array,
            required: true,
        },
    },
    data() {
        return {
            filters: {
                onlyMissingCategories: false,
                onlyImportables: true,
            },
        }
    },
    computed: {
        fields() {
            const hasSuggestions = this.value.some(r => !!r.suggestedCategory)
            const hasImportables = this.value.some(r => !!r.originalImportable)

            return [
                {
                    key: 'checksum',
                    label: '#',
                },
                hasImportables ?
                    {
                        key: "originalImportable",
                        label: "X",
                        sortable: true,
                        formatter: (importable) => {
                            return importable ? '' : 'X';
                        }
                    }
                    : undefined,
                {
                    key: "importable",
                    label: "?",
                    sortable: true,
                },
                {
                    key: "date",
                    label: "Date",
                    sortable: true,
                },
                {
                    key: "recipient",
                    label: "Empfänger",
                    sortable: true,
                },
                {
                    key: "amount",
                    label: "Money",
                    sortable: true,
                },
                {
                    key: "categoryId",
                    label: "Kategorie",
                    sortable: true,
                    sortByFormatted: true,
                    formatter: (id) => {
                        const cat = this.categoriesById[id]
                        if (!cat)
                            return id;
                        return cat.name;
                    },
                },
                hasSuggestions ? {
                    key: "suggestedCategory",
                    label: "Bank Vorschlag",
                    sortable: true,
                } : undefined,
                {
                    key: "Description"
                }
            ];
        },
        flattedCategories() {
            return flatCategoryTreeWithParentChain(this.categories, parents => parents.join(" > "));
        },
        categoriesByName() {
            return mapCategoriesByName(this.flattedCategories)
        },
        categoriesById() {
            return mapCategoriesById(this.flattedCategories)
        },
    },
    methods: {
        rowClass(item, type) {
            if (!item || type !== 'row')
                return;

            if (!item.importable)
                return 'table-secondary'

            if (!item.categoryId)
                return 'table-danger';

            return undefined;
        },
        doFilter(row, filterProp) {
            const importable = filterProp.onlyImportables ? row.originalImportable : true
            const missingCategory = filterProp.onlyMissingCategories ? !row.categoryId : true;

            return importable && missingCategory;
        },
        onCreateCategory(categoryName) {
            this.$emit("onCreateCategory", {
                categoryName: categoryName,
                callback: undefined
            })
        },
        onCreateSuggestedCategory(categoryName) {
            this.$emit("onCreateCategory", {
                categoryName: categoryName,
                callback: () => {
                    const newlyCreatedCategory = this.categories.find(cat => cat.name === categoryName)
                    if (!newlyCreatedCategory)
                        return;

                    this.value.forEach(row => {
                        if (row.suggestedCategory === categoryName && !row.categoryId)
                            row.categoryId = newlyCreatedCategory.id;
                    })
                }
            })
        },
        onSelectImportable(newValue) {
            this.value.forEach(row => {
                if (row.originalImportable)
                    row.importable = newValue;
            })
        },
    },
}
</script>

<style scoped>

</style>
