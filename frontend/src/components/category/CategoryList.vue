<script setup lang="ts">
import {computed, ref} from "vue";
import {Category} from "@api/api.ts";
import CategoryTreeList, {CategoryReassign, NewCategory} from "./CategoryTreeList.vue";
import {CategoriesById} from "../misc/categoryHelpers.ts";

const {categories, categoriesById} = defineProps<{
  categories: Category[],
  categoriesById: CategoriesById,
}>()

const emit = defineEmits<{
  (e: 'edit', id: string): void;
  (e: 'refresh'): void;
  (e: 'newCategory', cat: NewCategory): void;
  (e: 'deleteCategory', cat: Category): void;
  (e: 'onReassign', assignment: CategoryReassign): void;
}>()

const quickFilter = ref("");

const filteredCategories = computed<Category[]>(() => {
  if (!quickFilter.value)
    return categories

  const regex = new RegExp(quickFilter.value, "i")

  return categories.filter(cat => cat.name.search(regex) >= 0)
})

function onEdit(id: string) {
  emit("edit", id)
}

function onNewCategory(c: NewCategory) {
  emit("newCategory", c)
}

function onDeleteCategory(c: Category) {
  emit("deleteCategory", c)
}

function onReassign(reassignment: CategoryReassign) {
  emit("onReassign", reassignment);
}

function onRefresh() {
  emit("refresh");
}
</script>

<template>
  <v-row>
    <v-col>
      <v-text-field id="quick-filter-text-input-field"
                    v-model="quickFilter"
                    type="text"
                    :clearable="true"
                    label="Filter"
                    hint="Suche Kategorie (auch Regex möglich)"
                    prepend-inner-icon="mdi-filter-variant"
                    suffix="Regex"/>
    </v-col>
  </v-row>

  <v-row>
    <v-col>
      <category-tree-list :categories-by-id="categoriesById"
                          :categories="filteredCategories"
                          @newCategory="onNewCategory"
                          @deleteCategory="onDeleteCategory"
                          @onReassign="onReassign"
                          @edit="onEdit"
                          @refresh="onRefresh"/>
    </v-col>
  </v-row>
</template>
