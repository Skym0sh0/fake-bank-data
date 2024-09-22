<script  lang="ts">
function rand() {
  return Math.round(Math.random() * 10000000);
}
</script>

<script setup lang="ts">

import {computed} from "vue";
import * as _ from "lodash";


const {index = rand(), value, maxLength = 32} = defineProps<{
  index?: number;
  value: string | null | undefined;
  maxLength?: number;
}>();

const shortValue = computed(() => {
  return _.truncate(value ?? '', {length: maxLength});
})
</script>

<template>
  <v-tooltip :text="value ?? ''">
    <template v-slot:activator="{ props }">
      <div :id="`value-element-${index}`" v-bind="props">
        {{ shortValue }}
      </div>
    </template>
  </v-tooltip>
</template>
