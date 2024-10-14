<script setup lang="ts">
import WaitingIndicator from "../misc/WaitingIndicator.vue";
import {inject, onMounted, ref, useTemplateRef} from "vue";
import {useRouter} from "vue-router";
import {notifierRefKey} from "../../keys.ts";
import {VForm} from "vuetify/components";
import ShowPasswordButton from "./ShowPasswordButton.vue";
import {useApi} from "../../store/use-api.ts";

const api = useApi()
const notifierRef = inject(notifierRefKey);

const username = ref('')
const password = ref('')
const passwordRepeat = ref('')

const isLoading = ref(false)
const valid = ref(false)
const passwordVisible = ref(false)

const usernameRules = ref([
  (value: string) => !value ? "Required" : true,
  (value: string) => value.length < 3 ? "Minimal length is 3" : true,
  (value: string) => value.search(/\s+/) >= 0 ? "Must not contain spaces" : true,
])

const passwordRules = ref([
  (value: string) => !value ? "Required" : true,
  (value: string) => value.length < 3 ? "Minimal length is 3" : true,
  (value: string) => value !== passwordRepeat.value ? "Must match password repeat" : true
])

const passwordRepeatRules = ref([
  (value: string) => value !== password.value ? "Must match password" : true
])

const input = useTemplateRef<VForm>('registration-form')
const router = useRouter();

function abort() {
  router.back();
}

function onRegister() {
  if (!valid.value)
    return;

  isLoading.value = true

  api.authApi.registerUser({username: username.value, password: password.value})
    .then(() => router.push({name: 'login'}))
    .catch(e => notifierRef?.notifyError("Registrierung fehlgeschlagen", e))
    .finally(() => isLoading.value = false)
}

function validate() {
  input.value?.validate()
}

onMounted(() => {
  input.value?.validate()
})
</script>

<template>
  <div class="w-100 d-flex justify-center align-center">
    <v-form ref="registration-form" @submit.prevent="onRegister" v-model="valid">
      <v-card width="300px" elevation="10">
        <v-card-title>
          Registering
        </v-card-title>

        <v-card-text>
          <v-text-field v-model="username"
                        type="text"
                        label="Username"
                        prepend-inner-icon="mdi-account-outline"
                        :rules="usernameRules"
                        required/>

          <v-spacer class="my-5"/>

          <v-text-field v-model="password"
                        label="Passwort"
                        :rules="passwordRules"
                        prepend-inner-icon="mdi-lock-outline"
                        :type="passwordVisible ? 'text' : 'password'"
                        @input="validate">
            <template v-slot:append-inner>
              <ShowPasswordButton :visible="passwordVisible"
                                  @click="passwordVisible = !passwordVisible"/>
            </template>
          </v-text-field>
          <v-text-field v-model="passwordRepeat"
                        label="Passwort wiederholen"
                        :rules="passwordRepeatRules"
                        prepend-inner-icon="mdi-lock-outline"
                        :type="passwordVisible ? 'text' : 'password'"
                        @input="validate">
            <template v-slot:append-inner>
              <ShowPasswordButton :visible="passwordVisible"
                                  @click="passwordVisible = !passwordVisible"/>
            </template>
          </v-text-field>
        </v-card-text>

        <v-card-actions class="d-flex justify-center align-center">
          <v-btn @click="abort" color="warning" variant="tonal">
            Abbrechen
          </v-btn>

          <v-btn type="submit" color="primary" variant="tonal" :disabled="!valid">
            Registrieren
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-form>

    <waiting-indicator :is-loading="isLoading"/>
  </div>
</template>
