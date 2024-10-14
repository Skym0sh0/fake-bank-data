<script setup lang="ts">
import {Category, CategoryPatch, TurnoverImportRowsPatch, TurnoverRow, TurnoverRowPatch} from "@api/api.ts";
import {computed, inject, ref} from "vue";
import {notifierRefKey} from "../../../keys.ts";
import TableCellMonetary from "../../misc/TableCellMonetary.vue";
import TableCellDescription from "../../misc/TableCellDescription.vue";
import CategoryInput from "../../misc/CategoryInput.vue";
import {flatCategoryTreeWithParentChain, mapCategoriesById, mapCategoriesByName} from "../../misc/categoryHelpers.ts";
import type {VDataTable} from "vuetify/components";
import {useApi} from "../../../store/use-api.ts";

const api = useApi()
const notifierRef = inject(notifierRefKey);

const {category} = defineProps<{
  category: Category;
}>();

const emit = defineEmits<{
  (e: 'refresh'): void;
}>();

const isModalOpen = ref(false)

const isEditing = ref(false)
const isLoading = ref(false)
const referencedRows = ref<TurnoverRow[]>([])
const originalValues = ref<TurnoverRowPatch[] | null>(null)
const allCategories = ref<Category[] | null>(null)
const currentLoadingRowId = ref<string | null>(null)

// see https://stackoverflow.com/a/75993081
type ReadonlyHeaders = VDataTable['$props']['headers']
type UnwrapReadonlyArray<A> = A extends Readonly<Array<infer I>> ? I : never;
type ReadonlyDataTableHeader = UnwrapReadonlyArray<ReadonlyHeaders>;

const items = computed(() => {
  return referencedRows.value;
})

const flattedCategories = computed(() => {
  return flatCategoryTreeWithParentChain(allCategories.value ?? [], parents => parents.join(" > "))
})

const categoriesById = computed(() => {
  return mapCategoriesById(flattedCategories.value)
})

const categoriesByName = computed(() => {
  return mapCategoriesByName(flattedCategories.value)
})

const changedTurnovers = computed<TurnoverRowPatch[]>(() => {
  const all: TurnoverRowPatch[] = items.value.flatMap(rowToChangeObject)

  return all.filter(
    row => !(originalValues.value || [])
      .find(orig => orig.id === row.id && orig.categoryId === row.categoryId)
  );
})

function onOpenModal() {
  loadData()

  isModalOpen.value = true
}

function reset() {
  isEditing.value = false
  isLoading.value = false
  referencedRows.value = []
  allCategories.value = null
  currentLoadingRowId.value = null

  isModalOpen.value = false
}

function onChangeCategory(turnoverId: string | undefined, newCategoryId: string | null | undefined) {
  referencedRows.value = items.value.map((to: TurnoverRow): TurnoverRow => {
    if (turnoverId !== to.id)
      return to;

    return {
      ...to,
      categoryId: newCategoryId ?? undefined,
    }
  })
}

function onCreateCategory(id: string, categoryName: string) {
  const normalized: CategoryPatch = {
    name: categoryName,
  };

  currentLoadingRowId.value = id

  api.categoriesApi.createCategory(normalized)
    .then(res => {
      referencedRows.value.forEach(row => {
        if (row.id === id)
          row.categoryId = res.data.id;
      })

      if (allCategories.value)
        allCategories.value.push(res.data)
      else
        allCategories.value = [res.data]
    })
    .catch(e => notifierRef?.notifyError(`Kategorie '${categoryName}' konnte nicht erstellt werden`, e))
    .finally(() => currentLoadingRowId.value = null)
}

function save() {
  isLoading.value = true

  const patch: TurnoverImportRowsPatch = {
    rows: changedTurnovers.value,
  };
  api.turnoversApi.batchPatchTurnoverImports(patch)
    .then(() => emit("refresh"))
    .then(() => reset())
    .catch(e => notifierRef?.notifyError(`Kategorie konnte nicht verändert werden`, e))
    .finally(() => isLoading.value = false)
}

