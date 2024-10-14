<script setup lang="ts">
import {inject, onMounted, ref} from "vue";
import {TurnoverImport} from "@api/api.ts";
import {notifierRefKey} from "../../keys.ts";
import TurnoversList from "./TurnoversList.vue";
import WaitingIndicator from "../misc/WaitingIndicator.vue";
import TurnoverImporting from "./importing/TurnoverImporting.vue";
import type {AxiosResponse} from "axios";
import {useApi} from "../../store/use-api.ts";

const notifierRef = inject(notifierRefKey);

const api = useApi()

const isLoading = ref<boolean>(false);
const turnoverImports = ref<TurnoverImport[]>([]);

function loadImports() {
  isLoading.value = true;

  api.turnoversApi.fetchTurnoverImports()
    .then((res: AxiosResponse<TurnoverImport[]>) => turnoverImports.value = res.data)
    .catch(e => notifierRef?.notifyError("Umsätze konnten nicht geladen werden", e))
    .finally(() => isLoading.value = false)
}

function onDelete(fileImport: TurnoverImport) {
  isLoading.value = true;

  api.turnoversApi.deleteTurnoverImport(fileImport.id)
    .then(() => {
      turnoverImports.value = turnoverImports.value.filter(i => i.id !== fileImport.id)
    })
    .catch(e => notifierRef?.notifyError("Umsätze konnte nicht gelöscht werden", e))
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
