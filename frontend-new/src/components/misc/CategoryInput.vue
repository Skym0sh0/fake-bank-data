<script setup lang="ts">
import {computed, onMounted, ref, watch} from "vue";
import {CategoriesById, CategoriesByName, CategoryWithParentNamesChained} from "./categoryHelpers.ts";

const {
  id,
  value,
  disabled = false,
  loading = false,
  flattedCategories,
  categoriesById,
  categoriesByName,
  state = true,
  required = true
} = defineProps<{
  id: string;
  value?: string | null;
  disabled?: boolean;
  loading?: boolean;
  flattedCategories: CategoryWithParentNamesChained<string>[];
  categoriesById: CategoriesById;
  categoriesByName: CategoriesByName;
  state?: boolean;
  required?: boolean;
}>();

const emit = defineEmits<{
  (e: "input", id: string | undefined | null): void;
  (e: "createCategory", name: string): void;
}>();

const currentSearch = ref("");
const isUnknownCategory = ref(false);

const isValidState = computed<boolean>(() => {
  return state && !isUnknownCategory.value && !isCurrentSearchInvalid.value;
})

const isCurrentSearchInvalid = computed<boolean>(() => {
  return !currentSearch.value || currentSearch.value === "";
})

const isValidationTrue = computed<boolean | undefined>(() => {
  if (!required) {
    if (isUnknownCategory.value && currentSearch.value !== '')
      return false;

    return undefined;
  }

  return isValidState.value
})

const isAddableCategory = computed<boolean>(() => {
  return !(!isUnknownCategory.value || isCurrentSearchInvalid.value)
})

const isDisabled = computed(() => {
  return disabled || loading
})

function onCategoryInput(newValue: string) {
  currentSearch.value = newValue;

  findOption()
}

function findOption() {
  const foundCategory = categoriesByName[currentSearch.value];
  if (foundCategory) {
    isUnknownCategory.value = false;
    emit('input', foundCategory.id)
  } else {
    isUnknownCategory.value = true;
    emit('input', null)
  }
}

function onAddCategory() {
  emit('createCategory', currentSearch.value)
}

function initWhenOnlyValueIsSet() {
  if (value) {
    const foundCategory = categoriesById[value];
    if (foundCategory) {
      currentSearch.value = foundCategory.name;
      isUnknownCategory.value = false;
      emit('input', foundCategory.id);
    } else {
      currentSearch.value = "";
      isUnknownCategory.value = true;
      emit('input', null);
    }
  }
}

onMounted(() => {
  initWhenOnlyValueIsSet();
})

watch(() => value, () => initWhenOnlyValueIsSet(), {deep: true})
watch(() => categoriesByName, () => findOption(), {deep: true})

</script>

<template>
  <v-text-field :id="`${id}-category-input`"
                :list="`${id}-category-input-list`"
                :value="currentSearch"
                @update:modelValue="onCategoryInput"
                :error="!isValidationTrue"
                :disabled="isDisabled"
                density="compact"
                type="text"
                autocomplete="off"
                :hide-details="true">

    <template v-slot:append-inner>
      <v-icon color="success" v-if="isValidationTrue">
        mdi-check
      </v-icon>

      <v-tooltip v-if="!isValidationTrue">
        <template v-slot:activator="{ props }">
          <v-icon color="error" v-bind="props">
            mdi-alert-circle
          </v-icon>
        </template>

        Unbekannte Kategorie "{{ currentSearch }}"
      </v-tooltip>

      <v-btn v-if="!isDisabled"
             @click="onAddCategory"
             :disabled="!isAddableCategory || loading"
             :icon="loading ? 'mdi-timer-sand' : 'mdi-plus'"
             title="Erstelle diese Kategorie neu"
             color="primary"
             size="small"
             variant="flat"
             density="compact"/>
    </template>

    <template v-slot:append>

    </template>
  </v-text-field>

  <datalist :id="`${id}-category-input-list`">
    <option v-for="cat in flattedCategories" :key="cat.id" :value="cat.name">
      {{ cat.parentChain }}
    </option>
  </datalist>
</template>
