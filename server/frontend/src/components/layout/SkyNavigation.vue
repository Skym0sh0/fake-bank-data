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
                    {{ link.icon }}
                </v-icon>
            </v-list-item-action>
            <v-list-item-content>
                <v-list-item-title>
                    {{ link.title }}
                </v-list-item-title>
            </v-list-item-content>
        </v-list-item>

        <v-divider/>
    </v-list>
</template>

<script>
import getPages from "@/util/pages";

export default {
    name: "SkyNavigation",
    computed: {
        links() {
            return getPages();
        }
    },
}
</script>

<style scoped>

</style>