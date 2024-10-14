<script setup lang="ts">
import {computed, ref} from "vue";
import UserAvatar from "./components/login/UserAvatar.vue";
import WaitingIndicator from "./components/misc/WaitingIndicator.vue";
import ApiErrorBubble from "./components/misc/ApiErrorBubble.vue";
import ApiAccess from "./api/ApiAccess.vue";
import SkyNavigation from "./components/layout/SkyNavigation.vue";

import RegularIncomeIcon from './assets/regular-income.svg'
import {useUserStore} from "./store/user-store.ts";

const userStore = useUserStore();
userStore.initUser()

const drawer = ref(true)
const isLoggingOut = ref(false)

const isLoggedIn = computed(() => {
  return userStore.isLoggedIn
})

const isDevMode = computed(() => {
  return import.meta.env.DEV;
})

function doLogout() {
  isLoggingOut.value = true

  userStore.logout()
  location.reload();
}

function toggleDrawer() {
  drawer.value = !drawer.value;
}
</script>

<template>
  <ApiAccess>
    <v-app id="app">
      <v-app-bar app color="primary">
        <v-app-bar-nav-icon v-if="isLoggedIn" @click.stop="toggleDrawer"/>

        <v-toolbar-title>
          <div class="d-flex flex-row justify-start align-center">
            <div>
              <RegularIncomeIcon :width="48" :height="48"/>
            </div>

            <div class="d-flex ga-1">
              <span>Regular Income</span>

              <span v-if="isDevMode" class="text-yellow text-caption" style="rotate: -45deg">
                DEV
              </span>
            </div>
          </div>
        </v-toolbar-title>

        <v-spacer/>

        <div v-if="isLoggedIn" class="d-flex justify-space-between align-center" style="gap: 1em">
          <user-avatar/>

          <v-btn @click="doLogout" light color="dark">
            Ausloggen
          </v-btn>
        </div>
      </v-app-bar>

      <v-navigation-drawer v-if="isLoggedIn" app v-model="drawer" elevation="8">
        <sky-navigation/>
      </v-navigation-drawer>

      <v-main app>
        <v-container style="width: 75vw">
          <router-view/>
        </v-container>

        <api-error-bubble/>
      </v-main>

      <waiting-indicator :is-loading="isLoggingOut"/>
    </v-app>
  </ApiAccess>
</template>

<style scoped>
</style>
