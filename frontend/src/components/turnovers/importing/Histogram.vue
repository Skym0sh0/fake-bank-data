<script setup lang="ts">
import {computed} from "vue";
import {useTheme} from "vuetify";

export type HistogramValueType = {
  label?: string;
  value: number;
  color: string;
}

type OpticalValue = HistogramValueType & {
  width: string;
};

const {text, values} = defineProps<{
  text?: string;
  values: HistogramValueType[];
}>();

const theme = useTheme().current.value;

const maxValue = computed(() => {
  return values.reduce((old, cur) => old + cur.value, 0)
})

const opticalValues = computed<OpticalValue[]>(() => {
  return values.filter(v => v.value > 0)
    .map(v => ({
      ...v,
      width: `${Math.round(v.value / maxValue.value * 100)}%`,
      color: theme.colors[v.color],
    }))
})

</script>

<template>
  <div class="d-flex flex-column w-100">
    <h6>{{ text ?? '' }}</h6>

    <div class="root-bar" style="background-color: lightgray">
      <div v-if="!opticalValues.length" class="bar w-100">
        0
      </div>

      <template v-for="v in opticalValues">
        <v-tooltip :text="v.label" location="top" effect="dark">
          <template v-slot:activator="{ props }">
            <div class="bar"
                 :style="{width: v.width, backgroundColor: v.color}"
                 v-bind="props">
              {{ v.value }}
            </div>
          </template>
        </v-tooltip>
      </template>
    </div>
  </div>
</template>

<style scoped>
.root-bar {
  height: 1rem;
  width: 100%;

  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;

  border: 1px solid black;
  border-radius: 0.2em;
}

.bar {
  font-size: 0.625rem;
  font-weight: 700;
  line-height: 1.425;
  letter-spacing: 0.0178571429em;

  display: flex;
  justify-content: center;
  align-items: center;
}
</style>