<script setup lang="ts">
import {computed, inject, ref, watch} from "vue";
import {errorRefKey} from "../../keys.ts";

const errorRef = inject(errorRefKey);

const hasError = ref<boolean>(false)

const errorText = computed(() => {
  const err = errorRef?.value?.lastError;
  if (!err)
    return null;

  return err.message
})

watch(
  () => errorRef?.value?.lastError,
  () => {
    hasError.value = !!errorRef?.value?.lastError;
  }
)

function resetError() {
  errorRef?.value.resetError();
  hasError.value = false
}
</script>

<template>
  <v-snackbar v-model="hasError"
              :multi-line="true"
              :timer="true"
              color="red">
    <template v-slot:actions>
      <v-btn
        variant="text"
        @click="resetError">
        Close
      </v-btn>
    </template>

    <template #text>
      {{ errorText }}
    </template>
  </v-snackbar>
</template>
