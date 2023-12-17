<template>
    <div>PREVIEW !

        <div>
            <b-table :striped="true"
                     :hover="true"
                     :items="sortedRows"
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
                                    :state="true"
                                    :options="categories"
                                    :value="row.item.categoryId"/>
                </template>
                <template v-slot:cell(SuggestedCategory)="row">
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
        </div>
    </div>
</template>

<script>
import _ from 'lodash';
import TableCellDescription from "@/components/overview/utils/TableCellDescription.vue";
import TableCellMonetary from "@/components/overview/utils/TableCellMonetary.vue";
import CategoryInput from "@/components/enterings/CategoryInput.vue";
import {api} from "@/api/RegularIncomeAPI";

export default {
    name: "PreviewData",
    components: {CategoryInput, TableCellMonetary, TableCellDescription},
    props: {
        rows: {
            required: true,
            type: Array,
        },
    },
    data() {
        return {
            categories: [],
        }
    },
    computed: {
        fields() {
            return ["Date", "Recipient", "Amount", "Category", "SuggestedCategory", "Checksum", "Description"];
        },
        sortedRows() {
            const data = this.rows.filter((tmp, idx) => idx < 10);

            return _.reverse(_.sortBy(data, row => row.date));
        }
    },
    methods: {
        loadCategories() {
            api.getCategories()
                .fetchCategoryTree()
                .then(res => {
                    this.categories = res
                })
        },
    },
    mounted() {
        this.loadCategories()
    },
}
</script>

<style scoped>

</style>