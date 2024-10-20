<script setup lang="ts">
import {computed, ref} from "vue";
import {PreviewRowWithOriginalState} from "./TurnoverImporting.vue";
import type {VDataTable} from "vuetify/components";
import TableCellDescription from "../../misc/TableCellDescription.vue";
import TableCellMonetary from "../../misc/TableCellMonetary.vue";
import CategoryInput from "../../misc/CategoryInput.vue";
import {DateTime} from "luxon";
import * as _ from "lodash";
import CategorySuggestion, {CategorySelectionType} from "./CategorySuggestion.vue";
import {useCategories} from "../../../store/use-categories.ts";

const {value, showAlreadyImportedRow} = defineProps<{
  value: PreviewRowWithOriginalState[];
  showAlreadyImportedRow?: boolean;
}>();

const categoriesStore = useCategories()

const searchString = ref("some-dummy-value-to-activate-table-filtering");
const showOnlyMissingCategories = ref(false);

const hasSuggestions = computed<boolean>(() => {
  return value.some(r => !!r.suggestedCategory);
});

enum MarkType {
  All,
  None,
  Some
}

const allRowsAreMarkedAsImportable = computed<MarkType>(() => {
  const onlyImportables = value.filter(it => it.originalImportable);

  const groups = _.groupBy(onlyImportables, it => it.importable)

  const trueGroup = groups['true'] ?? []
  const falseGroup = groups['false'] ?? []

  if (trueGroup.length === onlyImportables.length)
    return MarkType.All;
  if (falseGroup.length === onlyImportables.length)
    return MarkType.None;

  return MarkType.Some;
})

function doFilter(row: PreviewRowWithOriginalState): boolean {
  const importable = !showAlreadyImportedRow ? !!row.originalImportable : true
  const missingCategory = showOnlyMissingCategories.value ? !row.categoryId : true;

  return importable && missingCategory;
}

function onSelectImportable(newValue: boolean) {
  value.forEach(row => {
    if (row.originalImportable)
      row.importable = newValue;
  })
}

function onSelectCategory(select: CategorySelectionType) {
  value.filter(row => row.checksum === select.checksum)
    .forEach(row => row.categoryId = select.categoryId)
}

function onCreateSuggestedCategory(categoryName: string) {
  categoriesStore.createCategory(categoryName)
    .then(() => {
      const newlyCreatedCategory = categoriesStore.categoriesByName[categoryName]

      if (!newlyCreatedCategory)
        return;

      value.forEach(row => {
        if (row.suggestedCategory === categoryName && !row.categoryId)
          row.categoryId = newlyCreatedCategory.id;
      })
    })
}

// see https://stackoverflow.com/a/75993081
type ReadonlyHeaders = VDataTable['$props']['headers']
type UnwrapReadonlyArray<A> = A extends Readonly<Array<infer I>> ? I : never;
type ReadonlyDataTableHeader = UnwrapReadonlyArray<ReadonlyHeaders>;

const fields = computed<ReadonlyDataTableHeader[]>(() => {
  const columns: ReadonlyDataTableHeader[] = [
    {
      key: "importable",
      title: "?",
      value: it => it.importable,
      sortable: false,
      width: '3ex',
      minWidth: '40px',
    },
    {
      key: 'index',
      title: '#',
      value: it => it.index,
      sortable: true,
    },
    {
      key: "date",
      title: "Date",
      value: it => it.date,
      sortable: true,
      width: '10em',
    },
    {
      key: "recipient",
      title: "Empfänger",
      value: it => it.recipient,
      sortable: true,
      // width: '20em',
    },
    {
      key: "amount",
      title: "Betrag",
      value: it => it.amountInCents,
      sortable: true,
      width: '8em',
    },
    {
      key: "categoryId",
      title: "Kategorie",
      value: it => it.categoryId,
      sortable: true,
      width: '24em',
    },
    {
      key: "suggestedCategories",
      title: "Vorschläge",
      width: '20em',
    },
    {
      key: "Description",
      title: 'Beschreibung',
      value: it => it.description,
    }
  ];

  if (hasSuggestions.value)
    columns.splice(-2, 0, {
      key: "suggestedCategory",
      title: "Bank Vorschlag",
      sortable: true,
    })

  return columns;
})
</script>

