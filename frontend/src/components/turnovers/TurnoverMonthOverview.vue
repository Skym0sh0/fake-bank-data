<script setup lang="ts">
import {onMounted, ref} from "vue";
import {TurnoverRow} from "@api/api.ts";
import WaitingIndicator from "../misc/WaitingIndicator.vue";
import {useApi} from "../../store/use-api.ts";
import {useNotification} from "../../store/use-notification.ts";
import TableCellDescription from "../misc/TableCellDescription.vue";
import CategoryInput from "../misc/CategoryInput.vue";
import TableCellMonetary from "../misc/TableCellMonetary.vue";

const notification = useNotification();

const api = useApi()

const isLoading = ref<boolean>(false);
const turnovers = ref<TurnoverRow[]>([]);

function loadRows() {
  isLoading.value = true;

  api.turnoversApi.fetchTurnoverRows()
    .then(res => turnovers.value = res.data)
    .catch(e => notification.notifyError("Umsätze konnten nicht geladen werden", e))
    .finally(() => isLoading.value = false)
}

function uploadSuccess() {
  loadRows()
}

function onReload() {
  loadRows()
}

onMounted(() => {
  loadRows();
})
</script>

<template>
  <v-card>
    <waiting-indicator :is-loading="isLoading"/>

    <v-card-title>
      <div class="d-flex justify-start align-items-center">
        <h4>
          Überblick monatliche Umsätze
        </h4>
      </div>
    </v-card-title>

    <v-card-text>
      <div>
        <table>
          <tbody>
          <tr v-for="(turnover, idx) in turnovers" :key="turnover.id">
            <th>{{ idx + 1 }}</th>
            <td>{{ turnover.date }}</td>
            <td>{{ turnover.recipient }}</td>
            <td>
              <table-cell-monetary :value="turnover.amountInCents"/>
            </td>
            <td>
              <category-input :id="turnover.id" :value="turnover.categoryId"></category-input>
            </td>
            <td>
              <table-cell-description :value="turnover.description"/>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </v-card-text>
  </v-card>
</template>
