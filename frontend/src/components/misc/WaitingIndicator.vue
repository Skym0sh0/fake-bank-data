<script setup lang="ts">
const {isLoading} = defineProps<{ isLoading: boolean }>();
</script>

<template>
  <v-overlay :value="isLoading">
    <v-progress-circular
        :indeterminate="true"
        :size="64"/>
  </v-overlay>
</template>
