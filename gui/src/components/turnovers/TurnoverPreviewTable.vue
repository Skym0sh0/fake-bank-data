<template>
    <b-table v-if="categories"
             :striped="true"
             :hover="true"
             :items="value"
             :fields="fields"
             :responsive="true">
        <template #cell(Date)="row">
            {{ row.item.date }}
        </template>
        <template v-slot:cell(Amount)="row">
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
import {api} from "@/api/RegularIncomeAPI";
import {normalizeCategory} from "@/util/Normalizer";

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
    },
    data() {
        return {
            categories: null,
        }
    },
    computed: {
        fields() {
            return ["Date", "Recipient", "Amount", "Category", "SuggestedCategory", /*"Checksum",*/ "Description"];
        },
        isUnknownCategory() {
            return this.categories.reduce((a, v) => ({...a, [v.name]: v}))
        }
    },
    methods: {
        loadCategories() {
            return api.getCategories()
                .fetchCategoryTree()
                .then(res => {
                    this.categories = res
                })
        },
        onCreateCategory(categoryName) {
            const normalized = normalizeCategory({
                name: categoryName,
            });

            return api.getCategories()
                .postCategory(normalized)
                .then(() => {
                    return this.loadCategories()
                })
        },
        onCreateSuggestedCategory(categoryName) {
            this.onCreateCategory(categoryName)
                .then(() => {
                    const newlyCreatedCategory = this.categories.find(cat => cat.name === categoryName)
                    if (!newlyCreatedCategory)
                        return;

                    this.value.forEach(row => {
                        if (row.suggestedCategory === categoryName && !row.categoryId)
                            row.categoryId = newlyCreatedCategory.id;
                    })
                });
        }
    },
    mounted() {
        this.loadCategories()
    },
}
</script>

<style scoped>

</style>