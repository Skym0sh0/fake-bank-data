<script setup lang="ts">
import {Category} from "@api/api.ts";
import {computed, onMounted, ref, watch} from "vue";

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
  flattedCategories: Category[];
  categoriesById: { [id: string]: Category };
  categoriesByName: { [name: string]: Category };
  state?: boolean;
  required?: boolean;
}>();


const currentSearch = ref("");
const isUnknownCategory = ref(false);

const isValidState = computed(() => {
  return state && !isUnknownCategory.value && !isCurrentSearchInvalid.value;
})

const isCurrentSearchInvalid = computed(() => {
  return !currentSearch.value || currentSearch.value === "";
})

const showValidationState = computed(() => {
  if (!required) {
    if (isUnknownCategory.value && currentSearch.value !== '')
      return false;

    return null;
  }

  return isValidState.value
})

const isAddableCategory = computed(() => {
  return !(!isUnknownCategory.value || isCurrentSearchInvalid.value)
})

function onCategoryInput(newValue) {
  currentSearch.value = newValue;

  findOption()
}

function findOption() {
  const foundCategory = categoriesByName[currentSearch.value];
  if (foundCategory) {
    isUnknownCategory.value = false;
    this.$emit('input', foundCategory.id)
  } else {
    isUnknownCategory.value = true;
    this.$emit('input', null)
  }
}

function onAddCategory() {
  this.$emit('createCategory', currentSearch.value)
}

function initWhenOnlyValueIsSet() {
  if (value) {
    const foundCategory = categoriesById[value];
    if (foundCategory) {
      currentSearch.value = foundCategory.name;
      isUnknownCategory.value = false;
      this.$emit('input', foundCategory.id);
    } else {
      currentSearch.value = "";
      isUnknownCategory.value = true;
      this.$emit('input', null);
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
                @input="onCategoryInput"
                :error="showValidationState"
                :disabled="disabled || loading"
                density="compact"
                type="text"
                autocomplete="off">

    <template v-slot:append>
      <v-btn @click="onAddCategory"
             :icon="true"
             :disabled="!isAddableCategory || loading">
        <v-icon :small="true" v-if="loading">
          mdi-timer-sand
        </v-icon>
        <v-icon :small="true" v-else>
          mdi-plus-circle
        </v-icon>
      </v-btn>
    </template>
  </v-text-field>
  <datalist :id="`${id}-category-input-list`">
    <option v-for="cat in flattedCategories" :key="cat.id" :value="cat.name">
      {{ cat.parentChain }}
    </option>
  </datalist>

  <!--  <b-input-group>-->
  <!--    <b-form-input :id="`${id}-category-input`"-->
  <!--                  :list="`${id}-category-input-list`"-->
  <!--                  @input="onCategoryInput"-->
  <!--                  :value="currentSearch"-->
  <!--                  :state="showValidationState"-->
  <!--                  :disabled="disabled || loading"-->
  <!--                  autocomplete="off"-->
  <!--                  size="sm"-->
  <!--                  type="text">-->
  <!--    </b-form-input>-->
  <!--    <b-input-group-append>-->
  <!--      <b-button size="sm"-->
  <!--                @click="onAddCategory"-->
  <!--                :variant="isAddableCategory ? 'primary' : 'secondary'"-->
  <!--                :disabled="!isAddableCategory || loading">-->
  <!--        <b-icon :icon="!loading ? 'plus-circle' : 'hourglas-split'" font-scale="1"/>-->
  <!--      </b-button>-->
  <!--    </b-input-group-append>-->

  <!--    <datalist :id="`${id}-category-input-list`">-->
  <!--      <option v-for="cat in flattedCategories" :key="cat.id" :value="cat.name">-->
  <!--        {{ cat.parentChain }}-->
  <!--      </option>-->
  <!--    </datalist>-->
  <!--  </b-input-group>-->
</template>
