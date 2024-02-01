<template>
    <div class="d-flex justify-content-start flex-wrap align-content-start" style="max-width: 35vh">
        <template v-for="s in enrichedSuggestions">
            <div :key="s.id">
                <b-button :id="`index-${index}-suggestion-${s.id}`"
                          @click="onClick(s.id)"
                          :variant="s.color"
                          size="sm"
                          style="font-size: 0.75em; padding: 0 2px; margin: 0.1em">
                    {{ s.label }} - {{ s.frequency }}
                </b-button>
                <b-tooltip :target="`index-${index}-suggestion-${s.id}`" triggers="hover">
                    <table>
                        <tr>
                            <th>Kategorie</th>
                            <td>{{ s.label }}</td>
                        </tr>
                        <tr>
                            <th>Häufigkeit</th>
                            <td>{{ s.frequency }}</td>
                        </tr>
                    </table>
                    <div>{{ s.origins }}</div>
                </b-tooltip>
            </div>
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
            const originMap = {
                amount_value_cents: "Betrag",
                suggested_category: "Bankvorschlag",
                recipient: "Empfänger/Sender",
                description: "Beschreibung",
            };
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

            return _.orderBy(this.suggestions, [s => s.precedence], ['desc'])
                .map((s, idx) => ({
                    id: s.categoryId,
                    label: this.categoriesById[s.categoryId].name,
                    frequency: s.frequency,
                    precedence: s.precedence,
                    origins: `Hergeleitet aus ${s.origins.map(o => originMap[o] || o).join(" & ")}`,
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
