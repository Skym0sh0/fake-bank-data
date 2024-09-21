<script setup lang="ts">
import {computed, inject, onMounted, ref, watch} from "vue";
import {RawCsvTable, TurnoversApi} from "@api/api.ts";
import {apiRefKey} from "../../../keys.ts";
import * as _ from "lodash";

const api: TurnoversApi | undefined = inject(apiRefKey)?.turnoversApi;

const {file, encoding} = defineProps<{
  file: File;
  encoding?: string;
}>();

const parsedData = ref<RawCsvTable | null>(null);

const dataRows = computed(() => {
  if (!parsedData.value)
    return [];

  return parsedData.value.data.map(row => {
    return row.map(cell => {
      const maxLength = 24;
      const trailer = "...";

      if (!cell)
        return null;

      return _.truncate(cell, {
        length: 24,
      })
    })
  })
});

const emit = defineEmits<{
  (e: 'isLoading', loading: boolean): void;
}>();

function triggerParsing() {
  parsedData.value = null;

  if (!file)
    return;

  emit("isLoading", true)
  api?.processCsvPreview(file, encoding)
    .then((data: RawCsvTable) => {
      parsedData.value = data;
    })
    .finally(() => emit("isLoading", false))
}

watch(() => file, () => triggerParsing())
watch(() => encoding, () => triggerParsing())

onMounted(() => {
  triggerParsing();
})
</script>

<template>
  <v-container v-if="parsedData"
               :fluid="true"
               style="font-size: 0.75em; line-height: 1"
               class="mb-2">
    <v-row :no-gutters="true">
      <v-col>
        <v-data-table :items="dataRows"
                      density="compact"
                      :items-per-page="-1"
                      :hide-default-footer="true"
                      height="50vh"
        />
      </v-col>
    </v-row>

    <v-row :no-gutters="true" class="mt-1">
      <v-col>Bytes: {{ file.size }}</v-col>
      <v-col>Zeilen: {{ parsedData.rows }}</v-col>
      <v-col>Spalten: {{ parsedData.columns }}</v-col>
    </v-row>
  </v-container>
</template>
