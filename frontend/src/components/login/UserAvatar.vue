<script setup lang="ts">

import {computed} from "vue";
import {useUserStore} from "../../store/user-store.ts";

const userStore = useUserStore();

const username = computed<string>(() => {
  const fullname = [userStore.currentUser?.firstname ?? '', userStore.currentUser?.lastname ?? ''].join(" ").trim()
  if (fullname)
    return fullname

  return userStore.currentUser?.username ?? '';
})

const avatar = computed(() => {
  return username.value.split(/\s+/)
    .filter(word => word.length > 0)
    .map(word => word.substring(0, 1))
    .filter((_, idx) => idx < 2)
    .join("")
    .toUpperCase();
})
</script>

<template>
  <div class="d-flex justify-space-between align-center" style="gap: 0.5em">
    <v-avatar color="warning" size="32">
      {{ avatar }}
    </v-avatar>

    <v-spacer/>

    <span>
      {{ username }}
    </span>

    <v-btn
      :link="true"
      size="small"
      icon="mdi-dots-vertical"
      to="/user-details">
    </v-btn>
  </div>
</template>

<style scoped>

</style>