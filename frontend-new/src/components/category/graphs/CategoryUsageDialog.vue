<script setup lang="ts">
import {Category, CategoryPatch, TurnoverImportRowsPatch, TurnoverRow, TurnoverRowPatch} from "@api/api.ts";
import {computed, inject, ref} from "vue";
import {apiRefKey, ApiType} from "../../../keys.ts";
import Waiter from "../../misc/Waiter.vue";
import TableCellMonetary from "../../misc/TableCellMonetary.vue";
import TableCellDescription from "../../misc/TableCellDescription.vue";
import CategoryInput from "../../misc/CategoryInput.vue";
import {flatCategoryTreeWithParentChain, mapCategoriesById, mapCategoriesByName} from "../../misc/categoryHelpers.ts";
import type {VDataTable} from "vuetify/components";

const api: ApiType | undefined = inject(apiRefKey);

type TurnoverIdWithCategoryId = Pick<TurnoverRow, 'id' | 'categoryId'>;

const {category} = defineProps<{
  category: Category;
}>();

const isModalOpen = ref(false)

const isEditing = ref(false)
const isLoading = ref(false)
const referencedRows = ref<TurnoverRow[]>([])
const originalValues = ref<TurnoverIdWithCategoryId[] | null>(null)
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
  const all: TurnoverIdWithCategoryId[] = items.value.map(rowToChangeObject)
  return all.filter(
    row => !(originalValues.value || [])
      .find(orig => orig.id === row.id && orig.categoryId === row.categoryId)
  )
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

function checkToHide(e) {
  const mustNotBeClosed = changedTurnovers.value.length || (
    isEditing.value && !(
      e.trigger === null // abort button was pressed
      || e.trigger === 'headerclose' // X Button in header was pressed
    ));
  if (mustNotBeClosed) {
    e.preventDefault()
  }
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

  api?.categoriesApi.createCategory(normalized)
    .then((res: Category) => {
      referencedRows.value.forEach(row => {
        if (row.id === id)
          row.categoryId = res.id;
      })

      allCategories.value.push(res)
    })
    .finally(() => currentLoadingRowId.value = null)
}

function save() {
  isLoading.value = true

  const patch: TurnoverImportRowsPatch = {
    rows: changedTurnovers.value,
  };
  api?.turnoversApi.batchPatchTurnoverImports(patch)
    .then(() => reset())
    .finally(() => isLoading.value = false)
}

function loadData() {
  isLoading.value = true;

  Promise.all([loadCategories(), loadReferencedRows()])
    .finally(() => isLoading.value = false)
}

function loadCategories() {
  allCategories.value = null

  return api?.categoriesApi.getCategoriesAsTree()
    .then((cats: Category[]) => allCategories.value = cats)
}

function loadReferencedRows() {
  referencedRows.value = null;

  return api.turnoversApi.fetchTurnoversForCategory(category.id)
    .then((rows: TurnoverRow[]) => {
      referencedRows.value = rows
      originalValues.value = rows.map(rowToChangeObject)
    })
}

function rowToChangeObject(row: TurnoverRow): TurnoverIdWithCategoryId {
  return {
    id: row.id,
    categoryId: row.categoryId,
  }
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
  <div>
    <slot name="button" :clickCallback="onOpenModal">
      <v-btn id="usage-count"
             variant="text"
             color="primary"
             :pill="true"
             size="sm"
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
          <waiter :is-loading="isLoading">
            <v-data-table :items="items"
                          :headers="columns"
                          density="compact"
                          :hover="true"
                          :hide-default-footer="true">

              <template v-slot:item.amountInCents="{value}">
                <table-cell-monetary :value="value" class="float-right"/>
              </template>

              <template v-slot:item.description="{index, value}">
                <table-cell-description :index="index" :value="value"/>
              </template>

              <template v-slot:item.newCategory="row" v-if="isEditing">
                <!--                {{ row }}-->
                <!--                v-model="row.item.categoryId"-->
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
          </waiter>
        </v-card-text>

        <v-card-actions>
          <div class="w-100 d-flex justify-space-between">
            <v-btn-group>
              <v-btn v-if="!isEditing"
                     @click="isEditing = true">
                Bearbeiten
              </v-btn>
            </v-btn-group>

            <v-btn-group>
              <v-btn v-if="isEditing"
                     @click="reset">
                Abbrechen
              </v-btn>

              <v-btn v-if="isEditing"
                     @click="save"
                     :disabled="!changedTurnovers.length || !!currentLoadingRowId">
                Speichern
              </v-btn>

              <v-btn v-if="!isEditing"
                     @click="reset">
                Ok
              </v-btn>
            </v-btn-group>
          </div>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!--    <b-modal ref="modal-turnovers"-->
    <!--             :scrollable="true"-->
    <!--             :centered="true"-->
    <!--             :title="`Turnovers mit Kategorie ${category.name}`"-->
    <!--             @hidden="reset"-->
    <!--             @hide="checkToHide">-->
    <!--      <template v-slot:modal-footer>-->
    <!--        <div class="w-100 d-flex justify-content-between">-->
    <!--          <b-btn-group>-->
    <!--            <b-button v-if="!isEditing"-->
    <!--                      @click="isEditing = true"-->
    <!--                      variant="danger">-->
    <!--              Bearbeiten-->
    <!--            </b-button>-->
    <!--          </b-btn-group>-->

    <!--          <b-btn-group>-->
    <!--            <b-button v-if="isEditing"-->
    <!--                      @click="reset"-->
    <!--                      variant="danger">-->
    <!--              Abbrechen-->
    <!--            </b-button>-->

    <!--            <b-button v-if="isEditing"-->
    <!--                      @click="save"-->
    <!--                      :disabled="!changedTurnovers.length || !!currentLoadingRowId"-->
    <!--                      variant="primary">-->
    <!--              Speichern-->
    <!--            </b-button>-->

    <!--            <b-button v-if="!isEditing"-->
    <!--                      @click="reset"-->
    <!--                      variant="primary">-->
    <!--              Ok-->
    <!--            </b-button>-->
    <!--          </b-btn-group>-->
    <!--        </div>-->
    <!--      </template>-->

    <!--      <waiter :is-loading="isLoading">-->
    <!--        <b-table :striped="true"-->
    <!--                 :hover="true"-->
    <!--                 :responsive="true"-->
    <!--                 :small="true"-->
    <!--                 :bordered="false"-->
    <!--                 :items="items"-->
    <!--                 :fields="columns">-->
    <!--          <template v-slot:cell(amountInCents)="row">-->
    <!--            <table-cell-monetary :value="row.item.amountInCents" class="float-right"/>-->
    <!--          </template>-->

    <!--          <template v-slot:cell(description)="row">-->
    <!--            <table-cell-description :index="row.index" :value="row.item.description"/>-->
    <!--          </template>-->

    <!--          <template v-slot:cell(newCategory)="row">-->
    <!--            <category-input :id="`${row.item.id}`"-->
    <!--                            v-model="row.item.categoryId"-->
    <!--                            :loading="row.item.id === currentLoadingRowId"-->
    <!--                            :flatted-categories="flattedCategories"-->
    <!--                            :categories-by-id="categoriesById"-->
    <!--                            :categories-by-name="categoriesByName"-->
    <!--                            @createCategory="name => onCreateCategory(row.item.id, name)"/>-->
    <!--          </template>-->
    <!--        </b-table>-->
    <!--      </waiter>-->
    <!--    </b-modal>-->
  </div>
</template>

<style scoped>

</style>