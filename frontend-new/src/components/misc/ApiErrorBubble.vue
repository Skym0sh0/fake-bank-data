<script setup lang="ts">
import {DateTime} from "luxon";
import {computed, inject} from "vue";
import {errorRefKey} from "../../keys.ts";

const errorRef = inject(errorRefKey);

const hasError = computed(() => {
  return !!errorRef.value.lastError;
})
const timestamp = computed(() => {
  if (!hasError.value)
    return null;

  return DateTime.fromISO(errorRef.value.lastError.timestamp)
      .toLocaleString(DateTime.DATETIME_MED_WITH_SECONDS)
})

const status = computed(() => {
  if (!hasError.value)
    return null;

  return errorRef.value.lastError.status
})

const target = computed(() => {
  if (!hasError.value)
    return null;

  return errorRef.value.lastError.path
})

const error = computed(() => {
  if (!hasError.value)
    return null;

  return errorRef.value.lastError.error
})

const details = computed(() => {
  if (!hasError.value)
    return null;

  return errorRef.value.lastError.errorDetails
})

function resetError() {
  errorRef.value.resetError();
}
</script>

<template>
  <v-snackbar :value="hasError"
              :multi-line="true"
              color="red"
              :timeout="-1">
    <template v-slot:action>
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
</template>
