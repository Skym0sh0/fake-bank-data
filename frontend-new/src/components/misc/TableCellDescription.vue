<script lang="ts">
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

const text = computed<string>(() => {
  return value ?? '';
})

const shortValue = computed(() => {
  const normalized = text.value.replace(/\s+/g, ' ').trim()

  return _.truncate(normalized, {length: maxLength});
})
</script>

<template>
  <v-tooltip>
    <template v-slot:activator="{ props }">
      <div :id="`value-element-${index}`" v-bind="props" class="text-caption"
           :style="{width: `${Math.round(1.2 * maxLength)}ex`}">
        <code>
          {{ shortValue }}
        </code>
      </div>
    </template>

    <div>
      <pre><code>{{ text }}</code></pre>
    </div>
  </v-tooltip>
</template>
