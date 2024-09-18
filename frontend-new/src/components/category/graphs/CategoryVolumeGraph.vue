<script setup lang="ts">
import {Category, CategoryTurnoverReport, CategoryTurnoverReportDatapoint, TurnoversApi} from "@api/api.ts";
import {computed, inject, ref, useTemplateRef, watch} from "vue";
import {apiRefKey} from "../../../keys.ts";
import {DateTime} from "luxon";
import Waiter from "../../misc/Waiter.vue";
import CategoryGraph from "./CategoryGraph.vue";

const api: TurnoversApi | undefined = inject(apiRefKey)?.turnoversApi;

const {category} = defineProps<{
  category: Category;
}>();

const isOpen = ref(false)

const isLoading = ref(false)
const referencedRows = ref<CategoryTurnoverReportDatapoint[]>([])
const grouping = ref("DAY")
const includeSubcategories = ref(false)

const modalDialog = useTemplateRef("modal-turnovers-graph");

const groupingKeys = computed(() => {
  return [
    {value: "DAY", title: "Tag",},
    {value: "MONTH", title: "Monat",},
    {value: "YEAR", title: "Jahr",},
  ];
})

export type GraphDataPoint = {
  date: Date;
  income: number;
  expense: number;
  budget: number;
}

const graphData = computed<GraphDataPoint[]>(() => {
  console.log("computed")
  return referencedRows.value.map(rec => ({
    date: DateTime.fromISO(rec.date).toJSDate(),
    income: rec.incomeAmountInCents / 100.0,
    expense: rec.expenseAmountInCents / 100.0,
    budget: -rec.expenseBudgetInCents / 100.0,
  }))
})

function onOpenModal() {
  loadData()

  isOpen.value = true
}

function loadData() {
  isLoading.value = true;

  referencedRows.value = [];

  api?.fetchTurnoversReportForCategory(category.id, grouping.value, includeSubcategories.value ? 1 << 16 : 1)
      .then((res: CategoryTurnoverReport) => {
        referencedRows.value = res.datapoints ?? []
      })
      .finally(() => isLoading.value = false)
}


watch(() => includeSubcategories.value, () => loadData())
watch(() => grouping.value, () => loadData())

</script>

<template>
  <div>
    <slot name="button" :clickCallback="onOpenModal">
      <v-btn id="usage-count"
             variant="text"
             :pill="true"
             size="sm"
             @click="onOpenModal"
             :title="`Diese Kategorie wird in ${category.usageCount} Transaktionen benutzt.`">
        {{ category.usageCount }}
      </v-btn>
    </slot>

    <v-dialog ref="modal-turnovers-graph"
              v-model="isOpen"
              :max-width="1200"
              :max-height="800"
              transition="dialog-top-transition">
      <v-card>
        <v-card-title>
          Turnovers Graph für Kategorie {{ category.name }}
        </v-card-title>

        <v-card-text>
          <v-container :fluid="true" class="mw-100 p-0">
            <v-row>
              <v-col>
                <waiter :is-loading="isLoading">
                  <category-graph v-if="graphData.length > 0" :data="graphData"/>
                  <div v-else>
                    Keine Daten vorhanden
                  </div>
                </waiter>
              </v-col>
            </v-row>

            <v-row class="d-flex justify-content-center">
              <v-col :cols="2">
                <v-select v-model="grouping"
                          :items="groupingKeys"
                          label="Zeitraum Gruppierung"/>
              </v-col>

              <v-col :cols="2">
                <v-checkbox v-model="includeSubcategories"
                            label="mit Unterkategorien"/>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>
