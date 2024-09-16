<script setup lang="ts">
import {computed, ref} from "vue";
import {Category} from "@api/api.ts";
import {CategoriesByIdMap} from "./CategoryOverview.vue";
import CategoryTreeList from "./CategoryTreeList.vue";

const {categories, categoriesById} = defineProps<{
  categories: Category[],
  categoriesById: CategoriesByIdMap,
}>()

const quickFilter = ref("");

const filteredCategories = computed<Category[]>(() => {
  if (!quickFilter.value)
    return categories

  const regex = new RegExp(quickFilter.value, "i")

  return categories.filter(cat => cat.name.search(regex) >= 0)
})
</script>

<template>
  <v-container class="px-0">
    <!--        <global-events @keydown.17.prevent="reallocationEnabled = true"-->
    <!--                       @keyup.17.prevent="reallocationEnabled = false"/>-->

    <v-row>
      <v-col>
        <v-text-field id="quick-filter-text-input-field"
                      v-model="quickFilter"
                      type="text"
                      :dense="true"
                      :clearable="true"
                      label="Filter"
                      hint="Suche Kategorie (auch Regex möglich)"
                      suffix="Regex"/>
      </v-col>
    </v-row>

    <v-row class="py-0">
      <v-col class="py-0">
        <category-tree-list :categories-by-id="categoriesById"
                            :categories="filteredCategories"
                            @newCategory="$emit('newCategory', $event)"
                            @deleteCategory="$emit('deleteCategory', $event)"
                            @onReassign="$emit('onReassign', $event)"
                            @edit="$emit('edit', $event)"/>
      </v-col>
    </v-row>
  </v-container>
</template>
