<template>
    <div id="nav">
        <router-link to="/">Home</router-link>
        |
        <router-link to="/statements">Statement Overview</router-link>
        |
        <router-link to="/about">About</router-link>
    </div>
</template>

<script>
    import * as uuid from "uuid";

    export default {
        methods: {
            newId() {
                return uuid.v4()
            },
        },
    }
</script>

<style scoped></style>
