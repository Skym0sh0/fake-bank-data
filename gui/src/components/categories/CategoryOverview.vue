<template>
    <v-card class="pa-2">
        <v-card-title>
            Categories
        </v-card-title>

        <v-row>
            <v-col>
                <v-card>
                    <v-card-title>
                        Category List
                    </v-card-title>

                    <v-card-text>
                        <div v-if="categories.length" class="overflow-y-auto" style="height: 70vh">
                            <v-treeview :items="categories"
                                        :activatable="true"
                                        :hoverable="true"
                                        :dense="true"
                                        :return-object="true"
                                        @update:active="onUpdateActive">
                            </v-treeview>
                        </div>
                    </v-card-text>
                </v-card>
            </v-col>

            <v-col>
                <v-card v-if="activeCategory" class="position-sticky">
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

                        <v-text-field v-show="activeCategory.createdAt"
                                      :value="formatDate(activeCategory.createdAt)"
                                      label="Created At"
                                      readonly
                                      outlined/>

                        <v-text-field v-show="activeCategory.updatedAt"
                                      :value="formatDate(activeCategory.updatedAt)"
                                      label="Updated At"
                                      readonly
                                      outlined/>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>
    </v-card>
</template>

<script>
    import {api} from "../../api/RegularIncomeAPI"
    import * as moment from "moment";

    export default {
        name: "CategoryOverview",
        data() {
            return {
                categories: [],
                activeCategory: null,
            }
        },
        methods: {
            formatDate(date) {
                if (!date)
                    return null

                return moment(date).format("YYYY-MM-DD HH:mm:ss")
            },
            onUpdateActive(activeCategories) {
                if (activeCategories && activeCategories.length > 0)
                    this.activeCategory = activeCategories[0]
            },
        },
        computed: {},
        mounted() {
            api.fetchCategories()
                .then(res => {
                    const sorter = cat => {
                        cat.children.sort((a, b) => {
                            return a.name.localeCompare(b.name)
                        })
                        cat.children.forEach(sorter)
                    }

                    res.sort((a, b) => {
                        return a.name.localeCompare(b.name)
                    })

                    res.forEach(sorter)

                    this.categories = res
                })
        },
    }
</script>

<style scoped>
</style>
