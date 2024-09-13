<script setup lang="ts">
import {getPages} from "../../utils/pages.ts";

const links = computed(() => {
  return getPages();
})
</script>

<template>
  <v-list nav dense>
    <v-list-item>
      <v-list-item-title>
        Topics
      </v-list-item-title>
      <v-list-item-subtitle>
        Table of Content
      </v-list-item-subtitle>
    </v-list-item>

    <v-divider/>

    <v-list-item v-for="(link, idx) in links" :key="idx" link :to="link.link">
      <v-list-item-action>
        <v-icon>
          {{ link.icon }}
        </v-icon>
      </v-list-item-action>
      <v-list-item-title>
        {{ link.title }}
      </v-list-item-title>
    </v-list-item>

    <v-divider/>
  </v-list>
</template>
