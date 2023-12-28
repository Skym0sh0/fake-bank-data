<template>
    <v-card class="px-5">
        <v-card-title class="p-2">
            <div class="w-100 d-flex justify-content-between align-items-center">
                <span>Categories</span>

                <v-btn-toggle :dense="true">
                    <v-btn @click="loadCategories"
                           :loading="isLoading"
                           color="accent"
                           :small="true">
                        Reload
                    </v-btn>
                    <v-btn @click="addNewRootCategory"
                           :loading="isLoading"
                           color="primary"
                           :small="true">
                        Neue Category
                    </v-btn>
                </v-btn-toggle>
            </div>
        </v-card-title>

        <v-container class="pt-0">
            <v-row class="py-0">
                <v-col class="py-0" :cols="selectedForDetails.isSelected ? 8 : 12">
                    <category-list :categories-by-id="categoriesById"
                                   :categories="categories"
                                   :is-loading="isLoading"
                                   @newRootCategory="addNewRootCategory"
                                   @click="addNewParentCategory"
                                   @newCategory="addNewCategoryTo"
                                   @deleteCategory="deleteCategory"
                                   @onReassign="onDrop"
                                   @open="selectForDetailedView"/>
                </v-col>

                <v-col v-if="selectedForDetails.isSelected" :cols="4">
                    <div class="fixed-position-editor">
                        <category-details ref="detail-form"
                                          :categories-by-id="categoriesById"
                                          :entity="selectedForDetails.entity"
                                          :is-new="selectedForDetails.isNew"
                                          :is-loading="isLoading"
                                          @createAsChild="createNewChildCategory"
                                          @createAsRoot="createNewRootCategory"
                                          @update="updateCategory"
                                          @close="cancelActiveForm"/>
                    </div>
                </v-col>
            </v-row>
        </v-container>
    </v-card>
</template>

<script>
import {api} from "@/api/RegularIncomeAPI"
import CategoryDetails from "@/components/categories/CategoryDetails.vue";
import CategoryList from "@/components/categories/CategoryList.vue";
import _ from 'lodash';

export default {
    name: "CategoryOverview",
    components: {
        CategoryList,
        CategoryDetails,
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
        addNewCategoryTo(payload) {
            this.newCategory(payload.parentId)
        },
        addNewRootCategory() {
            this.newCategory(null)
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
                        this.categories = _.filter(this.categories, cur => cur.id !== res.id)

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
        deleteCategory(category) {
            this.isLoading = true

            this.cancelActiveForm()

            api.getCategories()
                .deleteCategory(category)
                .then(() => {
                    this.categories = _.filter(this.categories, cur => cur.id !== category.id)
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
.fixed-position-editor {
    position: sticky;
    top: 5em;
    bottom:5em;
}
</style>
