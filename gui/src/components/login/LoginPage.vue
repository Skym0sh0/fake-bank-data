<template>
    <div style="height: 90vh" class="w-100 d-flex justify-content-center align-items-center">
        <v-card min-width="25%" max-width="80%" elevation="10">
            <v-card-title>
                Login
            </v-card-title>

            <v-card-text>
                <v-form v-model="valid" @submit.prevent="doLogin">
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

                    <div class="w-100 d-flex justify-content-between">
                        <v-btn @click="onRegisterClick" color="warning">
                            Register
                        </v-btn>
                        <v-btn :disabled="!valid" color="success" type="submit">
                            Login
                        </v-btn>
                    </div>
                </v-form>
            </v-card-text>
        </v-card>
    </div>
</template>

<script>
import {userService} from "@/auth/auth-header";

export default {
    name: "LoginPage",
    data() {
        return {
            valid: false,
            username: '',
            password: '',
            passwordVisible: false,
            nameRules: [
                value => {
                    if (!value)
                        return "Username is required"
                    return true;
                }
            ],
            passwordRules: [
                value => {
                    if (!value)
                        return "Password is required"
                    return true;
                }
            ],
        };
    },
    methods: {
        doLogin() {
            if (!this.username || !this.password)
                return;

            userService.login(this.username, this.password)

            this.$router.replace({
                path: this.$route.query.returnUrl || '/'
            })
            // location.reload();
        },
        onRegisterClick() {
            this.$router.push({name: 'register'})
        }
    },
}
</script>

<style scoped>

</style>
