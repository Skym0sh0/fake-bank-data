import {defineStore} from "pinia";
import {CategoryApi, ReportsApi, TurnoversApi, UserAuthApi} from "@api/api.ts";
import {Configuration} from "@api/configuration.ts";
import axios, {AxiosError, AxiosInstance} from "axios";
import {v4 as uuidv4} from "uuid";
import {ref} from "vue";
import {useUserStore} from "./user-store.ts";

export const useApi = defineStore('api', () => {
  const client = ref<AxiosInstance>(
    axios.create({
      headers: {
        correlationId: uuidv4(),
      }
    })
  );

  client.value.interceptors.request.use(request => {
    const userStore = useUserStore()

    const user = userStore.currentUser;

    if (user && user.username && user.password) {
      const authdata = window.btoa(user.username + ":" + user.password)

      request.headers.set("Authorization", 'Basic ' + authdata)
    }

    return request;
  })

  client.value.interceptors.response.use(response => {
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

  const api = {
    authApi: new UserAuthApi(config, "", client.value),
    reportsApi: new ReportsApi(config, "", client.value),
    categoriesApi: new CategoryApi(config, "", client.value),
    turnoversApi: new TurnoversApi(config, "", client.value),
  }

  return {...api}
})
