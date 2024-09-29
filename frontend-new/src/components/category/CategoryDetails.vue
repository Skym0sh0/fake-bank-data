<script setup lang="ts">
import {CategoriesById} from "../misc/categoryHelpers.ts";
import {Category, CategoryPatch} from "@api/api.ts";
import {computed, ref, useTemplateRef, watch} from "vue";
import CategoryUsageDialog from "./graphs/CategoryUsageDialog.vue";
import Breadcrumps from "../misc/Breadcrumps.vue";
import CategoryInfos from "./CategoryInfos.vue";
import {VForm} from "vuetify/components";
import BudgetArea from "./BudgetArea.vue";

const {categoriesById, entity, isNew, isLoading} = defineProps<{
  categoriesById: CategoriesById,
  entity: Category;
  isNew: boolean;
  isLoading: boolean;
}>();

const emit = defineEmits<{
  (e: "createAsChild", patch: CategoryPatch): void;
  (e: "createAsRoot", patch: CategoryPatch): void;
  (e: "update", patch: CategoryPatch): void;
  (e: "close"): void;
  (e: "refresh"): void;
}>();

defineExpose({
  reset
})

const hasChanged = ref(false);

const formRef = useTemplateRef<VForm>("details-form")

const isChild = computed(() => {
  return !!entity.parentId;
})

const allParentCategoryChain = computed(() => {
  let cur = entity.parentId;

  const chain = [entity.name];
  while (cur) {
    const current = categoriesById[cur];
    chain.push(current.name);
    cur = current.parentId;
  }
  return chain.reverse();
})

const isSaveable = computed(() => {
  return hasChanged.value && formRef.value?.isValid;
})

function reset() {
  formRef.value?.resetValidation()
}

function changeAnything() {
  hasChanged.value = true;
}

function saveActiveForm() {
  formRef.value?.validate()
    .then(validation => {
      if (validation.valid) {
        const normalized: CategoryPatch = entity

        if (isNew) {
          if (isChild.value) {
            emit("createAsChild", normalized)
          } else {
            emit("createAsRoot", normalized)
          }
        } else {
          emit("update", normalized)
        }

        hasChanged.value = false;
      }
    })
}

function cancelActiveForm() {
  emit("close");
}

function onNewBudget() {
  entity.budget = {
    budgetInCents: null,
    exceedingThreshold: null,
  }
  changeAnything()
}

function onDeleteBudget() {
  entity.budget = null
  changeAnything()
}

watch(() => entity, () => changeAnything(), {deep: true})
</script>

<template>
  <v-card class="position-sticky">
    <v-card-title class="d-flex justify-space-between">
      <span>Kategorie Details</span>

      <category-usage-dialog v-if="entity && entity.id"
                             :category="entity"
                             @refresh="emit('refresh')"/>
    </v-card-title>

    <v-card-subtitle class="d-flex justify-start">
      <breadcrumps :items="allParentCategoryChain"/>
    </v-card-subtitle>

    <v-card-text>
      <v-form ref="details-form" class="d-flex flex-column ga-2">
        <v-text-field v-model="entity.name"
                      label="Name"
                      placeholder="Name"
                      :rules="[value => !!value || 'Name is required']"
                      :outlined="true"
                      @input="changeAnything"/>

        <v-textarea v-model="entity.description"
                    label="Beschreibung"
                    placeholder="Beschreibung"
                    :outlined="true"
                    :auto-grow="true"
                    @input="changeAnything"/>

        <v-divider/>

        <budget-area :value="entity.budget"
                     @newBudget="onNewBudget"
                     @deleteBudget="onDeleteBudget"
                     @changed="changeAnything"/>

        <v-divider/>

        <category-infos :entity="entity"/>

        <div class="d-flex justify-end mt-2">
          <v-btn-group>
            <v-btn @click="cancelActiveForm"
                   :loading="isLoading"
                   :small="true"
                   color="warning">
              Abbrechen
            </v-btn>
            <v-btn @click="saveActiveForm"
                   :disabled="!isSaveable"
                   :loading="isLoading"
                   :small="true"
                   color="success">
              Speichern
            </v-btn>
          </v-btn-group>
        </div>
      </v-form>
    </v-card-text>
  </v-card>
</template>
