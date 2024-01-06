<template>
    <v-app id="app">
        <!--<v-system-bar app fixed window/>-->
        <v-app-bar app color="primary" dark>
            <v-app-bar-nav-icon v-if="isLoggedIn" @click.stop="drawer = !drawer"/>

            <v-toolbar-title class="mr-12 align-center title">
                <v-icon class="mx-4" color="warning">
                    mdi-currency-eur
                </v-icon>

                Regular Income
            </v-toolbar-title>

            <v-spacer/>

            <div v-if="isLoggedIn" class="d-flex justify-content-between align-items-center" style="gap: 1em">
                <user-avatar/>

                <v-btn @click="doLogout" light color="dark">
                    Logout
                </v-btn>
            </div>
        </v-app-bar>

        <v-navigation-drawer v-if="isLoggedIn" app v-model="drawer" dark>
            <sky-navigation/>
        </v-navigation-drawer>

        <v-main app>
            <v-container fluid>
                <router-view/>
            </v-container>
        </v-main>

        <waiting-indicator :is-loading="isLoggingOut"/>

        <!--<v-footer app/>-->
        <!--<v-bottom-navigation app/>-->
    </v-app>
</template>

<script>
import SkyNavigation from "./components/layout/SkyNavigation";
import {userService} from "@/auth/auth-header";
import WaitingIndicator from "@/components/misc/WaitingIndicator.vue";
import UserAvatar from "@/components/login/UserAvatar.vue";

export default {
    name: "App",
    data() {
        return {
            drawer: true,
            isLoggingOut: false,
        }
    },
    methods: {
        doLogout() {
            this.isLoggingOut = true;

            userService.logout();
            location.reload();
        }
    },
    computed: {
        isLoggedIn() {
            return !!this.$root.userRef.user;
        },
    },
    components: {
        UserAvatar,
        WaitingIndicator,
        SkyNavigation,
    },
    created() {
        userService.initUser();
    }
}
</script>

<style scoped>
/*#app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
}*/
</style>
