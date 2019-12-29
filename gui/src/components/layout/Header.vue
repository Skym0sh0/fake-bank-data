<template>
    <div id="nav">
        <router-link to="/">Home</router-link>
        |
        <router-link to="/statements">Statement Overview</router-link>
        |
        <router-link to="/reports">Reports</router-link>
        |
        <router-link to="/about">About</router-link>
    </div>
</template>

<script>
    export default {
        name: 'Header',
    }
</script>

<style scoped>
</style>
