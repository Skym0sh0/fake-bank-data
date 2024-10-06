<script setup lang="ts">
import {formatMonetaryAmount} from "../../../utils/moneyUtils.ts";
import {CategoryBudget} from "@api/api.ts";
import {computed} from "vue";

const {budget} = defineProps<{
  budget: CategoryBudget;
}>()

const tooltipText = computed(() => {
  return `Budget: ${formatMonetaryAmount(budget.budgetInCents * 100)} \u00B1 ${budget.exceedingThreshold} %`;
});
</script>

<template>
  <v-tooltip location="top">
    <template v-slot:activator="{ props }">
      <v-icon color="red"
              :small="true"
              v-bind="props">
        mdi-finance
      </v-icon>
    </template>

    <template v-slot:default>
      {{ tooltipText }}
    </template>
  </v-tooltip>
</template>
