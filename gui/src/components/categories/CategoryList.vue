<template>
    <v-card style="height: 80vh">
        <global-events @keydown.17.prevent="reallocationEnabled = true"
                       @keyup.17.prevent="reallocationEnabled = false"/>

        <v-card-title>
            Category List
        </v-card-title>

        <v-card-subtitle>
            <v-container>
                <v-row justify="space-between">
                    <v-col>
                        <v-switch v-show="categories && categories.length"
                                  v-model="reallocationEnabled"
                                  :loading="isLoading"
                                  hint="Categories can now be dragged & dropped persistently."
                                  messages="Shortcut: Ctrl"
                                  label="Edit Category Hierarchy"/>
                    </v-col>

                    <v-col>
                        <v-btn fab dark small
                               color="primary" class="mx-2"
                               :loading="isLoading">
                            <v-icon dark>
                                mdi-plus
                            </v-icon>
                        </v-btn>
                    </v-col>
                </v-row>

                <v-row>
                    <v-text-field id="quickfilter-text-input-field"
                                  v-model="quickfilter"
                                  type="text"
                                  :clearable="true"
                                  label="Quickfilter"
                                  hint="Type regex to match items"
                                  suffix="Regex"/>
                </v-row>
            </v-container>
        </v-card-subtitle>

        <v-card-text>
            <category-tree-list :categories-by-id="categoriesById"
                                :categories="categories"
                                :quickfilter="quickfilter"
                                :reallocation-enabled="reallocationEnabled"
                                :is-loading="isLoading"
                                @click="$emit('click', $event)"
                                @newCategory="$emit('newCategory', $event)"
                                @deleteCategory="$emit('deleteCategory', $event)"
                                @onReassign="$emit('onReassign', $event)"
                                @open="$emit('open', $event)"
            />
        </v-card-text>
    </v-card>
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
            reallocationEnabled: false,
            quickfilter: null,
        }
    },
}
</script>

<style scoped>

</style>