function loadData() {
  isLoading.value = true;

  Promise.all([loadCategories(), loadReferencedRows()])
    .catch(e => notifierRef?.notifyError(`Kategorien konnten nicht geladen werden`, e))
    .finally(() => isLoading.value = false)
}

function loadCategories() {
  allCategories.value = null

  return api.categoriesApi.getCategoriesAsTree()
    .then(res => allCategories.value = res.data)
}

function loadReferencedRows() {
  referencedRows.value = [];

  return api.turnoversApi.fetchTurnoversForCategory(category.id)
    .then(res => {
      referencedRows.value = res.data
      originalValues.value = res.data.flatMap(rowToChangeObject)
    })
}

function rowToChangeObject(row: TurnoverRow): TurnoverRowPatch[] {
  if (!row.categoryId)
    return [];

  return [{
    id: row.id,
    categoryId: row.categoryId,
  }]
}

const columns = computed<ReadonlyDataTableHeader[]>(() => {
  const tmp: ReadonlyDataTableHeader[] = [
    {
      key: "date",
      value: it => it.date,
      title: "Datum",
      sortable: true,
    },
    {
      key: "recipient",
      value: it => it.recipient,
      title: "Empfänger",
      sortable: true,
    },
    {
      key: "amountInCents",
      value: it => it.amountInCents,
      title: "Summe",
      sortable: true,
    },
    {
      key: "description",
      value: it => it.description,
      title: "Beschreibung",
      sortable: true,
    }
  ];

  if (isEditing.value)
    tmp.push({
      value: "newCategory",
      title: "Neue Kategorie",
    })

  return tmp;
})
</script>

<template>
  <slot name="button" :clickCallback="onOpenModal">
    <v-btn id="usage-count"
           variant="outlined"
           color="primary"
           :pill="true"
           size="small"
           :disabled="!category.usageCount"
           @click="onOpenModal"
           :title="`Diese Kategorie wird in ${category.usageCount} Transaktionen benutzt.`">
      {{ category.usageCount }}
    </v-btn>
  </slot>

  <v-dialog v-model="isModalOpen">
    <v-card>
      <v-card-title>
        Turnovers mit Kategorie {{ category.name }}
      </v-card-title>

      <v-card-text>
        <v-data-table style="max-height: 75vh"
                      :fixed-header="true"
                      :items="items"
                      :headers="columns"
                      :loading="isLoading"
                      density="compact"
                      :hover="true"
                      :items-per-page="-1"
                      :hide-default-footer="true">

          <template v-slot:item.amountInCents="{value}">
            <table-cell-monetary :value="value" class="float-right"/>
          </template>

          <template v-slot:item.description="{index, value}">
            <table-cell-description :index="index" :value="value"/>
          </template>

          <template v-slot:item.newCategory="row" v-if="isEditing">
            <category-input :id="`${row.item.id}`"
                            :value="row.item.categoryId"
                            :loading="row.item.id === currentLoadingRowId"
                            :flatted-categories="flattedCategories"
                            :categories-by-id="categoriesById"
                            :categories-by-name="categoriesByName"
                            @input="newCategoryId => onChangeCategory(row.item.id, newCategoryId)"
                            @createCategory="name => onCreateCategory(row.item.id, name)"/>
          </template>
        </v-data-table>
      </v-card-text>

      <v-card-actions>
        <div class="w-100 d-flex justify-end ga-1">
          <v-btn v-if="!isEditing"
                 @click="isEditing = true"
                 color="secondary">
            Bearbeiten
          </v-btn>

          <v-btn v-if="isEditing"
                 @click="reset"
                 color="warning">
            Abbrechen
          </v-btn>

          <v-btn v-if="isEditing"
                 @click="save"
                 :disabled="!changedTurnovers.length || !!currentLoadingRowId"
                 color="success">
            Speichern
          </v-btn>

          <v-btn v-if="!isEditing"
                 @click="reset"
                 color="primary">
            Ok
          </v-btn>
        </div>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
