<script setup lang="ts">
import {computed, inject, onMounted, ref, watch} from "vue";
import {RawCsvTable} from "@api/api.ts";
import {notifierRefKey} from "../../../keys.ts";
import * as _ from "lodash";
import {useApi} from "../../../store/use-api.ts";

const api = useApi()
const notifierRef = inject(notifierRefKey);

const {file, encoding} = defineProps<{
  file?: File;
  encoding?: string;
  isLoading?: boolean;
}>();

const emit = defineEmits<{
  (e: 'isLoading', loading: boolean): void;
}>();

const parsedData = ref<RawCsvTable | null>(null);

const dataRows = computed(() => {
  if (!parsedData.value)
    return [];

  return (parsedData.value?.data ?? []).map(row => {
    return row.map(cell => {
      if (!cell)
        return null;

      return _.truncate(cell, {length: 24})
    })
  })
});

function triggerParsing() {
  parsedData.value = null;

  if (!file)
    return;

  emit("isLoading", true)

  api.turnoversApi.processCsvPreview(file, encoding)
    .then(data => {
      parsedData.value = data.data;
    })
    .catch(e => notifierRef?.notifyError(`CSV Datei konnte nicht verarbeitet werden`, e))
    .finally(() => emit("isLoading", false))
}

watch(() => file, () => triggerParsing())
watch(() => encoding, () => triggerParsing())

onMounted(() => {
  triggerParsing();
})
</script>

<template>
  <v-container :fluid="true"

               class="mb-2 border">
    <v-row :no-gutters="true">
      <v-col>
        <v-data-table :items="dataRows"
                      :loading="isLoading"
                      density="compact"
                      :items-per-page="-1"
                      :hide-default-footer="true"
                      height="50vh"
        />
      </v-col>
    </v-row>

    <v-row class="mt-1" style="font-size: 0.75em; line-height: 1">
      <v-col>Bytes: {{ file?.size }}</v-col>
      <v-col>Zeilen: {{ parsedData?.rows }}</v-col>
      <v-col>Spalten: {{ parsedData?.columns }}</v-col>
    </v-row>
  </v-container>
</template>
