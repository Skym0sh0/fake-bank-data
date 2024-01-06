<template>
    <v-form ref="registration-form" @submit.prevent="onRegister" v-model="valid">
        <div style="height: 90vh" class="w-100 d-flex justify-content-center align-items-center">
            <v-card min-width="25%" max-width="80%" elevation="10">
                <v-card-title>
                    Registering
                </v-card-title>

                <v-card-text>
                    <v-text-field v-model="firstName" label="Vorname"/>
                    <v-text-field v-model="lastName" label="Nachname"/>

                    <v-spacer class="my-5"/>

                    <v-text-field v-model="loginName"
                                  type="text"
                                  label="Loginname*"
                                  prepend-inner-icon="mdi-account-outline"
                                  :rules="loginNameRules"
                                  required/>

                    <v-spacer class="my-5"/>

                    <v-text-field v-model="password"
                                  label="Passwort*"
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
                                  label="Passwort wiederholen*"
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
    </v-form>
</template>

<script>
export default {
    name: "RegisterPage",
    data() {
        return {
            firstName: '',
            lastName: '',
            loginName: '',
            password: '',
            passwordRepeat: '',
            valid: false,
            passwordVisible: false,
            loginNameRules: [
                value => !value ? "Required" : true,
                value => value.length < 5 ? "Minimal length is 5" : true,
                value => value.search(/\s+/) >= 0 ? "Must not contain spaces" : true,
            ],
            passwordRules: [
                value => !value ? "Required" : true,
                value => value.length < 8 ? "Minimal length is 8" : true,
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

            console.log(this.loginName, this.password, this.passwordRepeat)
            // this.$router.push({name: 'login'})
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