<script setup lang="ts">

const {items, divider = "/", size = "10px"} = defineProps<{
  items: string[];
  divider?: string;
  size?: string;
}>()
</script>

<template>
  <ul class="theme--light px-0" :style="{fontSize: size}">
    <template v-for="item in items" :key="item">
      <li class="breadcrump-divider px-1">
        {{ divider }}
      </li>
      <li style="display: inline">
        {{ item }}
      </li>
    </template>
  </ul>
</template>

<style scoped>
.breadcrump-divider {
  display: inline;
}
</style>
