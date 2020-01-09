<template>
    <v-app id="app">
        <!--<v-system-bar app fixed window/>-->
        <v-app-bar app color="primary" dark>
            <Header/>
        </v-app-bar>

        <!--<v-navigation-drawer app/>-->

        <v-content app>
            <v-container fluid>
                <router-view/>
            </v-container>
        </v-content>

        <!--<v-footer app/>-->
        <!--<v-bottom-navigation app/>-->
    </v-app>
</template>

<script>
    import Header from './components/layout/Header'

    export default {
        name: "App",
        components: {
            Header,
        },
    }
</script>

<style scoped>
    #app {
        font-family: 'Avenir', Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        color: #2c3e50;
    }
</style>
