<script setup lang="ts">
import {Category} from "@api/api.ts";
import {computed} from "vue";
import CategoryVolumeGraph from "./graphs/CategoryVolumeGraph.vue";
import CategoryUsageDialog from "./graphs/CategoryUsageDialog.vue";

const {category} = defineProps<{
  category: Category;
}>();

const emit = defineEmits<{
  (e: 'refresh'): void;
  (e: 'editCategory', categoryId: string): void;
  (e: 'addNewChildCategory', categoryId: string): void;
  (e: 'deleteCategory', categoryId: string): void;
}>();

const isRootCategory = computed(() => {
  return !category.parentId;
})

const hasChildren = computed<boolean>(() => {
  // return category.children && category.children.length > 0;
  return false;
})

const isUsed = computed<boolean>(() => {
  return (category.usageCount ?? 0) > 0;
})

const isDeletionForbidden = computed<boolean>(() => {
  return hasChildren.value || (isRootCategory.value && isUsed.value);
})

const deletionButtonTooltip = computed<string>(() => {
  if (!isDeletionForbidden.value) {
    return "Kategorie löschen";
  }

  if (hasChildren.value)
    return "Kategorie kann nicht gelöscht werden, da noch Unterkategorien existieren."

  return `Die Kategorie kann nicht gelöscht werden, da sie noch in ${category.usageCount} Turnovers benutzt wird.`;
})

function onRefresh() {
  emit("refresh")
}

function editCategory() {
  if (category.id)
    emit('editCategory', category.id);
}

function addNewCategoryTo() {
  if (category.id)
    emit('addNewChildCategory', category.id)
}

function deleteCategory() {
  if (category.id)
    emit('deleteCategory', category.id)
}
</script>

<template>
  <div class="ml-1 d-flex ga-2">
    <v-btn-group size="small"
                 density="compact">
      <category-volume-graph :category="category">
        <template v-slot:button="{ clickCallback }">
          <v-tooltip location="top">
            <template v-slot:activator="{ props }">
              <v-btn icon="mdi-chart-timeline-variant-shimmer"
                     color="warning"
                     @click.stop="clickCallback"
                     v-bind="props"/>
            </template>

            Zeige Graph
          </v-tooltip>
        </template>
      </category-volume-graph>

      <category-usage-dialog :category="category" @refresh="onRefresh">
        <template v-slot:button="{ clickCallback }">
          <v-tooltip location="top">
            <template v-slot:activator="{ props }">
              <v-btn icon="mdi-format-list-bulleted"
                     color="primary"
                     @click.stop="clickCallback"
                     v-bind="props"/>
            </template>

            Zeige {{ category.usageCount }} benutzte Turnovers
          </v-tooltip>
        </template>
      </category-usage-dialog>
    </v-btn-group>

    <v-btn-group size="small"
                 density="compact">
      <v-tooltip location="top">
        <template v-slot:activator="{ props }">
          <v-btn icon="mdi-playlist-edit"
                 color="success"
                 @click.stop="editCategory"
                 v-bind="props"/>
        </template>

        Kategorie editieren
      </v-tooltip>

      <v-tooltip location="top">
        <template v-slot:activator="{ props }">
          <v-btn icon="mdi-playlist-plus"
                 color="purple"
                 @click.stop="addNewCategoryTo"
                 v-bind="props"/>
        </template>

        Erstelle neue Unterkategorie
      </v-tooltip>

      <v-tooltip location="top">
        <template v-slot:activator="{ props }">
          <v-btn icon="mdi-trash-can-outline"
                 color="error"
                 @click.stop="deleteCategory"
                 :disabled="isDeletionForbidden"
                 v-bind="props"/>
        </template>

        {{ deletionButtonTooltip }}
      </v-tooltip>
    </v-btn-group>
  </div>
</template>
