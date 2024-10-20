<script setup lang="ts">
import {TurnoverRow, TurnoverRowPatch} from "@api/api.ts";
import {computed} from "vue";
import {LookupById} from "../../misc/categoryHelpers.ts";
import TableCellMonetary from "../../misc/TableCellMonetary.vue";
import CategoryInput from "../../misc/CategoryInput.vue";
import type {VDataTable} from "vuetify/components";
import {DateTime} from "luxon";
import TableCellDescription from "../../misc/TableCellDescription.vue";

const {rows, touchedRowsIdsById, deletedRowsIdsById} = defineProps<{
  rows: TurnoverRow[];
  touchedRowsIdsById: LookupById<TurnoverRowPatch>;
  deletedRowsIdsById: LookupById<string>;
}>();

const emit = defineEmits<{
  (e: 'deleteTurnover', row: TurnoverRow): void;
  (e: 'undoDeleteTurnover', row: TurnoverRow): void;
}>();

// see https://stackoverflow.com/a/75993081
type ReadonlyHeaders = VDataTable['$props']['headers']
type UnwrapReadonlyArray<A> = A extends Readonly<Array<infer I>> ? I : never;
type ReadonlyDataTableHeader = UnwrapReadonlyArray<ReadonlyHeaders>;

const columns = computed<ReadonlyDataTableHeader[]>(() => {
  return [
    {
      key: "id",
      title: "",
      width: '1em',
    },
    {
      key: "date",
      title: "Datum",
      value: it => it.date,
      sortable: true,
      width: '10em',
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
      width: '8em',
    },
    {
      key: "categoryId",
      title: "Kategorie",
      value: it => it.categoryId,
      width: '24em',
    },
    {
      key: "description",
      title: "Beschreibung",
      value: it => it.description,
      sortable: true,
    },
    {
      key: "actions",
      title: "Actions",
      width: '1em',
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
      <v-icon v-if="deletedRowsIdsById[value]">
        mdi-delete-variant
      </v-icon>

      <v-icon v-else-if="touchedRowsIdsById[value]">
        mdi-delta
      </v-icon>
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
                      @input="newCategoryId => row.item.categoryId = newCategoryId ?? undefined"/>
    </template>

    <template v-slot:item.description="{ index, value }">
      <table-cell-description :index="index" :value="value"/>
    </template>

    <template v-slot:item.actions="row">
      <v-btn v-if="!deletedRowsIdsById[row.item.id]"
             size="small"
             icon="mdi-trash-can-outline"
             color="error"
             variant="text"
             @click="emit('deleteTurnover', row.item)"/>

      <v-btn v-if="deletedRowsIdsById[row.item.id]"
             size="small"
             icon="mdi-undo"
             color="info"
             variant="text"
             @click="$emit('undoDeleteTurnover', row.item)"/>
    </template>
  </v-data-table>
</template>
