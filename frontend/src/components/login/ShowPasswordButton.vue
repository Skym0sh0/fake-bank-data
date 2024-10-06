<script setup lang="ts">
const {visible} = defineProps<{
  visible: boolean;
}>();

const emit = defineEmits<{
  (e: 'click'): void;
}>()
</script>

<template>
  <v-btn :icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
         @click="emit('click')"
         color="secondary"
         variant="flat"
         size="sm">
  </v-btn>
</template>
