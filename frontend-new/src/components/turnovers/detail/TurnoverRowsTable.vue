<script setup lang="ts">
import {Category, TurnoverRow, TurnoverRowPatch} from "@api/api.ts";
import {computed} from "vue";
import {
  flatCategoryTreeWithParentChain,
  LookupById,
  mapCategoriesById,
  mapCategoriesByName
} from "../../misc/categoryHelpers.ts";
import TableCellMonetary from "../../misc/TableCellMonetary.vue";
import CategoryInput from "../../misc/CategoryInput.vue";
import type {VDataTable} from "vuetify/components";
import {DateTime} from "luxon";
import TableCellDescription from "../../misc/TableCellDescription.vue";

const {categories, rows, touchedRowsIdsById, deletedRowsIdsById} = defineProps<{
  categories: Category[];
  rows: TurnoverRow[];
  touchedRowsIdsById: LookupById<TurnoverRowPatch>;
  deletedRowsIdsById: LookupById<string>;
}>();

const emit = defineEmits<{
  (e: 'onCreateCategory', categoryName: string): void;
  (e: 'deleteTurnover', row: TurnoverRow): void;
  (e: 'undoDeleteTurnover', row: TurnoverRow): void;
}>();

const flattedCategories = computed(() => {
  return flatCategoryTreeWithParentChain(categories, parents => parents.join(" > "));
})

const categoriesByName = computed(() => {
  return mapCategoriesByName(flattedCategories.value)
})

const categoriesById = computed(() => {
  return mapCategoriesById(flattedCategories.value)
})

function onCreateCategory(categoryName: string) {
  emit("onCreateCategory", categoryName)
}

// see https://stackoverflow.com/a/75993081
type ReadonlyHeaders = VDataTable['$props']['headers']
type UnwrapReadonlyArray<A> = A extends Readonly<Array<infer I>> ? I : never;
type ReadonlyDataTableHeader = UnwrapReadonlyArray<ReadonlyHeaders>;

const columns = computed<ReadonlyDataTableHeader[]>(() => {
  return [
    {
      key: "id",
      title: "",
    },
    {
      key: "date",
      title: "Datum",
      value: it => it.date,
      sortable: true,
    },
    {
      key: "recipient",
      title: "Empfänger",
      value: it => it.recipient,
      sortable: true,
    },
    {
      key: "amountInCents",
      title: "Summe",
      value: it => it.amountInCents,
      sortable: true,
    },
    {
      key: "categoryId",
      title: "Kategorie",
      value: it => it.categoryId,
    },
    {
      key: "description",
      title: "Beschreibung",
      value: it => it.description,
      sortable: true,
    },
    {
      key: "actions",
      title: "Actions"
    }
  ];
})
</script>

<template>
  <v-data-table :items="rows"
                :headers="columns"
                :hover="true"
                :items-per-page="-1"
                :hide-default-footer="true"
                density="compact">

    <template v-slot:item.id="{ value }">
      {{ (touchedRowsIdsById[value] || deletedRowsIdsById[value]) ? '*' : ' ' }}
    </template>

    <template v-slot:item.date="{ value }">
      {{ DateTime.fromISO(value).toLocaleString(DateTime.DATE_MED) }}
    </template>

    <template v-slot:item.recipient="{ index, value }">
      <table-cell-description :index="index" :value="value" :max-length="32"/>
    </template>

    <template v-slot:item.amountInCents="{ value }">
      <table-cell-monetary :value="value" class="float-right"/>
    </template>

    <template v-slot:item.categoryId="row">
      <category-input :id="`category-input-${row.index}`"
                      :value="row.item.categoryId"
                      @input="newCategoryId => row.item.categoryId = newCategoryId ?? undefined"
                      @createCategory="onCreateCategory"
                      :flatted-categories="flattedCategories"
                      :categories-by-id="categoriesById"
                      :categories-by-name="categoriesByName"/>
    </template>

    <template v-slot:item.description="{ index, value }">
      <table-cell-description :index="index" :value="value"/>
    </template>

    <template v-slot:item.actions="row">
      <v-btn v-if="!deletedRowsIdsById[row.item.id]"
             :small="true"
             :icon="true"
             color="error"
             @click="emit('deleteTurnover', row.item)">
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
  </v-data-table>
</template>
