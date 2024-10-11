<script setup lang="ts">
import {inject, onMounted, ref} from "vue";
import {TurnoverImport, TurnoversApi} from "@api/api.ts";
import {apiRefKey, errorRefKey} from "../../keys.ts";
import TurnoversList from "./TurnoversList.vue";
import WaitingIndicator from "../misc/WaitingIndicator.vue";
import TurnoverImporting from "./importing/TurnoverImporting.vue";
import type {AxiosResponse} from "axios";

const api: TurnoversApi | undefined = inject(apiRefKey)?.turnoversApi;
const errorReference = inject(errorRefKey)?.value;


const isLoading = ref<boolean>(false);
const turnoverImports = ref<TurnoverImport[]>([]);

function loadImports() {
  if (!api)
    return;

  isLoading.value = true;

  api.fetchTurnoverImports()
    .then((res: AxiosResponse<TurnoverImport[]>) => turnoverImports.value = res.data)
    .catch(e => {
      errorReference?.setError(e);
    })
    .finally(() => isLoading.value = false)
}

function onDelete(fileImport: TurnoverImport) {
  if (!api)
    return;

  isLoading.value = true;

  api.deleteTurnoverImport(fileImport.id)
    .then(() => {
      turnoverImports.value = turnoverImports.value.filter(i => i.id !== fileImport.id)
    })
    .then(() => loadImports())
    .finally(() => isLoading.value = false)
}

function uploadSuccess() {
  loadImports()
}

function onReload() {
  loadImports()
}

onMounted(() => {
  loadImports();
})
</script>

<template>
  <v-card>
    <waiting-indicator :is-loading="isLoading"/>

    <v-card-title>
      <div class="d-flex justify-space-between align-items-center">
        <h4>
          Überblick Umsätze
        </h4>

        <div class="d-flex justify-space-between align-center ga-1">
          <v-btn size="small"
                 color="secondary"
                 @click="onReload"
                 :disabled="isLoading"
                 :loading="isLoading">
            Neuladen
          </v-btn>

          <turnover-importing :is-loading="isLoading" @uploadSucceeded="uploadSuccess"/>
        </div>
      </div>
    </v-card-title>

    <v-card-text>
      <turnovers-list :imports="turnoverImports"
                      :is-loading="isLoading"
                      @onDelete="onDelete"/>
    </v-card-text>
  </v-card>
</template>
