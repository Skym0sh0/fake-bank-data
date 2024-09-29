<script setup lang="ts">
import WaitingIndicator from "../misc/WaitingIndicator.vue";
import {inject, ref} from "vue";
import {apiRefKey, authenticationKey} from "../../keys.ts";
import {useRoute, useRouter} from 'vue-router'
import {User, UserAuthApi} from "@api"

const router = useRouter()
const route = useRoute()

const nameRules = [
  value => {
    if (!value)
      return "Username ist nötig"
    return true;
  }
];
const passwordRules = [
  value => {
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

const userRef = inject(authenticationKey).value
const apiAccess = inject(apiRefKey)

function doLogin() {
  if (!username.value || !password.value)
    return;

  isLoading.value = true;

  const api: UserAuthApi = apiAccess.authApi;

  api.loginUser({username: username.value, password: password.value})
    .then((user: User) => {
      userRef.login(user, password.value)
      router.replace({
        path: route.query.returnUrl ?? '/'
      })
    })
    .catch(e => {
      errorMessage.value = e;
    })
    .finally(() => isLoading.value = false);
}

function onRegisterClick() {
  router.push({name: 'register'})
}
</script>

<template>
  <v-form v-model="valid" @submit.prevent="doLogin">
    <div style="height: 90vh" class="w-100 d-flex justify-content-center align-items-center">
      <v-card min-width="25%" max-width="80%" elevation="10">
        <v-card-title>
          Anmelden
        </v-card-title>

        <v-card-subtitle v-if="errorMessage"
                         @click="errorMessage = null"
                         style="font-size: small"
                         class="bg-warning transition-ease-in-out d-flex justify-content-center align-items-center">
                    <span>
                        {{ errorMessage }}
                    </span>
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
            <template v-slot:append>
              <v-btn icon @click="passwordVisible = !passwordVisible" small>
                <v-icon>
                  {{ passwordVisible ? 'mdi-eye-off' : 'mdi-eye' }}
                </v-icon>
              </v-btn>
            </template>
          </v-text-field>

          <v-card-actions class="w-100 d-flex justify-content-between">
            <v-btn @click="onRegisterClick" color="warning">
              Registrieren
            </v-btn>
            <v-btn :disabled="!valid" color="success" type="submit">
              Einloggen
            </v-btn>
          </v-card-actions>
        </v-card-text>
      </v-card>

      <waiting-indicator :is-loading="isLoading"/>
    </div>
  </v-form>
</template>
