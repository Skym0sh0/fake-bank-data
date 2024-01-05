<template>
    <v-app id="app">
        <!--<v-system-bar app fixed window/>-->
        <v-app-bar app color="primary" dark>
            <v-app-bar-nav-icon @click.stop="drawer = !drawer"/>

            <v-icon class="mx-4" color="warning">
                mdi-currency-eur
            </v-icon>

            <v-toolbar-title class="mr-12 align-center">
                <span class="title">
                    Regular Income
                </span>
            </v-toolbar-title>
        </v-app-bar>

        <v-navigation-drawer app v-model="drawer" dark>
            <sky-navigation/>
        </v-navigation-drawer>

        <v-main app>
            <v-container fluid>
                <router-view/>
            </v-container>
        </v-main>

        <!--<v-footer app/>-->
        <!--<v-bottom-navigation app/>-->
    </v-app>
</template>

<script>
    import SkyNavigation from "./components/layout/SkyNavigation";

    export default {
        name: "App",
        data() {
            return {
                drawer: true,
            }
        },
        components: {
            SkyNavigation,
        },
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
