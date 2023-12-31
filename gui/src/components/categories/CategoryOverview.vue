<template>
    <v-card class="px-5">
        <waiting-indicator :is-loading="isLoading"/>

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
                <v-col class="py-0" :cols="showDetails ? 8 : 12">
                    <category-list :categories-by-id="categoriesById"
                                   :categories="categories"
                                   @newCategory="addNewCategoryTo"
                                   @deleteCategory="deleteCategory"
                                   @onReassign="reassignCategories"
                                   @edit="openEditView"/>
                </v-col>

                <v-col v-if="showDetails" :cols="4">
                    <div class="fixed-position-editor">
                        <div class="details-view">
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
                        </div>
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
import WaitingIndicator from "@/components/misc/WaitingIndicator.vue";

export default {
    name: "CategoryOverview",
    components: {
        WaitingIndicator,
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
        openEditView(id) {
            if (this.selectedForDetails.entity && this.selectedForDetails.entity.id === id)
                return;

            this.selectedForDetails.isNew = false
            this.selectedForDetails.isSelected = true
            this.selectedForDetails.entity = {...this.categoriesById[id]}
        },
        cancelActiveForm() {
            this.selectedForDetails.isNew = null
            this.selectedForDetails.isSelected = false
            this.selectedForDetails.entity = null
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
                    this.openEditView(res.id)
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
        reassignCategories(payload) {
            this.isLoading = true

            Promise.all(
                payload.sources.map(id => this.categoriesById[id])
                    .map(source => api.getCategories()
                        .reassignCategory(payload.target, source))
            )
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
        showDetails() {
            return this.selectedForDetails.isSelected;
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
    bottom: 5em;
    height: 88vh;
}
</style>
