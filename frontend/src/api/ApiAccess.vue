<script setup lang="ts">
import {provide} from "vue";
import {apiRefKey, ApiType} from "../keys.ts";
import {v4 as uuidv4} from "uuid";
import {CategoryApi, Configuration, ReportsApi, TurnoversApi, UserAuthApi} from "@api/index.ts"
import axios, {AxiosError, AxiosInstance} from "axios";
import {useUserStore} from "../store/user-store.ts";

const client: AxiosInstance = axios.create({
  headers: {
    correlationId: uuidv4(),
  }
});

client.interceptors.request.use(request => {
  const userStore = useUserStore()

  const user = userStore.currentUser;

  if (user && user.username && user.password) {
    const authdata = window.btoa(user.username + ":" + user.password)

    request.headers.set("Authorization", 'Basic ' + authdata)
  }

  return request;
})

client.interceptors.response.use(response => {
    return response
  },
  (error: AxiosError) => {
    const userStore = useUserStore()

    if (error.response?.status === 401 && !error.request?.responseURL?.endsWith("/api/auth/login")) {
      userStore.logout();
      location.reload();
    }

    return Promise.reject(error);
  })

const config: Configuration = new Configuration({
  basePath: import.meta.env.VITE_BACKEND_BASE_PATH,
});

const api: ApiType = {
  authApi: new UserAuthApi(config, "", client),
  reportsApi: new ReportsApi(config, "", client),
  categoriesApi: new CategoryApi(config, "", client),
  turnoversApi: new TurnoversApi(config, "", client),
}

provide(apiRefKey, api);
</script>

<template>
  <div>
    <slot></slot>
  </div>
</template>
