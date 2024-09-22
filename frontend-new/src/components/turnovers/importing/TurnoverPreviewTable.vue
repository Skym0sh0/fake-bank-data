<script setup lang="ts">
import {Category} from "@api/api.ts";
import {computed, ref} from "vue";
import {
  CategoriesById,
  CategoriesByName,
  flatCategoryTreeWithParentChain,
  mapCategoriesById,
  mapCategoriesByName
} from "../../misc/categoryHelpers.ts";
import {PreviewRowWithOriginalState} from "./TurnoverImporting.vue";
import type {VDataTable} from "vuetify/components";
import TableCellDescription from "../../misc/TableCellDescription.vue";
import TableCellMonetary from "../../misc/TableCellMonetary.vue";
import CategoryInput from "../../misc/CategoryInput.vue";
import {DateTime} from "luxon";
import * as _ from "lodash";
import {useTheme} from "vuetify";

const {value, categories} = defineProps<{
  value: PreviewRowWithOriginalState[];
  categories: Category[];
}>();

const emit = defineEmits<{
  (e: 'onCreateCategory', item: { categoryName: string; callback?: () => void; }): void;
}>();

const searchString = ref("some-dummy-value-to-activate-table-filtering");
const showOnlyImportables = ref(true);
const showOnlyMissingCategories = ref(false);

const flattedCategories = computed(() => {
  return flatCategoryTreeWithParentChain(categories, parents => parents.join(" > "));
})

const categoriesByName = computed<CategoriesByName>(() => {
  return mapCategoriesByName(flattedCategories.value)
})

const categoriesById = computed<CategoriesById>(() => {
  return mapCategoriesById(flattedCategories.value)
})

const hasSuggestions = computed<boolean>(() => {
  return value.some(r => !!r.suggestedCategory);
});

const hasImportables = computed<boolean>(() => {
  return value.some(r => !!r.originalImportable)
});

enum MarkType {
  All,
  None,
  Some
}

const allRowsAreMarkedAsImportable = computed<MarkType>(() => {
  const onlyImportables = value.filter(it => it.originalImportable);

  const groups = _.groupBy(onlyImportables, it => !!it.importable)

  const trueGroup = groups['true'] ?? []
  const falseGroup = groups['false'] ?? []

  if (trueGroup.length === onlyImportables.length)
    return MarkType.All;
  if (falseGroup.length === onlyImportables.length)
    return MarkType.None;

  return MarkType.Some;
})

function doFilter(row: PreviewRowWithOriginalState): boolean {
  const importable = showOnlyImportables.value ? !!row.originalImportable : true
  const missingCategory = showOnlyMissingCategories.value ? !row.categoryId : true;

  return importable && missingCategory;
}

function onSelectImportable(newValue: boolean) {
  value.forEach(row => {
    if (row.originalImportable)
      row.importable = newValue;
  })
}

function rowClass(item: PreviewRowWithOriginalState, type: any) {
  if (!item || type !== 'row')
    return;

  if (!item.importable)
    return 'table-secondary'

  if (!item.categoryId)
    return 'table-danger';

  return undefined;
}

const {current: theme} = useTheme();

function rowProps({item}: { item?: PreviewRowWithOriginalState }) {
  if (!item || !item.originalImportable)
    return undefined;

  const props: any = {style: {}};

  if (!item.importable)
    props.style['background-color'] = theme.value.colors.warning;
  else if (!item.categoryId)
    props.style['background-color'] = theme.value.colors.error;

  return props;
}

function onSelectCategory(select: any) {
  value.filter(row => row.checksum === select.checksum)
    .forEach(row => row.categoryId = select.categoryId)
}

function onCreateCategory(categoryName: string) {
  emit("onCreateCategory", {
    categoryName: categoryName,
    callback: undefined
  })
}

function onCreateSuggestedCategory(categoryName: string) {
  emit("onCreateCategory", {
    categoryName: categoryName,
    callback: () => {
      const newlyCreatedCategory = categories.find(cat => cat.name === categoryName)
      if (!newlyCreatedCategory)
        return;

      value.forEach(row => {
        if (row.suggestedCategory === categoryName && !row.categoryId)
          row.categoryId = newlyCreatedCategory.id;
      })
    }
  })
}

// see https://stackoverflow.com/a/75993081
type ReadonlyHeaders = VDataTable['$props']['headers']
type UnwrapReadonlyArray<A> = A extends Readonly<Array<infer I>> ? I : never;
type ReadonlyDataTableHeader = UnwrapReadonlyArray<ReadonlyHeaders>;

