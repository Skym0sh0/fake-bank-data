<script setup lang="ts">
import {getPages} from "../utils/pages.ts";
import {computed} from "vue";

const links = computed(() => {
  return getPages().filter(p => !p.isStartPage);
})
</script>

<template>
  <div class="d-flex flex-wrap justify-content-start align-items-baseline align-content-center flex-grow-1"
       style="column-gap: 1em; row-gap: 2em">
    <template v-for="(link) in links">
      <v-card no-body
              :link="true"
              :to="link.link"
              width="16em"
              height="8em"
              elevation="8">
        <v-card-title>
          <router-link :to="link.link">
            {{ link.title }}
          </router-link>
        </v-card-title>

        <v-card-text>
          <div class="d-flex justify-content-between align-items-baseline">
            <p class="card-text">
              {{ link.shortDescription }}
            </p>

            <v-icon :x-large="true">
              {{ link.icon }}
            </v-icon>
          </div>
        </v-card-text>
      </v-card>
    </template>
  </div>
</template>
