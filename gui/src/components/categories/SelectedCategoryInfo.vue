<template>
    <v-snackbar :value="isOpen"
                :timeout="0"
                :vertical="true"
                color="info"
                :right="true">
        <div v-show="opened">
            <ul>
                <li v-for="cat in categories" :key="cat">
                    {{ cat }}
                </li>
            </ul>
        </div>

        <div class="d-flex justify-content-between align-items-center">
            <div class="btn v-card--hover subtitle-2" @click="clearSelection">
                <v-icon :left="true" :dense="true" :small="true">
                    mdi-delete
                </v-icon>
                {{ categoryCount }} ausgewählte Kategorien
            </div>

            <v-btn :icon="true"
                   :small="true"
                   @click="toggleExpand"
                   class="m-0">
                <v-icon :dense="true">
                    {{ opened ? "mdi-chevron-down" : "mdi-chevron-up" }}
                </v-icon>
            </v-btn>
        </div>
    </v-snackbar>
</template>

<script>
export default {
    name: "SelectedCategoryInfo",
    props: {
        selectedIds: {
            type: Array,
            required: true,
        },
        categoriesById: {
            type: Object,
            required: true,
        },
    },
    data() {
        return {
            opened: false,
        };
    },
    methods: {
        toggleExpand() {
            this.opened = !this.opened;
        },
        clearSelection() {
            this.$emit("clear");
        },
    },
    computed: {
        isOpen() {
            return this.categoryCount > 0;
        },
        categoryCount() {
            return (this.selectedIds || []).length;
        },
        categories() {
            return this.selectedIds.map(id => this.categoriesById[id])
                .map(cat => cat.name)
                .sort();
        },
    },
}
</script>

<style scoped>
</style>