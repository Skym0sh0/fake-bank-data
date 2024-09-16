<script setup lang="ts">

const {transferData} = defineProps<{ transferData: any }>();

const emit = defineEmits<{
  (e: "dragstart"): void;
}>();
</script>

<template>
  <slot/>
</template>
