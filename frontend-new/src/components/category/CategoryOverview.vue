<script setup lang="ts">
import WaitingIndicator from "../misc/WaitingIndicator.vue";
import {computed, inject, ref, useTemplateRef} from "vue";
import {Category, CategoryApi, CategoryPatch, ReportsApi} from "@api/api.ts";
import {apiRefKey} from "../../keys.ts";

const api: CategoryApi | undefined = inject(apiRefKey)?.categoriesApi;

type DetailSelectionType = {
  isNew: null;
  isSelected: boolean;
  parentId: null;
  entity: null;
  budget: number | null;
}

const detailForm = useTemplateRef("detail-form");

const isLoading = ref(false);
const categories = ref([]);
const selectedForDetails = ref<DetailSelectionType>({
  isNew: null,
  isSelected: false,
  parentId: null,
  entity: null,
});

const categoriesById = computed(() => {
  return categories.value.reduce((old, cur) => ({...old, [cur.id]: cur}), {})
})

const showDetails = computed(() => {
  return selectedForDetails.value.isSelected;
})

function loadCategories() {
  isLoading.value = true

  return api?.getCategoriesAsTree()
      .then((res: Category[]) => {
        const flatter = cat => {
          return [
            cat,
            ...(cat.subCategories || []).flatMap(flatter)
          ];
        };

        categories.value = res.flatMap(flatter)
        return categories.value
      })
      .finally(() => {
        isLoading.value = false
      })

}

function newCategory(parentId: string) {
  if (selectedForDetails.value.isSelected)
    detailForm.value?.reset()

  selectedForDetails.value.isNew = true
  selectedForDetails.value.isSelected = true
  selectedForDetails.value.entity = {
    parentId: parentId,
    name: "",
    description: "",
    budget: null,
  }
}

function addNewCategoryTo(payload) {
  newCategory(payload.parentId)
}

function addNewRootCategory() {
  newCategory(null)
}

function openEditView(id) {
  if (selectedForDetails.value.entity && selectedForDetails.value.entity.id === id)
    return;

  selectedForDetails.value.isNew = false
  selectedForDetails.value.isSelected = true
  selectedForDetails.value.entity = {...categoriesById[id]}
}

function cancelActiveForm() {
  selectedForDetails.value.isNew = null
  selectedForDetails.value.isSelected = false
  selectedForDetails.value.entity = null
}

function createNewRootCategory(cat: CategoryPatch) {
  this.doRestCallForCategory(() => {
    return api?.createCategory(cat);
  })
}

function createNewChildCategory(cat) {
  this.doRestCallForCategory(() => {
        return api?.createCategoryAsChild(cat.parentId, cat);
      }
  );
}

function updateCategory(cat) {
  this.doRestCallForCategory(() => {
    return api?.updateCategory(cat)
        .then(res => {
          categories.value = _.filter(categories.value, cur => cur.id !== res.id)

          return res;
        })
  });
}

function doRestCallForCategory(callback: () => Promise<Category> | undefined) {
  this.isLoading = true

  callback()
      ?.then(res => {
        this.selectedForDetails.entity = {...res}
        this.categories.push(res)
        this.openEditView(res.id)
      })
      .finally(() => {
        this.isLoading = false
      })
}

function deleteCategory(category) {
  isLoading.value = true

  if (selectedForDetails.value.entity && selectedForDetails.value.entity.id === category.id)
    cancelActiveForm()

  api?.deleteCategory(category.id)
      .then(() => {
        categories.value = _.filter(categories.value, cur => cur.id !== category.id)
      })
      .finally(() => {
        isLoading.value = false
      })
}

function reassignCategories(payload) {
  isLoading.value = true

  Promise.all(
      payload.sources.map(id => categoriesById.value[id])
          .map(source => api?.reallocateCategory(payload.target, source))
  )
      .then(() => loadCategories())
      .finally(() => {
        isLoading.value = false
      })
}

loadCategories();
</script>

<template>
  <v-card class="px-5">
    <waiting-indicator :is-loading="isLoading"/>

    <template v-slot:title class="p-2">
      <div class="w-100 d-flex justify-space-between align-center">
        <h5>Kategorien</h5>

        <v-btn-toggle :dense="true">
          <v-btn @click="loadCategories"
                 :loading="isLoading"
                 color="accent"
                 :small="true">
            Neuladen
          </v-btn>
          <v-btn @click="addNewRootCategory"
                 :loading="isLoading"
                 color="primary"
                 :small="true">
            Neue Kategorie
          </v-btn>
        </v-btn-toggle>
      </div>
    </template>


    <template v-slot:text class="p-2">
      <v-container class="pt-0">
        <v-row class="py-0">
          <v-col class="py-0" :cols="showDetails ? 8 : 12">
            <!--            <category-list :categories-by-id="categoriesById"-->
            <!--                           :categories="categories"-->
            <!--                           @newCategory="addNewCategoryTo"-->
            <!--                           @deleteCategory="deleteCategory"-->
            <!--                           @onReassign="reassignCategories"-->
            <!--                           @edit="openEditView"/>-->
          </v-col>

          <v-col v-if="showDetails" :cols="4">
            <div class="fixed-position-editor">
              <div class="details-view">
                <!--                <category-details v-if="selectedForDetails.isSelected"-->
                <!--                                  ref="detail-form"-->
                <!--                                  :categories-by-id="categoriesById"-->
                <!--                                  :entity="selectedForDetails.entity"-->
                <!--                                  :is-new="selectedForDetails.isNew"-->
                <!--                                  :is-loading="isLoading"-->
                <!--                                  @createAsChild="createNewChildCategory"-->
                <!--                                  @createAsRoot="createNewRootCategory"-->
                <!--                                  @update="updateCategory"-->
                <!--                                  @close="cancelActiveForm"/>-->
              </div>
            </div>
          </v-col>
        </v-row>
      </v-container>
    </template>
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
