<script setup lang="ts">
import {Category} from "@api/api.ts";
import {computed} from "vue";
import CategoryVolumeGraph from "./graphs/CategoryVolumeGraph.vue";

const {category} = defineProps<{
  category: Category;
}>();

const emit = defineEmits<{
  (e: 'editCategory', categoryId: string): void;
  (e: 'addNewChildCategory', categoryId: string): void;
  (e: 'deleteCategory', categoryId: string): void;
}>();

const isRootCategory = computed(() => {
  return !category.parentId;
})

const hasChildren = computed(() => {
  return category.children && category.children.length > 0;
})

const isUsed = computed(() => {
  return category.usageCount > 0;
})

const isDeletionForbidden = computed(() => {
  return hasChildren.value || (isRootCategory.value && isUsed.value);
})

const deletionButtonTooltip = computed(() => {
  if (!isDeletionForbidden.value) {
    return "Kategorie löschen";
  }

  if (hasChildren.value)
    return "Kategorie kann nicht gelöscht werden, da noch Unterkategorien existieren."

  return `Die Kategorie kann nicht gelöscht werden, da sie noch in ${category.usageCount} Turnovers benutzt wird.`;
})

function editCategory() {
  emit('editCategory', category.id);
}

function addNewCategoryTo() {
  emit('addNewChildCategory', category.id)
}

function deleteCategory() {
  emit('deleteCategory', category.id)
}
</script>

<template>
  <div>
    <v-divider :vertical="true"/>

    <v-btn-group>
            <category-volume-graph :category="category">
              <template v-slot:button="{ clickCallback }">
                <v-btn :icon="true"
                       :small="true"
                       color="warning"
                       @click.stop="clickCallback"
                       title="Graph">
                  <v-icon :small="true">
                    mdi-chart-timeline-variant-shimmer
                  </v-icon>
                </v-btn>
              </template>
            </category-volume-graph>

      <!--      <category-usage-dialog :category="category">-->
      <!--        <template v-slot:button="{ clickCallback }">-->
      <!--          <v-tooltip :top="true">-->
      <!--            <template v-slot:activator="{ on, attrs }">-->
      <!--              <v-btn :icon="true"-->
      <!--                     :small="true"-->
      <!--                     color="warning"-->
      <!--                     @click.stop="clickCallback"-->
      <!--                     v-bind="attrs" v-on="on">-->
      <!--                <v-icon :small="true">-->
      <!--                  mdi-format-list-bulleted-->
      <!--                </v-icon>-->
      <!--              </v-btn>-->
      <!--            </template>-->
      <!--            Zeige benutzte Turnovers-->
      <!--          </v-tooltip>-->
      <!--        </template>-->
      <!--      </category-usage-dialog>-->

      <v-tooltip :top="true">
        <template v-slot:activator="{ props }">
          <div v-bind="props">
            <v-btn :icon="true"
                   :small="true"
                   color="success"
                   @click.stop="editCategory">
              <v-icon :small="true">
                mdi-playlist-edit
              </v-icon>
            </v-btn>
          </div>
        </template>
        Kategorie editieren
      </v-tooltip>

      <v-tooltip :top="true">
        <template v-slot:activator="{ props }">
          <div v-bind="props">
            <v-btn :icon="true"
                   :small="true"
                   color="purple"
                   @click.stop="addNewCategoryTo">
              <v-icon :small="true">
                mdi-playlist-plus
              </v-icon>
            </v-btn>
          </div>
        </template>
        Erstelle neue Unterkategorie
      </v-tooltip>

      <v-tooltip :top="true">
        <template v-slot:activator="{ props }">
          <div v-bind="props">
            <v-btn :icon="true"
                   :small="true"
                   color="error"
                   @click.stop="deleteCategory"
                   :disabled="isDeletionForbidden">
              <v-icon :small="true">
                mdi-trash-can-outline
              </v-icon>
            </v-btn>
          </div>
        </template>
        {{ deletionButtonTooltip }}
      </v-tooltip>
    </v-btn-group>
  </div>
</template>
