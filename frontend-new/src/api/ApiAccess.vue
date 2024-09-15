<script setup lang="ts">
import {inject, provide} from "vue";
import {apiRefKey, ApiType, authenticationKey, errorRefKey} from "../keys.ts";
import {v4 as uuidv4} from "uuid";
import {Configuration, ReportsApi, UserAuthApi} from "@api/index.ts"
import axios, {AxiosInstance} from "axios";

const userRef = inject(authenticationKey).value;
const errorReference = inject(errorRefKey).value;

function authHeader() {
  const user = userRef?.user;

  if (user && user.username && user.password) {
    const authdata = window.btoa(user.username + ":" + user.password)
    return {'Authorization': 'Basic ' + authdata};
  } else {
    return {};
  }
}

const client: AxiosInstance = axios.create({
  headers: {
    correlationId: uuidv4(),
  }
});

client.interceptors.request.use(request => {
  request.headers = {
    ...request.headers,
    ...authHeader()
  }
  return request;
})

client.interceptors.response.use(response => {
    return response.data
  },
  error => {
    errorReference.error = error.response.data;

    if (error.response.status === 401 && !error.request?.responseURL?.endsWith("/api/auth/login")) {
      userRef.logout();
      location.reload();
    }

    return Promise.reject(error.response.data);
  })

const config: Configuration = new Configuration({
  basePath: "/dev-proxy",
});

const api: ApiType = {
  authApi: new UserAuthApi(config, "", client),
  reportsApi: new ReportsApi(config, "", client)
}

provide(apiRefKey, api);
</script>

<template>
  <div>
    <slot></slot>
  </div>
</template>
