<script setup lang="ts">
import WaitingIndicator from "../misc/WaitingIndicator.vue";
import {inject, ref} from "vue";
import {apiRefKey, notifierRefKey} from "../../keys.ts";
import {useRoute, useRouter} from 'vue-router'
import {UserAuthApi} from "@api/api.ts"
import ShowPasswordButton from "./ShowPasswordButton.vue";
import {useUserStore} from "../../store/user-store.ts";

const router = useRouter()
const route = useRoute()

const userStore = useUserStore();

const nameRules = [
  (value: string) => {
    if (!value)
      return "Username ist nötig"
    return true;
  }
];
const passwordRules = [
  (value: string) => {
    if (!value)
      return "Password ist nötig"
    return true;
  }
]

const isLoading = ref(false);
const valid = ref(false);

const username = ref("");
const password = ref("");

const passwordVisible = ref(false);

const errorMessage = ref<string | null>(null);

const apiAccess: UserAuthApi | undefined = inject(apiRefKey)?.authApi
const notifierRef = inject(notifierRefKey);

function doLogin() {
  if (!username.value || !password.value)
    return;

  if (!apiAccess)
    return;

  isLoading.value = true;

  apiAccess.loginUser({username: username.value, password: password.value})
    .then(res => {
      userStore.login(res.data, password.value)

      const path: string = route.query['returnUrl']?.toString() ?? '/';

      router.replace({path: path})
    })
    .catch(e => {
      errorMessage.value = e.error;
      notifierRef?.notifyError("Login fehlgeschlagen", e)
    })
    .finally(() => isLoading.value = false);
}

function onRegisterClick() {
  router.push({name: 'register'})
}
</script>

<template>
  <div class="w-100 d-flex justify-center align-center">
    <v-form v-model="valid" @submit.prevent="doLogin">
      <v-card width="300px" elevation="10">
        <v-card-title>
          Anmelden
        </v-card-title>

        <v-card-subtitle v-if="errorMessage"
                         @click="errorMessage = null"
                         style="font-size: small; max-width: 100%; max-height: 300px"
                         class=" transition-ease-in-out d-flex justify-center align-center">
          <div class=" bg-warning text-wrap overflow-auto w-100" style="max-height: 300px">
            {{ errorMessage }}
          </div>
        </v-card-subtitle>

        <v-card-text>
          <v-text-field v-model="username"
                        type="text"
                        label="Username"
                        :rules="nameRules"
                        prepend-inner-icon="mdi-account-outline"
                        required/>

          <v-text-field v-model="password"
                        :type="passwordVisible ? 'text' : 'password'"
                        label="Passwort"
                        :rules="passwordRules"
                        prepend-inner-icon="mdi-lock-outline"
                        required>
            <template v-slot:append-inner>
              <ShowPasswordButton :visible="passwordVisible"
                                  @click="passwordVisible = !passwordVisible"/>
            </template>
          </v-text-field>

          <v-card-actions class="w-100 d-flex justify-space-between align-center">
            <v-btn @click="onRegisterClick" color="warning" variant="tonal">
              Registrieren
            </v-btn>
            <v-btn :disabled="!valid" color="success" variant="tonal" type="submit">
              Einloggen
            </v-btn>
          </v-card-actions>
        </v-card-text>
      </v-card>

      <waiting-indicator :is-loading="isLoading"/>
    </v-form>
  </div>
</template>
