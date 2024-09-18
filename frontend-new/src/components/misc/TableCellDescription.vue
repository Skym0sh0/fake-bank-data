<script setup lang="ts">

import {computed} from "vue";
import * as _ from "lodash";

const {index, value} = defineProps<{
  index: number;
  value: string;
}>();

const isTooLong = computed(() => {
  return value !== shortValue.value
})

const shortValue = computed(() => {
  return _.truncate(value, {length: 32});
})
</script>

<template>
  <v-tooltip :text="value">
    <template v-slot:activator="{ props }">
      <div :id="`value-element-${index}`" v-bind="props">
        {{ shortValue }}
      </div>
    </template>
  </v-tooltip>
</template>
