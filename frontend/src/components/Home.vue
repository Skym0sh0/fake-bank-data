<script setup lang="ts">
import {getPages} from "../router.ts";
import {computed} from "vue";
import {useTheme} from "vuetify";

const theme = useTheme().current.value;

const links = computed(() => {
  return getPages().filter(p => !p.isStartPage)
})
</script>

<template>
  <div class="d-flex flex-wrap justify-start align-baseline align-center"
       style="column-gap: 1em; row-gap: 2em">

    <template v-for="(link) in links">
      <v-card :to="link.link"
              width="16em"
              elevation="8">
        <v-card-title :style="{color: theme.colors.info}">
          {{ link.title }}
        </v-card-title>

        <v-card-subtitle>
          {{ link.title }}
        </v-card-subtitle>

        <v-card-text>
          <div class="d-flex justify-space-between align-baseline">
            <span>
              {{ link.shortDescription }}
            </span>

            <v-icon size="x-large" :color="theme.colors.info">
              {{ link.icon }}
            </v-icon>
          </div>
        </v-card-text>
      </v-card>
    </template>
  </div>
</template>
