<script setup lang="ts">
import {inject, onMounted, ref} from "vue";
import {TurnoverImport, TurnoversApi} from "@api/api.ts";
import {apiRefKey} from "../../keys.ts";
import TurnoversList from "./TurnoversList.vue";
import WaitingIndicator from "../misc/WaitingIndicator.vue";

const api: TurnoversApi | undefined = inject(apiRefKey)?.turnoversApi;

const isLoading = ref<boolean>(false);
const turnoverImports = ref<TurnoverImport[]>([]);

function loadImports() {
  isLoading.value = true;

  api?.fetchTurnoverImports()
    .then((imports: TurnoverImport[]) => turnoverImports.value = imports)
    .finally(() => isLoading.value = false)
}

function onDelete(fileImport: TurnoverImport) {
  isLoading.value = true;

  api?.deleteTurnoverImport(fileImport.id)
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
      <div class="d-flex justify-content-between align-items-center">
        <h4>
          Überblick Umsätze
        </h4>

        <v-btn-group>
          <v-btn color="secondary" @click="onReload" :disabled="isLoading">
            Neuladen
          </v-btn>

          <!--          <turnover-importing @uploadSucceeded="uploadSuccess"/>-->
        </v-btn-group>
      </div>
    </v-card-title>

    <v-card-text>
      <turnovers-list :imports="turnoverImports"
                      :is-loading="isLoading"
                      @onDelete="onDelete"/>
    </v-card-text>
  </v-card>
</template>
