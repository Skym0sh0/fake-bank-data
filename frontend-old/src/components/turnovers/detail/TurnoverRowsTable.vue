<template>
  <div>
    <b-table :striped="true"
             :hover="true"
             :responsive="true"
             :bordered="false"
             :items="rows"
             :fields="columns">
      <template v-slot:cell(amountInCents)="row">
        <table-cell-monetary :value="row.item.amountInCents" class="float-right"/>
      </template>

      <template v-slot:cell(categoryId)="row">
        <category-input :id="`category-input-${row.index}`"
                        v-model="row.item.categoryId"
                        @createCategory="onCreateCategory"
                        :flatted-categories="flattedCategories"
                        :categories-by-id="categoriesById"
                        :categories-by-name="categoriesByName"/>
      </template>

      <template v-slot:cell(description)="row">
        <table-cell-description :index="row.index" :value="row.item.description"/>
      </template>

      <template v-slot:cell(actions)="row">
        <v-btn v-if="!deletedRowsIdsById[row.item.id]"
               :small="true"
               :icon="true"
               color="error"
               @click="$emit('deleteTurnover', row.item)">
          <v-icon small>
            mdi-trash-can-outline
          </v-icon>
        </v-btn>

        <v-btn v-if="deletedRowsIdsById[row.item.id]"
               :small="true"
               :icon="true"
               color="info"
               @click="$emit('undoDeleteTurnover', row.item)">
          <v-icon small>
            mdi-undo
          </v-icon>
        </v-btn>
      </template>
    </b-table>
  </div>
</template>

<script>

import TableCellDescription from "@/components/turnovers/table/TableCellDescription.vue";
import TableCellMonetary from "@/components/turnovers/table/TableCellMonetary.vue";
import CategoryInput from "@/components/misc/CategoryInput.vue";
import {
  flatCategoryTreeWithParentChain,
  mapCategoriesById,
  mapCategoriesByName
} from "@/components/turnovers/category-helpers";

export default {
  name: "TurnoverRowsTable",
  components: {
    CategoryInput,
    TableCellMonetary,
    TableCellDescription
  },
  props: {
    categories: {
      type: Array,
      required: true,
    },
    rows: {
      type: Array,
      required: true,
    },
    touchedRowsIdsById: {
      type: Object,
      required: true,
    },
    deletedRowsIdsById: {
      type: Object,
      required: true,
    },
  },
  methods: {
    onCreateCategory(categoryName) {
      this.$emit("onCreateCategory", categoryName)
    },
  },
  computed: {
    columns() {
      return [
        {
          key: "id",
          label: "",
          formatter: (value) => (this.touchedRowsIdsById[value] || this.deletedRowsIdsById[value]) ? '*' : ' '
        },
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
          key: "categoryId",
          label: "Kategorie",
        },
        {
          key: "description",
          label: "Beschreibung",
          sortable: true,
        },
        {
          key: "actions",
          label: "Actions"
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
}
</script>


<style scoped>

</style>
