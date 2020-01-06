<template>
    <div class="home">
        <b-card>
            <img alt="Vue logo" src="../assets/logo.png">
            <HelloWorld msg="Welcome to Your Vue.js App"/>
        </b-card>
    </div>
</template>

<script>
    // @ is an alias to /src
    import HelloWorld from '@/components/HelloWorld.vue'

    export default {
        name: 'home',
        components: {
            HelloWorld
        }
    }
</script>