<template>
  <v-data-table height="60vh"
                :fixedHeader="true"
                :items="value"
                :headers="fields"
                :search="searchString"
                :custom-filter="(_1, _2, row: any) => doFilter(row.raw)"
                :items-per-page="-1"
                :hide-default-footer="true"
                :hover="true"
                density="compact">

    <template v-slot:header.importable>
      <v-checkbox :model-value="allRowsAreMarkedAsImportable === MarkType.All"
                  :indeterminate="allRowsAreMarkedAsImportable === MarkType.Some ? true : undefined"
                  @update:modelValue="(x: any) => onSelectImportable(!!x)"
                  density="compact"
                  color="info"
                  :hide-details="true"/>
    </template>

    <template v-slot:item.importable="row">
      <v-checkbox v-model="row.item.importable"
                  :disabled="!row.item.originalImportable"
                  :indeterminate="!row.item.originalImportable"
                  title="Zeile wird importiert."
                  density="compact"
                  color="info"
                  :hide-details="true"/>
    </template>

    <template v-slot:header.categoryId="{column}">
      <div class="d-flex justify-space-between align-center">
        <span>{{ column.title }}</span>

        <v-btn :icon="showOnlyMissingCategories ? 'mdi-filter-check' : 'mdi-filter'"
               size="small"
               @click="showOnlyMissingCategories = !showOnlyMissingCategories"
               color="light"
               variant="flat"
               title="Filtert Zeilen mit schon befüllten Kategorien weg."/>
      </div>
    </template>
    <template v-slot:item.categoryId="row">
      <category-input :id="`csv-category-input-${row.index}`"
                      :value="row.item.categoryId"
                      @input="newId => row.item.categoryId = newId ?? undefined"
                      :required="row.item.importable"
                      :disabled="!row.item.importable"/>
    </template>

    <template v-slot:item.index="row">
      {{ row.index + 1 }}
    </template>

    <template v-slot:item.date="{ value }">
      <div class="float-right">
        {{ DateTime.fromISO(value).toLocaleString(DateTime.DATE_MED) }}
      </div>
    </template>

    <template v-slot:item.recipient="{index, value}">
      <table-cell-description :index="index"
                              :value="value"
                              :max-length="32"/>
    </template>

    <template v-slot:item.amount="{ value }">
      <table-cell-monetary :value="value" class="float-right"/>
    </template>

    <template v-slot:item.suggestedCategories="row">
      <category-suggestion v-if="categoriesStore.isInitialized"
                           :checksum="row.item.checksum"
                           :suggestions="row.item.suggestedCategories || []"
                           :categories-by-id="categoriesStore.categoriesById"
                           :disabled="!row.item.importable || categoriesStore.isLoading"
                           :selected-category-id="row.item.categoryId"
                           @select="onSelectCategory"/>
    </template>

    <template v-if="hasSuggestions" v-slot:item.suggestedCategory="row">
      <v-btn v-if="row.item.suggestedCategory && !categoriesStore.categoriesByName[row.item.suggestedCategory]"
             @click="onCreateSuggestedCategory(row.item.suggestedCategory)"
             :disabled="!row.item.importable"
             class="p-0"
             color="info"
             :icon="true">
        <v-icon>
          mdi-plus-circle
        </v-icon>
      </v-btn>

      {{ row.item.suggestedCategory }}
    </template>

    <template v-slot:item.Description="{index, value}">
      <table-cell-description :index="index" :value="value" :max-length="48"/>
    </template>
  </v-data-table>
</template>
