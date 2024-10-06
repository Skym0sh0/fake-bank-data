<script setup lang="ts">
import {computed} from "vue";

const {value} = defineProps<{
  value: number;
}>();

const formattedMonetary = computed(() => {
  const eu = new Intl.NumberFormat('de-DE', {
    style: 'currency',
    currency: 'EUR',
  });
  return eu.format(value / 100);
});
</script>

<template>
<div>
  {{ formattedMonetary }}
</div>
</template>

<style scoped>

</style>