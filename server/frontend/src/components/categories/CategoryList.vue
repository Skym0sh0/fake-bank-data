<template>
    <v-container class="px-0">
        <!--        <global-events @keydown.17.prevent="reallocationEnabled = true"-->
        <!--                       @keyup.17.prevent="reallocationEnabled = false"/>-->

        <v-row>
            <v-col>
                <v-text-field id="quickfilter-text-input-field"
                              v-model="quickfilter"
                              type="text"
                              :dense="true"
                              :clearable="true"
                              label="Filter"
                              hint="Suche Kategorie (auch Regex möglich)"
                              suffix="Regex"/>
            </v-col>
        </v-row>

        <v-row class="py-0">
            <v-col class="py-0">
                <category-tree-list :categories-by-id="categoriesById"
                                    :categories="filteredCategories"
                                    @newCategory="$emit('newCategory', $event)"
                                    @deleteCategory="$emit('deleteCategory', $event)"
                                    @onReassign="$emit('onReassign', $event)"
                                    @edit="$emit('edit', $event)"/>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import CategoryTreeList from "@/components/categories/CategoryTreeList.vue";

export default {
    name: "CategoryList",
    components: {
        CategoryTreeList
    },
    props: {
        categoriesById: {
            type: Object,
            required: true,
        },
        categories: {
            type: Array,
            required: true,
        },
    },
    data() {
        return {
            quickfilter: null,
        }
    },
    computed: {
        filteredCategories() {
            if (!this.quickfilter)
                return this.categories

            const regex = new RegExp(this.quickfilter, "i")

            return this.categories.filter(cat => cat.name.search(regex) >= 0)
        },
    },
}
</script>

<style scoped>

</style>