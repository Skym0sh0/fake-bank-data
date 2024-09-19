<script setup lang="ts">
import {Category} from "@api/api.ts";
import {computed, ref, watch} from "vue";
import Drag from "../misc/dragndrop/Drag.vue";
import Drop from "../misc/dragndrop/Drop.vue";
import * as _ from "lodash";
import CategoryTreeItemButtons from "./CategoryTreeItemButtons.vue";
import {CategoriesById} from "../misc/categoryHelpers.ts";
import {formatMonetaryAmount} from "../../utils/moneyUtils.ts";
import SelectedCategoryInfo from "./SelectedCategoryInfo.vue";

const {categories, categoriesById} = defineProps<{
  categories: Category[],
  categoriesById: CategoriesById,
}>()

export type NewCategory = { parentId: string };

export type CategoryReassign = { sources: string[]; target: Category };

const emit = defineEmits<{
  (e: 'edit', id: string): void;
  (e: 'newCategory', cat: NewCategory): void;
  (e: 'deleteCategory', cat: Category): void;
  (e: 'onReassign', assignment: CategoryReassign): void;
}>()

const selected = ref<string[]>([]);
const opened = ref<string[]>([]);

const parentCategories = computed(() => {
  return _.sortBy(categories.filter(cat => !cat.parentId), x => x.name.toLowerCase())
})

type CategoryWithChildren = Category & { children: Category[] };

const categoriesAsTree = computed<CategoryWithChildren[]>(() => {
  const resolver = (cat: Category): CategoryWithChildren => {
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

function setOpenRecursively(id: string) {
  const newlyOpened = new Set(opened.value)

  let current: Category | undefined = categoriesById[id]
  while (current) {
    newlyOpened.add(current.id)
    current = current.parentId ? categoriesById[current.parentId] : undefined;
  }

  opened.value = [...newlyOpened]
}

function editCategory(id: string) {
  if (!opened.value.includes(id))
    opened.value.push(id)

  emit("edit", id)
}

function addNewCategoryTo(id: string) {
  emit("newCategory", {parentId: id})
  setOpenRecursively(id)
}

function deleteCategory(id: string) {
  emit("deleteCategory", categoriesById[id])
}

function onDragstart(srcItem: any) {
  if (srcItem)
    selected.value.push(srcItem.id)
}

function onDrop(trgtItem: any /*, srcItem*/) {
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
                <template v-slot:activator="{props}">
                  <v-icon color="red"
                          :small="true"
                          v-bind="props">
                    mdi-finance
                  </v-icon>
                </template>

                Budget:
                {{ formatMonetaryAmount(item.budget.budgetInCents * 100) }}
                +
                {{ item.budget.exceedingThreshold }} %
              </v-tooltip>
            </div>
          </div>
        </drop>
      </template>

      <template v-slot:append="{ item }">
        <category-tree-item-buttons :category="item"
                                    @editCategory="editCategory"
                                    @addNewChildCategory="addNewCategoryTo"
                                    @deleteCategory="deleteCategory"/>
      </template>
    </v-treeview>

    <selected-category-info :selectedIds="selected"
                            :categories-by-id="categoriesById"
                            @clear="clearSelection">
      <template v-slot:prepend>
        <drag @dragstart="onDragstart">
          <v-icon class="drag-point">
            mdi-drag
          </v-icon>
        </drag>
      </template>
    </selected-category-info>
  </v-card>
</template>

<style scoped>
.drag-point {
  cursor: grabbing;
}
</style>
