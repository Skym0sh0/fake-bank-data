<script setup lang="ts">
const {isLoading} = defineProps<{ isLoading: boolean }>();
</script>

<template>
  <div class="w-100 h-100">
    <div v-if="isLoading"
         class="d-flex justify-content-center align-items-center p-3 w-100 h-100"
         style="background-color: rgb(0,0,0, 0.1)">
      <v-progress-circular :indeterminate="true"
                           :size="64"
                           color="primary">
        Loading ...
      </v-progress-circular>
    </div>
    <template v-else>
      <slot/>
    </template>
  </div>
</template>
