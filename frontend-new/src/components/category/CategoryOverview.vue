<script setup lang="ts">
import WaitingIndicator from "../misc/WaitingIndicator.vue";
import {computed, inject, onMounted, ref, useTemplateRef} from "vue";
import {Category, CategoryApi, CategoryPatch} from "@api/api.ts";
import {apiRefKey} from "../../keys.ts";
import CategoryList from "./CategoryList.vue";
import {CategoriesById, mapCategoriesById} from "../misc/categoryHelpers.ts";
import {CategoryReassign, NewCategory} from "./CategoryTreeList.vue";
import * as _ from "lodash";
import CategoryDetails from "./CategoryDetails.vue";

const api: CategoryApi | undefined = inject(apiRefKey)?.categoriesApi;

type DetailSelectionType = {
  isSelected: boolean;
  isNew: boolean | null;
  parentId: string | null;
  entity: Category | null;
}

const detailForm = useTemplateRef("detail-form");

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

  return api?.getCategoriesAsTree()
    .then((res: Category[]) => {
      const flatter = (cat: Category): Category[] => {
        return [
          cat,
          ...(cat.subCategories || []).flatMap(flatter)
        ];
      };

      categories.value = res.flatMap(flatter)
    })
    .finally(() => {
      isLoading.value = false
    })
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
    budget: null,
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
    return api?.createCategory(cat);
  })
}

function createNewChildCategory(cat: CategoryPatch) {
  if (!selectedForDetails.value.parentId)
    return;

  doRestCallForCategory(() => {
      return api?.createCategoryAsChild(selectedForDetails.value.parentId!, cat);
    }
  );
}

function updateCategory(cat: CategoryPatch) {
  if (!cat.id)
    return;

  doRestCallForCategory(() => {
    return api?.updateCategory(cat.id!, cat)
      .then((res: Category) => {
        categories.value = _.filter(categories.value, cur => cur.id !== res.id)

        return res;
      })
  });
}

function doRestCallForCategory(callback: () => Promise<Category> | undefined) {
  isLoading.value = true

  callback()
    ?.then(res => {
      selectedForDetails.value.entity = {...res}
      categories.value.push(res)
      openEditView(res.id)
    })
    .then(() => refresh())
    .finally(() => {
      isLoading.value = false
    })
}

function deleteCategory(category: Category) {
  isLoading.value = true

  if (selectedForDetails.value.entity && selectedForDetails.value.entity.id === category.id)
    cancelActiveForm()

  api?.deleteCategory(category.id)
    .then(() => {
      categories.value = _.filter(categories.value, cur => cur.id !== category.id)
      refresh()
    })
    .finally(() => {
      isLoading.value = false
    })
}

function reassignCategories(payload: CategoryReassign) {
  isLoading.value = true

  Promise.all(
    payload.sources.map(id => categoriesById.value[id])
      .map(source => api?.reallocateCategory(payload.target.id, source.id))
  )
    .then(() => refresh())
    .finally(() => {
      isLoading.value = false
    })
}

function refresh() {
  loadCategories()
}

onMounted(() => {
  loadCategories();
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
               color="primary">
          Neue Kategorie
        </v-btn>
      </div>
    </v-card-title>

    <v-card-text>
      <v-container>
        <v-row>
          <v-col :cols="showDetails ? 8 : 12">
            <category-list :categories-by-id="categoriesById"
                           :categories="categories"
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
