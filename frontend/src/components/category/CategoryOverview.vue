<script setup lang="ts">
import WaitingIndicator from "../misc/WaitingIndicator.vue";
import {computed, onMounted, ref, useTemplateRef} from "vue";
import {Category, CategoryPatch} from "@api/api.ts";
import CategoryList from "./CategoryList.vue";
import {CategoriesById, mapCategoriesById} from "../misc/categoryHelpers.ts";
import {CategoryReassign, NewCategory} from "./CategoryTreeList.vue";
import * as _ from "lodash";
import CategoryDetails from "./CategoryDetails.vue";
import {AxiosResponse} from "axios";
import {useApi} from "../../store/use-api.ts";
import {useNotification} from "../../store/use-notification.ts";

const api = useApi();
const notification = useNotification();

type DetailSelectionType = {
  isSelected: boolean;
  isNew: boolean | null;
  parentId: string | null;
  entity: Category | null;
}

const detailForm = useTemplateRef<typeof CategoryDetails>("detail-form");

const isLoading = ref(false);
const categories = ref<Category[]>([]);
const selectedForDetails = ref<DetailSelectionType>({
  isNew: null,
  isSelected: false,
  parentId: null,
  entity: null,
});

const categoriesById = computed<CategoriesById>(() => {
  return mapCategoriesById(categories.value)
})

const showDetails = computed(() => {
  return selectedForDetails.value.isSelected;
})

function loadCategories() {
  isLoading.value = true

  return api.categoriesApi.getCategoriesAsTree()
    .then(res => {
      const flatter = (cat: Category): Category[] => {
        return [
          cat,
          ...(cat.subCategories || []).flatMap(flatter)
        ];
      };

      categories.value = res.data.flatMap(flatter)
    })
    .catch(e => notification.notifyError("Kategorien konnten nicht geladen werden", e))
    .finally(() => isLoading.value = false)
}

function newCategory(parentId: string | null | undefined) {
  if (selectedForDetails.value.isSelected)
    detailForm.value?.reset()

  selectedForDetails.value.isNew = true
  selectedForDetails.value.isSelected = true
  selectedForDetails.value.parentId = parentId ?? null
  selectedForDetails.value.entity = {
    parentId: parentId ?? undefined,
    name: "",
    description: "",
    budget: undefined,
    isNew: true,
    id: 'new-id'
  }
}

function addNewCategoryTo(payload: NewCategory) {
  newCategory(payload.parentId)
}

function addNewRootCategory() {
  newCategory(null)
}

function openEditView(id: string) {
  if (selectedForDetails.value.entity && selectedForDetails.value.entity.id === id) {
    return;
  }

  selectedForDetails.value.isNew = false
  selectedForDetails.value.isSelected = true
  selectedForDetails.value.entity = {...categoriesById.value[id]}
}

function cancelActiveForm() {
  selectedForDetails.value.isSelected = false
  selectedForDetails.value.isNew = null
  selectedForDetails.value.entity = null
  selectedForDetails.value.parentId = null
}

function createNewRootCategory(cat: CategoryPatch) {
  doRestCallForCategory(() => {
    return api.categoriesApi.createCategory(cat);
  })
}

function createNewChildCategory(cat: CategoryPatch) {
  if (!selectedForDetails.value.parentId)
    return;

  doRestCallForCategory(() => {
      return api.categoriesApi.createCategoryAsChild(selectedForDetails.value.parentId!, cat);
    }
  );
}

function updateCategory(cat: CategoryPatch) {
  if (!cat.id)
    return;

  doRestCallForCategory(() => {
    return api.categoriesApi.updateCategory(cat.id!, cat)
      .then(res => {
        categories.value = _.filter(categories.value, cur => cur.id !== res.data.id)

        return res;
      })
  });
}

function doRestCallForCategory(callback: () => Promise<AxiosResponse<Category>> | undefined) {
  isLoading.value = true

  callback()
    ?.then(res => {
      selectedForDetails.value.entity = {...res.data}
      categories.value.push(res.data)
      openEditView(res.data.id)
    })
    .catch(e => notification?.notifyError("Kategorie konnte nicht erstellt oder verändert werden", e))
    .then(() => refresh())
    .finally(() => {
      isLoading.value = false
    })
}

function deleteCategory(category: Category) {
  isLoading.value = true

  if (selectedForDetails.value.entity && selectedForDetails.value.entity.id === category.id)
    cancelActiveForm()

  api.categoriesApi.deleteCategory(category.id)
    .then(() => {
      categories.value = _.filter(categories.value, cur => cur.id !== category.id)
    })
    .then(() => refresh())
    .catch(e => notification?.notifyError("Kategorie konnte nicht gelöscht werden", e))
    .finally(() => isLoading.value = false)
}

function reassignCategories(payload: CategoryReassign) {
  isLoading.value = true

  Promise.all(
    payload.sources.map(id => categoriesById.value[id])
      .map(source => api.categoriesApi.reallocateCategory(payload.target.id, source.id))
  )
    .then(() => notification?.notifySuccess("Kategorien verschoben"))
    .then(() => refresh())
    .catch(e => notification?.notifyError("Kategorie konnte nicht verschoben werden", e))
    .finally(() => isLoading.value = false)
}

function refresh() {
  loadCategories()
}

onMounted(() => {
  refresh();
})
</script>

<template>
  <v-card class="px-5">
    <waiting-indicator :is-loading="isLoading"/>

    <v-card-title class="p-2">
      <div class="w-100 d-flex justify-space-between align-center">
        <h4>Kategorien</h4>

        <v-btn @click="addNewRootCategory"
               :loading="isLoading"
               :disabled="selectedForDetails.isSelected"
               prepend-icon="mdi-plus-box-outline"
               text="Neue Kategorie"
               color="primary"/>
      </div>
    </v-card-title>

    <v-card-text>
      <v-container>
        <v-row>
          <v-col :cols="showDetails ? 8 : 12">
            <category-list :categories-by-id="categoriesById"
                           :categories="categories"
                           @refresh="refresh"
                           @edit="openEditView"
                           @newCategory="addNewCategoryTo"
                           @deleteCategory="deleteCategory"
                           @onReassign="reassignCategories"/>
          </v-col>

          <v-col v-if="showDetails" :cols="4">
            <div class="fixed-position-editor">
              <div class="details-view">
                <category-details
                  v-if="selectedForDetails.isSelected && selectedForDetails.entity && selectedForDetails.isNew !== null"
                  ref="detail-form"
                  :categories-by-id="categoriesById"
                  :entity="selectedForDetails.entity"
                  :is-new="selectedForDetails.isNew"
                  :is-loading="isLoading"
                  @createAsChild="createNewChildCategory"
                  @createAsRoot="createNewRootCategory"
                  @update="updateCategory"
                  @close="cancelActiveForm"
                  @refresh="refresh"/>
              </div>
            </div>
          </v-col>
        </v-row>
      </v-container>
    </v-card-text>
  </v-card>
</template>

<style scoped>
.fixed-position-editor {
  position: sticky;
  top: 5em;
  bottom: 5em;
  height: 88vh;
}
</style>
