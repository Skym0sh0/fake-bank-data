<template>
    <v-card class="pa-2">
        <v-card-title>
            Categories
        </v-card-title>

        <v-row>
            <v-col>
                <v-card>
                    <v-card-title>
                        Category List:
                    </v-card-title>

                    <v-card-text>
                        <div v-if="categories.length">
                            <v-treeview :items="categories"
                                        :open-on-click="true"
                                        :activatable="true"
                                        :hoverable="true"
                                        :dense="true"
                                        @update:active="onUpdateActive"
                            />
                        </div>
                    </v-card-text>
                </v-card>
            </v-col>

            <v-col>
                <v-card v-if="activeCategory">
                    <v-card-title>
                        Category Details
                    </v-card-title>

                    <v-card-text>
                        <v-text-field v-model="activeCategory.name"
                                      label="Name"
                                      placeholder="Name"
                                      outlined/>

                        <v-textarea v-model="activeCategory.description"
                                    label="Description"
                                    placeholder="Description"
                                    outlined/>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>
    </v-card>
</template>

<script>
    import {api} from "../../api/RegularIncomeAPI"

    export default {
        name: "CategoryOverview",
        data() {
            return {
                categories: [],
                activeCategory: null,
            }
        },
        methods: {
            onUpdateActive(activeIds) {
                if (activeIds && activeIds.length > 0) {
                    const activeId = activeIds[0]

                    const actives = this.flatCategories.filter(c => c.id === activeId)
                    if (actives && actives.length > 0)
                        this.activeCategory = actives[0]
                }
            },
        },
        computed: {
            flatCategories() {
                const flatter = cat => [cat, ...cat.children.flatMap(flatter)]

                return this.categories.flatMap(flatter)
            },
        },
        mounted() {
            api.fetchCategories()
                .then(res => {
                    const sorter = cat => {
                        cat.children.sort((a, b) => {
                            return a.name.localeCompare(b.name)
                        })
                        cat.children.forEach(sorter)
                    }
                    res.forEach(sorter)

                    this.categories = res
                })
        },
    }
</script>

<style scoped>
</style>
