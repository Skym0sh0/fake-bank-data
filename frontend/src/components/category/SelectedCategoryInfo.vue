<script setup lang="ts">
import {CategoriesById} from "../misc/categoryHelpers.ts";
import {computed, ref, watch} from "vue";

const {selectedIds, categoriesById} = defineProps<{
  selectedIds: string[];
  categoriesById: CategoriesById;
}>();

const emit = defineEmits<{
  (e: "clear"): void;
}>();

const opened = ref(false);

const isOpen = computed(() => {
  return categoryCount.value > 0;
})

const categoryCount = computed(() => {
  return (selectedIds || []).length;
})

const categories = computed(() => {
  return selectedIds.map(id => categoriesById[id])
    .map(cat => cat.name)
    .sort();
})

function toggleExpand() {
  opened.value = !opened.value;
}

function clearSelection() {
  emit("clear");
}

watch(() => categoryCount.value, (newValue) => {
  if (newValue <= 0) {
    opened.value = false;
  }
})
</script>

<template>
    <v-snackbar :model-value="isOpen"
                :timeout="-1"
                :vertical="true"
                color="info"
                :right="true">
      <div v-show="opened">
        <ul>
          <li v-for="cat in categories" :key="cat">
            {{ cat }}
          </li>
        </ul>
      </div>

      <div class="d-flex justify-content-between align-items-center">
        <div class="d-flex justify-content-start align-items-center">
          <slot name="prepend"/>

          <div class="btn v-card--hover subtitle-2 px-0" @click="clearSelection">
            <v-icon :left="true" :small="true">
              mdi-delete
            </v-icon>
            {{ categoryCount }} ausgewählte Kategorien
          </div>
        </div>

        <v-btn :icon="true"
               :small="true"
               @click="toggleExpand"
               class="m-0">
          <v-icon :dense="true">
            {{ opened ? "mdi-chevron-down" : "mdi-chevron-up" }}
          </v-icon>
        </v-btn>
      </div>
    </v-snackbar>
</template>
