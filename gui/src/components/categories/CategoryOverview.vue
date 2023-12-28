<template>
    <v-card class="pa-2">
        <global-events @keydown.17.prevent="reallocationEnabled = true"
                       @keyup.17.prevent="reallocationEnabled = false"/>

        <v-card-title>
            Categories
        </v-card-title>

        <v-card-subtitle>
            <v-btn @click="loadCategories"
                   :loading="isLoading">
                Reload
            </v-btn>
        </v-card-subtitle>
        <v-row>
            <v-col>
                <v-card style="height: 80vh">
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
                                           :loading="isLoading"
                                           @click="addNewParentCategory">
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
                        <category-list :categories-by-id="categoriesById"
                            :categories="categories"
                                       :quickfilter="quickfilter"
                                       :reallocation-enabled="reallocationEnabled"
                                       :is-loading="isLoading"
                                       @newCategory="addNewCategoryTo"
                                       @deleteCategory="deleteCategory"
                                       @onReassign="onDrop"
                                       @open="selectForDetailedView"/>
                    </v-card-text>
                </v-card>
            </v-col>

            <v-col>
                <category-details v-if="selectedForDetails.isSelected"
                                  ref="detail-form"
                                  :categories-by-id="categoriesById"
                                  :entity="selectedForDetails.entity"
                                  :is-new="selectedForDetails.isNew"
                                  :is-loading="isLoading"
                                  @createAsChild="createNewChildCategory"
                                  @createAsRoot="createNewRootCategory"
                                  @update="updateCategory"
                                  @close="cancelActiveForm"/>
            </v-col>
        </v-row>
    </v-card>
</template>

<script>
import {api} from "@/api/RegularIncomeAPI"
import CategoryList from "@/components/categories/CategoryList.vue";
import CategoryDetails from "@/components/categories/CategoryDetails.vue";

export default {
    name: "CategoryOverview",
    components: {
        CategoryDetails,
        CategoryList
    },
    data() {
        return {
            isLoading: false,
            categories: [],
            selectedForDetails: {
                isNew: null,
                isSelected: false,
                parentId: null,
                entity: null,
            },
            reallocationEnabled: false,
            quickfilter: null,
        }
    },
    methods: {
        loadCategories() {
            this.isLoading = true

            return api.getCategories()
                .fetchCategories()
                .then(res => {
                    this.categories = res
                    return this.categories
                })
                .finally(() => {
                    this.isLoading = false
                })
        },
        newCategory(parentId) {
            if (this.selectedForDetails.isSelected)
                this.$refs["detail-form"].reset()

            this.selectedForDetails.isNew = true
            this.selectedForDetails.isSelected = true
            this.selectedForDetails.entity = {
                parentId: parentId,
                name: "",
                description: "",
            }
        },
        selectForDetailedView(id) {
            this.selectedForDetails.isNew = false
            this.selectedForDetails.isSelected = true
            this.selectedForDetails.entity = {...this.categoriesById[id]}

            this.selected = [id]
        },
        cancelActiveForm() {
            this.selectedForDetails.isNew = null
            this.selectedForDetails.isSelected = false
            this.selectedForDetails.entity = null
            this.selected = []
        },
        createNewRootCategory(cat) {
            this.doRestCallForCategory(() => {
                return api.getCategories()
                    .postCategory(cat);
            })
        },
        createNewChildCategory(cat) {
            this.doRestCallForCategory(() => {
                    return api.getCategories()
                        .postChildCategory(cat.parentId, cat);
                }
            );
        },
        updateCategory(cat) {
            this.doRestCallForCategory(() => {
                return api.getCategories()
                    .patchCategory(cat)
                    .then(res => {
                        const idx = this.categories.findIndex(cat => cat.id === res.id)
                        if (idx >= 0)
                            this.categories.splice(idx, 1)

                        return res;
                    })
            });
        },
        doRestCallForCategory(call) {
            this.isLoading = true

            call()
                .then(res => {
                    this.categories.push(res)
                    this.selectForDetailedView(res.id)
                })
                .finally(() => {
                    this.isLoading = false
                })
        },
        addNewCategoryTo(payload) {
            this.newCategory(payload.parentId)
        },
        deleteCategory(category) {
            this.isLoading = true

            this.cancelActiveForm()

            api.getCategories()
                .deleteCategory(category)
                .then(() => {
                    this.loadCategories()
                })
                .finally(() => {
                    this.isLoading = false
                })
        },
        addNewParentCategory() {
            this.newCategory(null)
        },
        onDrop(payload) {
            this.isLoading = true

            api.getCategories()
                .reassignCategory(payload.target, payload.source)
                .then(() => this.loadCategories())
                .finally(() => {
                    this.isLoading = false
                })
        },
    },
    computed: {
        categoriesById() {
            return this.categories.reduce((old, cur) => ({...old, [cur.id]: cur}), {})
        },
    },
    mounted() {
        this.loadCategories()
    },
}
</script>

<style scoped>
</style>
