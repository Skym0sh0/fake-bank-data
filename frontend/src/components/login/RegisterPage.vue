<template>
    <v-form ref="registration-form" @submit.prevent="onRegister" v-model="valid">
        <div style="height: 90vh" class="w-100 d-flex justify-content-center align-items-center">
            <v-card min-width="25%" max-width="80%" elevation="10">
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
                                  @input="validate">
                        <template v-slot:append>
                            <v-btn icon @click="passwordVisible = !passwordVisible" small>
                                <v-icon>
                                    {{ passwordVisible ? 'mdi-eye-off' : 'mdi-eye' }}
                                </v-icon>
                            </v-btn>
                        </template>
                    </v-text-field>
                </v-card-text>

                <v-card-actions class="d-flex justify-content-center">
                    <v-btn @click="abort" color="warning">
                        Abbrechen
                    </v-btn>

                    <v-btn type="submit" color="primary" :disabled="!valid">
                        Registrieren
                    </v-btn>
                </v-card-actions>
            </v-card>
        </div>

        <waiting-indicator :is-loading="isLoading"/>
    </v-form>
</template>

<script>
import {api} from "@/api/RegularIncomeAPI";
import WaitingIndicator from "@/components/misc/WaitingIndicator.vue";

export default {
    name: "RegisterPage",
    components: {WaitingIndicator},
    data() {
        return {
            username: '',
            password: '',
            passwordRepeat: '',

            isLoading: false,
            valid: false,
            passwordVisible: false,
            usernameRules: [
                value => !value ? "Required" : true,
                value => value.length < 3 ? "Minimal length is 3" : true,
                value => value.search(/\s+/) >= 0 ? "Must not contain spaces" : true,
            ],
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
        abort() {
            this.$router.back()
        },
        onRegister() {
            if (!this.valid)
                return;

            this.isLoading = true;

            api.getAuth()
                .registerUser({
                    username: this.username,
                    password: this.password
                })
                .then(() => this.$router.push({name: 'login'}))
                .finally(() => this.isLoading = false)
        },
        validate() {
            this.$refs['registration-form'].validate()
        },
    },
    mounted() {
        this.$refs['registration-form'].validate()
    },
}
</script>


<style scoped>

</style>
