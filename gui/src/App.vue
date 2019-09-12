<template>
    <div id="app" class="p-2">
        <Header/>

        <router-view/>
    </div>
</template>

<script>
    import Header from './components/layout/Header'

    export default {
        name: "App",
        components: {
            Header
        }
    }
</script>

<style scoped>
    #app {
        font-family: 'Avenir', Helvetica, Arial, sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        text-align: center;
        color: #2c3e50;
        margin-top: 60px;
    }
</style>
