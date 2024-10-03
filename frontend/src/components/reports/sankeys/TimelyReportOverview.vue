<script setup lang="ts">
import WaitingIndicator from "../../misc/WaitingIndicator.vue";
import {computed, inject, ref} from "vue";
import {DateTime} from "luxon";
import {BasicCoarseInfo, ReportsApi, ReportTimeUnits} from "@api/api.ts";
import {apiRefKey} from "../../../keys.ts";
import TimeboxSelector from "./TimeboxSelector.vue";
import {AssociationsType, BasicInfo, SelectType} from "./types.ts";
import IncomeExpenseSankeyReport from "./IncomeExpenseSankeyReport.vue";

const api: ReportsApi | undefined = inject(apiRefKey)?.reportsApi;

const now = DateTime.now();

const isLoading = ref(false);
const basicInfo = ref<BasicInfo | null>(null);

const select = ref<SelectType>({
  mode: AssociationsType.Relative,
  depth: 2,

  year: now.year,
  month: now.month,

  referenceDate: now,
  timeunit: ReportTimeUnits.Months,
  units: 6,
});

const isDataQueryable = computed(() => {
  return !!basicInfo.value && basicInfo.value.numberOfTransactions > 0 && !!basicInfo.value.earliest && !!basicInfo.value.latest;
})

function loadBasicInfo() {
  isLoading.value = true

  api?.fetchCoarseInfos()
    .then(resp => {
      const res: BasicCoarseInfo = resp.data;

      basicInfo.value = {
        earliest: res.earliestTransaction ? DateTime.fromISO(res.earliestTransaction) : now,
        latest: res.latestTransaction ? DateTime.fromISO(res.latestTransaction) : now,
        maxDepth: res.maxDepthOfCategories ?? 0,
        numberOfTransactions: res.numberOfTransactions ?? 0,
        numberOfUsedCategories: res.numberOfUsedCategories ?? 0,
      };

      select.value.depth = Math.min(2, basicInfo.value.maxDepth)
      select.value.year = basicInfo.value.latest.year
      select.value.month = undefined
    })
    .finally(() => isLoading.value = false)
}

loadBasicInfo()
</script>

<template>
  <v-card>
    <template v-slot:title>
      Timely Reports
    </template>

    <template v-slot:subtitle v-if="isDataQueryable">
      <v-row>
        <v-col v-if="basicInfo" class="py-1 d-flex justify-space-around align-center">
          <div>
            Transaktionen: {{ basicInfo.numberOfTransactions }}
          </div>
          <div>
            Kategorien: {{ basicInfo.numberOfUsedCategories }}
          </div>
        </v-col>
      </v-row>

      <v-row>
        <v-col class="py-0">
          <timebox-selector v-if="basicInfo"
                            :value="select"
                            :earliest="basicInfo.earliest"
                            :latest="basicInfo.latest"
                            :max-depth="basicInfo.maxDepth"/>
        </v-col>
      </v-row>
    </template>

    <template v-slot:text>
      <income-expense-sankey-report v-if="isDataQueryable"
                                    :height="800"
                                    :select="select"/>

      <div v-if="!isDataQueryable">
        No data present
      </div>
    </template>

    <waiting-indicator :is-loading="isLoading"/>
  </v-card>
</template>
