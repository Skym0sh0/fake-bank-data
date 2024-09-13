<script setup lang="ts">
import {computed, inject, onMounted, ref} from "vue";
import UserAvatar from "./components/login/UserAvatar.vue";
import WaitingIndicator from "./components/misc/WaitingIndicator.vue";
import {authenticationKey} from "./keys.ts";
import ApiErrorBubble from "./components/misc/ApiErrorBubble.vue";
import ApiAccess from "./api/ApiAccess.vue";

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
      <v-app-bar app color="primary" dark>
        <v-app-bar-nav-icon v-if="isLoggedIn" @click.stop="toggleDrawer"/>

        <v-toolbar-title class="mr-12 align-center title">
          <v-icon class="mx-4" color="warning">
            mdi-currency-eur
          </v-icon>

          Regular Income
        </v-toolbar-title>

        <v-spacer/>

        <div v-if="isLoggedIn" class="d-flex justify-content-between align-items-center" style="gap: 1em">
          <user-avatar/>

          <v-btn @click="doLogout" light color="dark">
            Ausloggen
          </v-btn>
        </div>
      </v-app-bar>

      <v-navigation-drawer v-if="isLoggedIn" app v-model="drawer" dark>
        <!--      <sky-navigation/>-->
      </v-navigation-drawer>

      <v-main app>
        <v-container fluid>
          <router-view/>
        </v-container>

        <api-error-bubble/>
      </v-main>

      <waiting-indicator :is-loading="isLoggingOut"/>
    </v-app>
  </ApiAccess>
</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}

.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}

.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
