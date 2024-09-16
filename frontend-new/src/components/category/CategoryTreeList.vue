<script setup lang="ts">
import {Category} from "@api/api.ts";
import {CategoriesByIdMap} from "./CategoryOverview.vue";
import {computed, ref, watch} from "vue";
import {formatMonetaryAmount} from "../../utils/moneyUtils.ts";
import Drag from "../misc/dragndrop/Drag.vue";
import Drop from "../misc/dragndrop/Drop.vue";
import * as _ from "lodash";

const {categories, categoriesById} = defineProps<{
  categories: Category[],
  categoriesById: CategoriesByIdMap,
}>()

const emit = defineEmits<{
  (e: 'edit', id: string): void;
  (e: 'newCategory', cat: { parentId: string }): void;
  (e: 'deleteCategory', cat: Category): void;
  (e: 'onReassign', assignment: { sources: string[]; target: Category }): void;
}>()

const selected = ref<string[]>([]);
const opened = ref<string[]>([]);

const parentCategories = computed(() => {
  return _.sortBy(categories.filter(cat => !cat.parentId), x => x.name.toLowerCase())
})

const categoriesAsTree = computed(() => {
  const resolver = cat => {
    const children = categories.filter(c => c.parentId === cat.id).map(resolver)

    return {
      ...cat,
      children: _.sortBy(children, x => x.name.toLowerCase()),
    }
  }

  return parentCategories.value.map(resolver)
})

function clearSelection() {
  selected.value = [];
}

function clearOpenings() {
  opened.value = [];
}

function setOpenRecursively(id) {
  const newlyOpened = new Set(opened.value)

  let current = categoriesById[id]
  while (current) {
    newlyOpened.add(current.id)
    current = categoriesById[current.parentId]
  }

  opened.value = [...newlyOpened]
}

function editCategory(id) {
  if (!opened.value.includes(id))
    opened.value.push(id)

  emit("edit", id)
}

function addNewCategoryTo(id) {
  emit("newCategory", {parentId: id})
  setOpenRecursively(id)
}

function deleteCategory(id) {
  emit("deleteCategory", categoriesById[id])
}

function onDragstart(srcItem) {
  if (srcItem)
    selected.value.push(srcItem.id)
}

function onDrop(trgtItem/*, srcItem*/) {
  if (selected.value.includes(trgtItem.id))
    return;

  emit("onReassign", {
    sources: selected.value,
    target: trgtItem,
  });

  // this.$nextTick(() => this.opened.push(trgtItem.id, srcItem.id));
}

watch(() => categories, () => clearSelection(), {deep: true})

</script>

<template>
  <v-card>
    <v-treeview :items="categoriesAsTree"
                :active.sync="selected"
                :open.sync="opened"
                :activatable="true"
                :multiple-active="true"
                :selectable="false"
                :hoverable="true"
                :dense="true"
                :return-object="false"
                :transition="true"
                :rounded="false">

      <template v-slot:prepend="{ item }">
        <drop @drop="onDrop(item, ...arguments)">
          <drag :transfer-data="item" @dragstart="onDragstart">
            <v-icon class="drag-point">
              mdi-drag
            </v-icon>
          </drag>
        </drop>
      </template>

      <template v-slot:title="{ item }">
        <drop @drop="onDrop(item, ...arguments)">
          <div class="d-flex justify-content-between">
            <v-badge :content="item.children.length"
                     :value="item.children.length"
                     color="accent"
                     :inline="true">
              {{ item.name }}
            </v-badge>

            <div class="d-flex" style="gap: 0.25em">
              <v-icon v-if="item.isNew" color="success">
                mdi-new-box
              </v-icon>

              <v-tooltip v-if="item.budget" :top="true">
                <template v-slot:activator="{on, attrs}">
                  <v-icon color="red"
                          :small="true"
                          v-bind="attrs"
                          v-on="on">
                    mdi-finance
                  </v-icon>
                </template>

                Budget: {{ formatMonetaryAmount(item.budget.budget * 100) }} + {{ item.budget.exceedingThresholdPercent }} %
              </v-tooltip>
            </div>
          </div>
        </drop>
      </template>

      <template v-slot:append="{ item }">
<!--        <category-tree-item-buttons :category="item"-->
<!--                                    @editCategory="editCategory"-->
<!--                                    @addNewChildCategory="addNewCategoryTo"-->
<!--                                    @deleteCategory="deleteCategory"/>-->
      </template>
    </v-treeview>

<!--    <selected-category-info :selectedIds="selected"-->
<!--                            :categories-by-id="categoriesById"-->
<!--                            @clear="clearSelection">-->
<!--      <template v-slot:prepend>-->
<!--        <drag @dragstart="onDragstart">-->
<!--          <v-icon class="drag-point">-->
<!--            mdi-drag-->
<!--          </v-icon>-->
<!--        </drag>-->
<!--      </template>-->
<!--    </selected-category-info>-->
  </v-card>
</template>

<style scoped>
.drag-point {
  cursor: grabbing;
}
</style>
