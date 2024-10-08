<script setup lang="ts">
import WaitingIndicator from "../misc/WaitingIndicator.vue";
import ConfirmationedButton from "../misc/ConfirmationedButton.vue";
import {inject, onMounted, ref, useTemplateRef} from "vue";
import {UserAuthApi} from "@api/api.ts";
import {apiRefKey, authenticationKey} from "../../keys.ts";
import {VForm} from "vuetify/components";
import ShowPasswordButton from "./ShowPasswordButton.vue";

const isLoading = ref(false)
const valid = ref(false)

const firstname = ref('')
const lastname = ref('')

const username = ref('')
const usernameRules = ref([
  (value: string) => !value ? "Required" : true,
  (value: string) => value.length < 3 ? "Minimal length is 3" : true,
  (value: string) => value.search(/\s+/) >= 0 ? "Must not contain spaces" : true,
])

const password = ref('')
const passwordRepeat = ref('')
const passwordVisible = ref(false)
const passwordRules = ref([
  (value: string) => !value ? "Required" : true,
  (value: string) => value.length < 3 ? "Minimal length is 3" : true,
  (value: string) => value !== passwordRepeat.value ? "Must match password repeat" : true
])
const passwordRepeatRules = ref([
  (value: string) => value !== password.value ? "Must match password" : true
])

const apiRef: UserAuthApi | undefined = inject(apiRefKey)?.authApi
const userService = inject(authenticationKey)?.value
const formRef = useTemplateRef<VForm>('user-details-form')

function onDelete() {
  if (!userService?.user?.id)
    return;

  isLoading.value = true

  apiRef?.deleteUser(userService.user.id)
    .then(() => reload())
    .finally(() => isLoading.value = false)
}

function onSave() {
  if (!userService?.user?.id)
    return;

  isLoading.value = true

  apiRef?.updateUser(userService.user.id, {
    firstname: firstname.value,
    lastname: lastname.value,
    username: username.value,
    password: password.value,
  })
    .then(res => userService?.login(res.data, password.value))
    .finally(() => isLoading.value = false)
}

function reload() {
  setTimeout(() => {
    userService?.logout()
    location.reload()

    setTimeout(() => isLoading.value = false, 500)
  }, 1000)
}

function validate() {
  formRef.value?.validate();
}

onMounted(() => {
  username.value = userService?.user?.username ?? '';

  password.value = userService?.user?.password ?? '';
  passwordRepeat.value = userService?.user?.password ?? '';

  firstname.value = userService?.user?.firstname ?? '';
  lastname.value = userService?.user?.lastname ?? '';
})

</script>

<template>
  <v-form ref="user-details-form" @submit.prevent="onSave" v-model="valid" class="d-flex justify-center align-center">
    <v-card width="50vw">
      <v-card-title>
        Benutzer Details
      </v-card-title>

      <v-card-text>
        <div>
          <v-text-field v-model="firstname"
                        type="text"
                        label="Vorname"
                        prepend-inner-icon="mdi-text-account"/>
          <v-text-field v-model="lastname"
                        type="text"
                        label="Nachname"
                        prepend-inner-icon="mdi-text-account"/>
        </div>

        <v-divider/>

        <div>
          <v-text-field v-model="username"
                        type="text"
                        label="Username"
                        prepend-inner-icon="mdi-account-outline"
                        :rules="usernameRules"
                        @input="validate"
                        required/>
        </div>

        <v-divider/>

        <div>
          <v-text-field v-model="password"
                        label="Passwort"
                        :rules="passwordRules"
                        prepend-inner-icon="mdi-lock-outline"
                        :type="passwordVisible ? 'text' : 'password'"
                        autocomplete="on"
                        @input="validate">
            <template v-slot:append-inner>
              <show-password-button :visible="passwordVisible"
                                    @click="passwordVisible = !passwordVisible"/>
            </template>
          </v-text-field>
          <v-text-field v-model="passwordRepeat"
                        label="Passwort wiederholen"
                        :rules="passwordRepeatRules"
                        prepend-inner-icon="mdi-lock-outline"
                        :type="passwordVisible ? 'text' : 'password'"
                        autocomplete="on"
                        @input="validate">
            <template v-slot:append-inner>
              <show-password-button :visible="passwordVisible"
                                    @click="passwordVisible = !passwordVisible"/>
            </template>
          </v-text-field>
        </div>
      </v-card-text>

      <waiting-indicator :is-loading="isLoading"/>

      <v-card-actions class="d-flex justify-center">
        <confirmationed-button @click="onDelete"
                               default-caption="Account Löschen?"
                               request-caption="Account wirklich löschen?"
                               confirmed-caption="Jetzt Löschen !"/>

        <v-btn type="submit"
               color="primary"
               :disabled="!valid"
               :loading="isLoading">
          Speichern & Neu einloggen
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-form>
</template>