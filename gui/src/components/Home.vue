<template>
    <div class="d-flex flex-wrap justify-content-start align-items-baseline align-content-center flex-grow-1"
         style="column-gap: 1em; row-gap: 2em">
        <template v-for="(link, idx) in links">
            <v-card no-body :key="idx"
                    :link="true"
                    :to="link.link"
                    width="16em"
                    height="8em"
                    elevation="8">
                <v-card-text>
                    <router-link :to="link.link">
                        <h4>{{ link.title }}</h4>
                    </router-link>

                    <div class="d-flex justify-content-between align-items-baseline">
                        <p class="card-text">{{ link.title }}</p>
                        <v-icon :x-large="true">
                            {{ link.icon }}
                        </v-icon>
                    </div>
                </v-card-text>
            </v-card>
        </template>
    </div>
</template>

<script>
import getPages from "@/util/pages";

export default {
    name: 'home',
    components: {},
    methods: {},
    computed: {
        links() {
            return getPages().filter(p => !p.isStartPage);
        }
    },
}
</script>
