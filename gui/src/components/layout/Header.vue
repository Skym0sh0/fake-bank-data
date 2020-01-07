<template>
    <div class="d-inline-flex align-center justify-content-center">
        <v-card-text>
            Regular Income
        </v-card-text>

        <v-spacer/>

        <div v-for="(link, idx) in links" :key="idx">
            <v-btn :to="link.link" class="mr-2">
                {{link.title}}
            </v-btn>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'Header',
        data() {
            return {
                links: [
                    {link: "/", title: "Home",},
                    {link: "/statements", title: "Statement Overview",},
                    {link: "/reports", title: "Reports",},
                    {link: "/about", title: "About",},
                ]
            }
        },
    }
</script>

<style scoped>
</style>
