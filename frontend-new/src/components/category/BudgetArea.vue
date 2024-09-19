<script setup lang="ts">
import {CategoryBudget} from "@api/api.ts";
import {computed, ref, watch} from "vue";

const {value} = defineProps<{
  value: CategoryBudget | null | undefined;
}>();

const emit = defineEmits<{
  (e: "newBudget"): void;
  (e: "deleteBudget"): void;
  (e: "changed"): void;
}>();

const budgetInEuro = ref((value?.budgetInCents ?? 0) / 100);
const thresholdInPercent = ref((value?.exceedingThreshold ?? 0.1) * 100);

watch(() => budgetInEuro.value, () => {
  if (value) {
    value.budgetInCents = budgetInEuro.value * 100;
  }
});
watch(() => thresholdInPercent.value, () => {
  if (value) {
    value.exceedingThreshold = thresholdInPercent.value / 100.0;
  }
});

const isBudgetAbsent = computed(() => {
  return value === null || value === undefined || !value;
})

const budget = computed(() => {
  return budgetInEuro.value;
})

const budgetFormatted = computed(() => {
  return formatMonetary(budget.value)
})

const exceedingThreshold = computed(() => {
  return thresholdInPercent.value/100.0;
})

const budgetThresholdFraction = computed(() => {
  return budget.value * exceedingThreshold.value;
})

const budgetThresholdFractionFormatted = computed(() => {
  return formatMonetary(budgetThresholdFraction.value)
})

const thresholdRange = computed(() => {
  return [
    budget.value - budgetThresholdFraction.value,
    budget.value + budgetThresholdFraction.value
  ];
})

const thresholdRangeFormatted = computed(() => {
  return thresholdRange.value.map(formatMonetary)
})

const fractionMessage = computed(() => {
  return `${thresholdInPercent.value}% von ${budgetFormatted.value || '?'} = ${budgetThresholdFractionFormatted.value || '?'}`;
})

const rangeMessage = computed(() => {
  if (!budget.value)
    return null

  return `Bereich ${thresholdRangeFormatted.value[0]} - ${thresholdRangeFormatted.value[1]}`;
})

function onHeaderClick() {
  if (isBudgetAbsent.value)
    onNewBudget();
  else
    onRemoveBudget();
  changeAnything()
}

function onNewBudget() {
  console.assert(isBudgetAbsent.value, "Budget must not exist")
  emit("newBudget");
}

function onRemoveBudget() {
  console.assert(!isBudgetAbsent.value, "Budget must exist")
  emit("deleteBudget");
}

function changeAnything() {
  emit("changed")
}

function formatMonetary(value: number) {
  if (!value && value !== 0)
    return null;

  return `${Math.round(value)} €`;
}
</script>

<template>
  <div>
    <div class="d-flex justify-content-between align-items-center p-1">
      <div>
        <v-icon :small="true" color="red">
          mdi-finance
        </v-icon>

        Ausgaben Budget
      </div>

      <v-btn size="small"
             :icon="true"
             @click="onHeaderClick">
        <v-icon>
          {{ isBudgetAbsent ? 'mdi-plus' : 'mdi-minus' }}
        </v-icon>
      </v-btn>
    </div>

    <div v-if="!isBudgetAbsent">
      <v-text-field label="Monatliches Budget"
                    suffix="€"
                    type="number"
                    placeholder="1000"
                    v-model="budgetInEuro"
                    :rules="[
                                v => !!v || 'Muss vorhanden sein',
                                v => Number.parseInt(v) > 0 || 'Muss positiv sein'
                              ]"
                    @input="changeAnything"/>

      <v-slider :min="0" :max="50"
                :step="5"
                color="orange"
                :disabled="!budget"
                :thumb-label="true"
                v-model="thresholdInPercent"
                @input="changeAnything"
                label="Warnung"
                messages="msg">
        <template v-slot:message>
          <div class="d-flex flex-column">
            <span>{{ fractionMessage }}</span>
            <span>{{ rangeMessage }}</span>
          </div>
        </template>
        <template v-slot:thumb-label="{ modelValue }">
          {{ modelValue }}%
        </template>
      </v-slider>
    </div>
  </div>
</template>
