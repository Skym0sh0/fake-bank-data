<script setup lang="ts">
import {DateTime} from "luxon";
import {computed, inject, ref, watch} from "vue";
import {errorRefKey} from "../../keys.ts";

const errorRef = inject(errorRefKey);

const lastTimeout = ref<ReturnType<typeof setInterval>>()

watch(
  () => errorRef?.value?.lastError,
  () => {
    console.log("on change", errorRef?.value?.lastError)

    if (lastTimeout.value) {
      clearTimeout(lastTimeout.value);
      lastTimeout.value = undefined
    }

    if (!!errorRef?.value?.lastError)
      lastTimeout.value = setTimeout(() => {
        errorRef?.value?.resetError()
      }, 2500)
  }
)

const hasError = computed(() => {
  return !!errorRef?.value.lastError;
})
const timestamp = computed(() => {
  if (!hasError.value || !errorRef?.value.lastError?.timestamp)
    return null;

  return DateTime.fromISO(errorRef.value.lastError.timestamp)
    .toLocaleString(DateTime.DATETIME_MED_WITH_SECONDS)
})

const status = computed(() => {
  if (!hasError.value)
    return null;

  return errorRef?.value.lastError?.status
})

const target = computed(() => {
  if (!hasError.value)
    return null;

  return errorRef?.value.lastError?.path
})

const error = computed(() => {
  if (!hasError.value)
    return null;

  return errorRef?.value.lastError?.error
})

const details = computed(() => {
  if (!hasError.value)
    return null;

  return errorRef?.value.lastError?.errorDetails
})

function resetError() {
  errorRef?.value.resetError();
}
</script>

<template>
  <div>
    <div>
      Bla bla bla ({{ lastTimeout }}):
      <p v-if="hasError">"{{ errorRef?.lastError }}"</p>
    </div>

    <v-snackbar :value="hasError"
                :multi-line="true"
                color="red"
                :timeout="-1">
      <template v-slot:actions>
        <v-btn
          variant="text"
          @click="resetError">
          Close
        </v-btn>
      </template>

      <template v-slot:default>
        <h5> {{ error }} - {{ status }} </h5>
        <h6> {{ target }}</h6>

        <div>
          {{ details }}
        </div>

        <div style="font-size: 0.675em">
          {{ timestamp }}
        </div>
      </template>
    </v-snackbar>
  </div>
</template>
