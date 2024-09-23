<script setup lang="ts">
import {computed, inject, ref} from "vue";
import UserAvatar from "./components/login/UserAvatar.vue";
import WaitingIndicator from "./components/misc/WaitingIndicator.vue";
import {authenticationKey} from "./keys.ts";
import ApiErrorBubble from "./components/misc/ApiErrorBubble.vue";
import ApiAccess from "./api/ApiAccess.vue";
import SkyNavigation from "./components/layout/SkyNavigation.vue";

import RegularIncomeIcon from './assets/regular-income.svg'

const userRef = inject(authenticationKey)

userRef.value.initUser()

const drawer = ref(true)
const isLoggingOut = ref(false)

const isLoggedIn = computed(() => {
  return !!userRef.value.user;
})

function doLogout() {
  isLoggingOut.value = true

  userRef.value.logout();
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

        <v-toolbar-title class="mr-12 align-center title">
          <div class="d-flex flex-row justify-start align-center">
            <div>
              <RegularIncomeIcon :width="48" :height="48"/>
            </div>

            <span>Regular Income</span>
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
