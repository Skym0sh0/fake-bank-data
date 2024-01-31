<template>
    <div class="d-flex justify-content-start flex-wrap align-content-start" style="max-width: 35vh">
        <template v-for="s in enrichedSuggestions">
            <b-button :key="s.id"
                      @click="onClick(s.id)"
                      :variant="s.color"
                      size="sm"
                      style="font-size: 0.75em; padding: 0 2px; margin: 0.1em">
                {{ s.label }}
            </b-button>
        </template>
    </div>
</template>

<script>
import _ from "lodash";

export default {
    name: "CategorySuggestion",
    props: {
        index: {
            type: Number,
            required: true,
        },
        suggestions: {
            type: Array,
            required: true,
        },
        categoriesById: {
            type: Object,
            required: true,
        },
    },
    computed: {
        enrichedSuggestions() {
            const colors = [
                "success",
                "primary",
                "dark",
                "secondary",
                "warning",
                "danger",
                "info",
                "light",
            ];

            return _.sortBy(this.suggestions, s => s.frequency)
                .map((s, idx) => ({
                    id: s.categoryId,
                    label: this.categoriesById[s.categoryId].name,
                    frequency: s.frequency,
                    color: colors[idx % colors.length],
                }))
        },
    },
    methods: {
        onClick(categoryId) {
            this.$emit("select", {index: this.index, categoryId: categoryId})
        },
    },
}
</script>

<style scoped>

</style>
