<script setup lang="ts">
const emit = defineEmits<{
  (e: "drop"): void;
}>()
</script>

<template>
  <slot/>
</template>