const fields = computed<ReadonlyDataTableHeader[]>(() => {
  const columns: ReadonlyDataTableHeader[] = [
    {
      key: 'checksum',
      title: '#',
      sortable: false,
    }];

  if (hasImportables.value)
    columns.push(
      {
        key: "originalImportable",
        title: "X",
        value: it => it.originalImportable,
        sortable: false,
      });

  columns.push(...[
    {
      key: "importable",
      title: "?",
      value: it => it.importable,
      sortable: false,
    },
    {
      key: "date",
      title: "Date",
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
      key: "amount",
      title: "Betrag",
      value: it => it.amountInCents,
      sortable: true,
    },
    {
      key: "categoryId",
      title: "Kategorie",
      value: it => it.categoryId,
      sortable: true,
    },
    {
      key: "suggestedCategories",
      title: "Vorschläge",
    }]);

  if (hasSuggestions.value)
    columns.push({
      key: "suggestedCategory",
      title: "Bank Vorschlag",
      sortable: true,
    });

  columns.push(...[
    {
      key: "Description",
      value: it => it.description,
    }
  ]);

  return columns;
})
</script>

<template>
  <v-data-table :items="value"
                :headers="fields"
                :search="searchString"
                :custom-filter="(_1, _2, row?: any) => doFilter(row.columns)"
                :items-per-page="-1"
                :hide-default-footer="true"
                :hover="true"
                :row-props="rowProps"
                density="compact">

    <template v-slot:header.originalImportable>
      <div class="d-flex justify-content-start align-items-center">
        <v-btn :icon="true"
               :x-small="!showOnlyImportables"
               @click.stop="showOnlyImportables = !showOnlyImportables">
          <v-icon color="success">
            {{ showOnlyImportables ? 'mdi-filter-check' : 'mdi-filter' }}
          </v-icon>
        </v-btn>
      </div>
    </template>

    <template v-slot:header.importable>
      <v-checkbox :model-value="allRowsAreMarkedAsImportable === MarkType.All"
                  :indeterminate="allRowsAreMarkedAsImportable === MarkType.Some ? true : undefined"
                  @update:modelValue="(x: any) => onSelectImportable(!!x)"/>
    </template>

    <template v-slot:header.categoryId="{column}">
      <div class="d-flex justify-content-between align-items-end">
        {{ column.title }}

        <v-btn :icon="true"
               :x-small="true"
               @click="showOnlyMissingCategories = !showOnlyMissingCategories">
          <v-icon>
            {{ showOnlyMissingCategories ? 'mdi-filter-check' : 'mdi-filter' }}
          </v-icon>
        </v-btn>
      </div>
    </template>

    <template v-slot:item.checksum="row">
      {{ row.index + 1 }}
    </template>

    <template v-slot:item.originalImportable="{ value }">
      {{ value ? '' : 'X' }}
    </template>

    <template v-slot:item.importable="row">
      <v-checkbox v-model="row.item.importable"
                  :disabled="!row.item.originalImportable"
                  density="compact"/>
    </template>

    <template v-slot:item.date="{ value }">
      {{ DateTime.fromISO(value).toLocaleString(DateTime.DATE_MED) }}
    </template>

    <template v-slot:item.recipient="{index, value}">
      <table-cell-description :index="index"
                              :value="value"
                              :max-length="32"/>
    </template>

    <template v-slot:item.amount="{ value }">
      <table-cell-monetary :value="value" class="float-right"/>
    </template>

    <template v-slot:item.categoryId="row">
      <category-input :id="`csv-category-input-${row.index}`"
                      :value="row.item.categoryId"
                      @input="newId => row.item.categoryId = newId ?? undefined"
                      @createCategory="onCreateCategory"
                      :flatted-categories="flattedCategories"
                      :categories-by-id="categoriesById"
                      :categories-by-name="categoriesByName"
                      :required="row.item.importable"
                      :disabled="!row.item.importable"/>
    </template>

    <template v-slot:item.suggestedCategories="row">
      <!--            <category-suggestion :checksum="row.item.checksum"-->
      <!--                                 :suggestions="row.item.suggestedCategories || []"-->
      <!--                                 :categories-by-id="categoriesById"-->
      <!--                                 :disabled="!row.item.importable"-->
      <!--                                 @select="onSelectCategory"/>-->
      suggested {{ row.index }}
    </template>

    <template v-slot:item.suggestedCategory="row" v-if="hasSuggestions">
      <v-btn v-if="row.item.suggestedCategory && !categoriesByName[row.item.suggestedCategory]"
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
      <table-cell-description :index="index" :value="value"/>
    </template>
  </v-data-table>
</template>

<style scoped>
.excluded-import-rows {
  background-color: yellow;
}

.error-import-rows {
  background-color: red;
}
</style>
