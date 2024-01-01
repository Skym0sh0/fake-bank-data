<template>
    <b-table :striped="true"
             :hover="true"
             :bordered="true"
             :items="value"
             :fields="fields"
             :responsive="true"
             primary-key="checksum"
             :small="true"
             :foot-clone="true">

        <template v-slot:cell(importable)="row">
            <b-checkbox v-model="row.item.importable"
                        :disabled="!row.item.originalImportable"/>
        </template>

        <template #cell(date)="row">
            {{ row.item.date }}
        </template>

        <template v-slot:cell(amount)="row">
            <table-cell-monetary :value="row.item.amountInCents"/>
        </template>

        <template v-slot:cell(Category)="row">
            <category-input :id="`csv-category-input-${row.index}`"
                            v-model="row.item.categoryId"
                            @createCategory="onCreateCategory"
                            :options="categories"
                            :state="true"/>
        </template>

        <template v-slot:cell(SuggestedCategory)="row">
            <b-button v-if="!isUnknownCategory[row.item.suggestedCategory]"
                      size="sm"
                      @click="onCreateSuggestedCategory(row.item.suggestedCategory)">
                +
            </b-button>
            {{ row.item.suggestedCategory }}
        </template>

        <template v-slot:cell(Description)="row">
            <table-cell-description :index="row.index" :value="row.item.description"/>
        </template>

        <template v-slot:cell(Recipient)="row">
            {{ row.item.recipient }}
        </template>

        <template v-slot:cell(Checksum)="row">
            {{ row.item.checksum }}
        </template>
    </b-table>
</template>

<script>
import TableCellDescription from "@/components/overview/utils/TableCellDescription.vue";
import TableCellMonetary from "@/components/overview/utils/TableCellMonetary.vue";
import CategoryInput from "@/components/enterings/CategoryInput.vue";

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
        return {}
    },
    computed: {
        fields() {
            return [
                {
                    key: "importable",
                    label: "?",
                    sortable: true,
                },
                {
                    key: "date",
                    label: "Date",
                    sortable: true
                },
                {
                    key: "Recipient"
                },
                {
                    key: "amount",
                    label: "Money",
                },
                {
                    key: "Category"
                },
                {
                    key: "SuggestedCategory",
                    label: "Suggested"
                },
                {
                    key: "Description"
                }
            ];
        },
        isUnknownCategory() {
            return (this.categories || []).reduce((a, v) => ({...a, [v.name]: v}), {})
        }
    },
    methods: {
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
        }
    },
}
</script>

<style scoped>

</style>