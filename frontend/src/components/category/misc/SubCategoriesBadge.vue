<script setup lang="ts">
import {Category} from "@api/api.ts";

const {category} = defineProps<{
  category?: Category;
}>()
</script>

<template>
  <v-tooltip v-if="category && category.subCategories && category.subCategories.length" location="top">
    <template v-slot:activator="{ props }">
      <v-badge :content="category.subCategories.length"
               color="info"
               :inline="true"
               v-bind="props"/>
    </template>

    Hat {{ category.subCategories.length }} Unterkategorien.
  </v-tooltip>
</template>
