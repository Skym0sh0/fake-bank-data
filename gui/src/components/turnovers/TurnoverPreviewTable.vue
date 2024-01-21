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
            <table-cell-monetary :value="row.item.amountInCents"/>
        </template>

        <template v-slot:cell(categoryId)="row">
            <category-input :id="`csv-category-input-${row.index}`"
                            v-model="row.item.categoryId"
                            @createCategory="onCreateCategory"
                            :options="categories"
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
            },
        }
    },
    computed: {
        fields() {
            const hasSuggestions = this.value.some(r => !!r.suggestedCategory)

            return [
                {
                    key: 'checksum',
                    label: '#',
                },
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
        categoriesByName() {
            return (this.categories || []).reduce((a, v) => ({...a, [v.name]: v}), {})
        },
        categoriesById() {
            return (this.categories || []).reduce((a, v) => ({...a, [v.id]: v}), {})
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
            return filterProp.onlyMissingCategories ? !row.categoryId : true;
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
