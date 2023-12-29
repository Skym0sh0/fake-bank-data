<template>
    <v-container class="py-0">
        <!--        <global-events @keydown.17.prevent="reallocationEnabled = true"-->
        <!--                       @keyup.17.prevent="reallocationEnabled = false"/>-->

        <v-row>
            <v-col class="py-0">
                <v-text-field id="quickfilter-text-input-field"
                              v-model="quickfilter"
                              type="text"
                              :dense="true"
                              :clearable="true"
                              label="Quickfilter"
                              hint="Type regex to match items"
                              suffix="Regex"/>
            </v-col>
        </v-row>

        <v-row class="py-0">
            <v-col class="py-0">
                <category-tree-list :categories-by-id="categoriesById"
                                    :categories="filteredCategories"
                                    :is-loading="isLoading"
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
        isLoading: {
            type: Boolean,
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