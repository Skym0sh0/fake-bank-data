<script setup lang="ts">
import {computed, inject, onMounted, ref} from "vue";
import {
  Category,
  CategoryApi,
  CategoryPatch,
  TurnoverImport,
  TurnoverImportRowsPatch,
  TurnoverRow,
  TurnoverRowPatch,
  TurnoversApi
} from "@api/api.ts";
import {DateTime} from "luxon";
import {useRouter} from "vue-router";
import {apiRefKey} from "../../../keys.ts";
import WaitingIndicator from "../../misc/WaitingIndicator.vue";
import ConfirmationedButton from "../../misc/ConfirmationedButton.vue";
import TurnoverRowsTable from "./TurnoverRowsTable.vue";
import {LookupById} from "../../misc/categoryHelpers.ts";

const api: TurnoversApi | undefined = inject(apiRefKey)?.turnoversApi;
const categoryApi: CategoryApi | undefined = inject(apiRefKey)?.categoriesApi;

const router = useRouter()

const {id} = defineProps<{
  id: string;
}>();

const isLoading = ref(false)
const turnoverImport = ref<TurnoverImport | null>(null)
const categories = ref<Category[]>([])
const initialTurnoverRowsCategories = ref<TurnoverRowPatch[]>([])
const deleteRowsWithIds = ref<string[]>([])

const importTimestamp = computed(() => {
  if (!turnoverImport.value || !turnoverImport.value.importedAt)
    return null;

  return DateTime.fromISO(turnoverImport.value.importedAt).toLocaleString(DateTime.DATETIME_MED_WITH_SECONDS);
})

type TurnoverRowPatchById = LookupById<TurnoverRowPatch>;

const currentRowCategoryChanges = computed<TurnoverRowPatch[]>(() => {
  if (!turnoverImport.value)
    return []

  const rowsById: TurnoverRowPatchById = initialTurnoverRowsCategories.value.reduce((prev: TurnoverRowPatchById, cur: TurnoverRowPatch): TurnoverRowPatchById => {
    if (!cur.id)
      return prev;
    return ({...prev, [cur.id]: cur});
  }, {})

  return extractTurnoverRowsWithCategories(turnoverImport.value)
    .filter((row: TurnoverRowPatch) => row.id && rowsById[row.id]?.categoryId !== row.categoryId);
})

const currentRowCategoryChangesById = computed<TurnoverRowPatchById>(() => {
  return currentRowCategoryChanges.value.reduce((prev: TurnoverRowPatchById, cur: TurnoverRowPatch): TurnoverRowPatchById => {
    if (!cur.id)
      return prev;

    return ({...prev, [cur.id]: cur});
  }, {})
})

const currentDeletedRowsById = computed<LookupById<string>>(() => {
  return deleteRowsWithIds.value.reduce((prev, cur) => ({...prev, [cur]: cur}), {})
})

const isValidToSave = computed(() => {
  return deleteRowsWithIds.value.length > 0
    || (currentRowCategoryChanges.value.length > 0
      && currentRowCategoryChanges.value.every(row => !!row.categoryId))
})

function reload() {
  isLoading.value = true

  Promise.all([loadImport(), loadCategories()])
    .finally(() => isLoading.value = false)
}

function loadImport() {
  return api?.fetchTurnoverImport(id)
    .then((res: TurnoverImport) => {
      turnoverImport.value = res
      deleteRowsWithIds.value = []
      initialTurnoverRowsCategories.value = extractTurnoverRowsWithCategories(res)
    })
}

function loadCategories() {
  return categoryApi?.getCategoriesAsTree()
    .then((res: Category[]) => {
      categories.value = res
    })
}

function onCreateCategory(categoryName: string) {
  const normalized: CategoryPatch = {
    name: categoryName,
  };

  isLoading.value = true
  categoryApi?.createCategory(normalized)
    .then(() => loadCategories())
    .finally(() => isLoading.value = false)
}

function onDeleteTurnover(row: TurnoverRow) {
  if (!row.id)
    return;

  deleteRowsWithIds.value.push(row.id)
}

function onUndoDeleteTurnover(row: TurnoverRow) {
  if (!row.id)
    return;

  let idx;
  while ((idx = deleteRowsWithIds.value.indexOf(row.id)) >= 0) {
    if (idx < 0)
      return;
    deleteRowsWithIds.value.splice(idx, 1)
  }
}

function onReset() {
  reload()
}

function onBack() {
  router.back()
}

function onSave() {
  if (!isValidToSave.value)
    return;

  const changes: TurnoverImportRowsPatch = {
    rows: currentRowCategoryChanges.value,
    deleteRowIds: deleteRowsWithIds.value,
  };

  isLoading.value = true
  api?.patchTurnoverImport(id, changes)
    .then(() => loadImport())
    .finally(() => isLoading.value = false)
}

function extractTurnoverRowsWithCategories(turnoverImport?: TurnoverImport): TurnoverRowPatch[] {
  if (!turnoverImport || !turnoverImport.turnovers)
    return [];

  return turnoverImport.turnovers.map((row: TurnoverRow): TurnoverRowPatch => ({
    id: row.id,
    categoryId: row.categoryId,
  }))
}

onMounted(() => {
  reload()
})
</script>

<template>
  <div>
    <waiting-indicator :is-loading="isLoading"/>

    <v-card v-if="!!turnoverImport">
      <v-card-title>
        Importiere Umsatz Datei
      </v-card-title>

      <v-card-subtitle>
        <div class="d-flex justify-content-between">
          <span>
            {{ turnoverImport.format }}: "{{ turnoverImport.filename }}" ({{ turnoverImport.encoding }})
          </span>

          <span>
            {{ importTimestamp }}
          </span>
        </div>
      </v-card-subtitle>

      <v-card-text>
        <turnover-rows-table v-if="categories"
                             :rows="turnoverImport?.turnovers ?? []"
                             :touchedRowsIdsById="currentRowCategoryChangesById"
                             :deletedRowsIdsById="currentDeletedRowsById"
                             :categories="categories"
                             @onCreateCategory="onCreateCategory"
                             @deleteTurnover="onDeleteTurnover"
                             @undoDeleteTurnover="onUndoDeleteTurnover"/>
      </v-card-text>

      <v-card-actions class="d-flex justify-content-between">
        <confirmationed-button @click="onReset"
                               default-caption="Verwerfen"
                               request-caption="Verwerfen??"
                               confirmed-caption="Jetzt Verwerfen!!!"
                               :wait-time-ms="250"
                               default-color="error"
                               :small="true"
                               :disabled="!isValidToSave"/>

        <div class="d-flex" style="gap: 0.5em">
          <v-btn @click="onBack">
            Zurück
          </v-btn>
          <confirmationed-button @click="onSave"
                                 :wait-time-ms="500"
                                 default-caption="Speichern"
                                 request-caption="Umsätze überschreiben??"
                                 confirmed-caption="Jetzt überschreiben"
                                 :disabled="!isValidToSave"
                                 color="info">
            Speichern
          </confirmationed-button>
        </div>
      </v-card-actions>
    </v-card>
  </div>
</template>
