<template>
    <div class="d-inline-flex align-center justify-content-center">
        <div>
            <v-img src="../../assets/icons8-finance-document-100.png"
                   lazy-src="../../assets/icons8-finance-document-100.png"
                   :max-width="64"
                   :max-height="64"
                   :aspect-ratio="1.0"/>

            <v-toolbar-title class="ma-2 pa-2">
                Regular Income
            </v-toolbar-title>
        </div>

        <v-spacer/>

        <div v-for="(link, idx) in links" :key="idx">
            <v-btn :to="link.link" class="ma-2">
                <v-icon class="mr-2" v-if="link.icon">
                    {{link.icon}}
                </v-icon>
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
                    {link: "/", title: "Home", icon: 'mdi-apps',},
                    {link: "/statements", title: "Statement Overview", icon: 'mdi-file-document-box',},
                    {link: "/reports", title: "Reports", icon: 'mdi-graphql',},
                    {link: "/about", title: "About", icon: 'mdi-help-circle',},
                ]
            }
        },
    }
</script>

<style scoped>
</style>
