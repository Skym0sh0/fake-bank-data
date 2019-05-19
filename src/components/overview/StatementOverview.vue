<template>
    <div v-if="false">
        <h1>Balance Statements </h1>
        <div class="statements">
            <p v-bind:key="stmt.id" v-for="stmt in statements">{{stmt.date}} => {{stmt.balance}}</p>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'StatementOverview',
        props: ["statements"]
    }
</script>

<style scoped>
    .statements p {
        background: azure;
    }
</style>
