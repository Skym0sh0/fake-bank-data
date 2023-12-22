<template>
    <v-list nav dense>
        <v-list-item>
            <v-list-item-content>
                <v-list-item-title>
                    Topics
                </v-list-item-title>
                <v-list-item-subtitle>
                    Table of Content
                </v-list-item-subtitle>
            </v-list-item-content>
        </v-list-item>

        <v-divider/>

        <v-list-item v-for="(link, idx) in links" :key="idx" link :to="link.link">
            <v-list-item-action>
                <v-icon>
                    {{link.icon}}
                </v-icon>
            </v-list-item-action>
            <v-list-item-content>
                <v-list-item-title>
                    {{link.title}}
                </v-list-item-title>
            </v-list-item-content>
        </v-list-item>

        <v-divider/>
    </v-list>
</template>

<script>
    export default {
        name: "SkyNavigation",
        data() {
            return {
                links: [
                    {link: "/", title: "Home", icon: 'mdi-apps',},
                    {link: "/statements", title: "Statement Overview", icon: 'mdi-file-document-box',},
                    {link: "/turnovers", title: "Turnover Overview", icon: 'mdi-file-document-box',},
                    {link: "/categories", title: "Category Overview", icon: 'mdi-file-document-box',},
                    {link: "/reports", title: "Reports", icon: 'mdi-graphql',},
                    {link: "/about", title: "About", icon: 'mdi-help-circle',},
                ]
            }
        },
    }
</script>

<style scoped>

</style>