<template>
    <v-form ref="user-details-form" @submit.prevent="onSave" v-model="valid">
        <v-card>
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
                        <template v-slot:append>
                            <v-btn icon @click="passwordVisible = !passwordVisible" small>
                                <v-icon>
                                    {{ passwordVisible ? 'mdi-eye-off' : 'mdi-eye' }}
                                </v-icon>
                            </v-btn>
                        </template>
                    </v-text-field>
                    <v-text-field v-model="passwordRepeat"
                                  label="Passwort wiederholen"
                                  :rules="passwordRepeatRules"
                                  prepend-inner-icon="mdi-lock-outline"
                                  :type="passwordVisible ? 'text' : 'password'"
                                  autocomplete="on"
                                  @input="validate">
                        <template v-slot:append>
                            <v-btn icon @click="passwordVisible = !passwordVisible" small>
                                <v-icon>
                                    {{ passwordVisible ? 'mdi-eye-off' : 'mdi-eye' }}
                                </v-icon>
                            </v-btn>
                        </template>
                    </v-text-field>
                </div>
            </v-card-text>

            <waiting-indicator :is-loading="isLoading"/>

            <v-card-actions class="d-flex justify-content-center">
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

<script>
import ConfirmationedButton from "@/components/misc/ConfirmationedButton.vue";
import {userService} from "@/auth/auth-header";
import WaitingIndicator from "@/components/misc/WaitingIndicator.vue";
import {api} from "@/api/RegularIncomeAPI";

export default {
    name: "UserDetailsPage",
    components: {
        WaitingIndicator,
        ConfirmationedButton
    },
    data() {
        return {
            isLoading: false,
            valid: false,

            firstname: '',
            lastname: '',

            username: '',
            usernameRules: [
                value => !value ? "Required" : true,
                value => value.length < 3 ? "Minimal length is 3" : true,
                value => value.search(/\s+/) >= 0 ? "Must not contain spaces" : true,
            ],

            password: '',
            passwordRepeat: '',
            passwordVisible: false,
            passwordRules: [
                value => !value ? "Required" : true,
                value => value.length < 3 ? "Minimal length is 3" : true,
                value => value !== this.passwordRepeat ? "Must match password repeat" : true
            ],
            passwordRepeatRules: [
                value => value !== this.password ? "Must match password" : true
            ],
        }
    },
    methods: {
        onDelete() {
            this.isLoading = true;

            api.getAuth()
                .deleteUser(userService.userReference.user.id)
                .then(() => this.reload())
                .finally(() => this.isLoading = false)
        },
        onSave() {
            this.isLoading = true

            api.getAuth()
                .updateUser(userService.userReference.user.id, {
                    firstname: this.firstname,
                    lastname: this.lastname,
                    username: this.username,
                    password: this.password
                })
                // .then(() => this.reload())
                .then(user => userService.login(user, this.password))
                .finally(() => this.isLoading = false)
        },
        reload() {
            setTimeout(() => {
                userService.logout();
                location.reload();

                setTimeout(() => this.isLoading = false, 500)
            }, 1000)
        },
        validate() {
            this.$refs['user-details-form'].validate()
        },
    },
    created() {
        this.username = userService.userReference.user.username

        this.password = userService.userReference.user.password
        this.passwordRepeat = userService.userReference.user.password

        this.firstname = userService.userReference.user.firstname
        this.lastname = userService.userReference.user.lastname
    },
}
</script>

<style scoped>

</style>
