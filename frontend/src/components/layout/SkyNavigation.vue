<script setup lang="ts">
import {getPages} from "../../router.ts";
import {computed} from "vue";

const links = computed(() => {
  return getPages();
})
</script>

<template>
  <v-list :nav="true">
    <v-list-item>
      <v-list-item-title>
        Topics
      </v-list-item-title>
      <v-list-item-subtitle>
        Table of Content
      </v-list-item-subtitle>
    </v-list-item>

    <v-divider/>

    <v-list-item v-for="link in links"
                 :key="link.link"
                 :to="link.link">
      <template v-slot:prepend>
        <v-icon :icon="link.icon" color="primary"/>
      </template>

      <v-list-item-title>
        {{ link.title }}
      </v-list-item-title>
      <v-list-item-subtitle>
        {{ link.shortDescription }}
      </v-list-item-subtitle>
    </v-list-item>

    <v-divider/>
  </v-list>
</template>
