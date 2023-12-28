<template>
    <v-card class="pa-2">

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
                <category-list :categories-by-id="categoriesById"
                               :categories="categories"
                               :is-loading="isLoading"
                               @click="addNewParentCategory"
                               @newCategory="addNewCategoryTo"
                               @deleteCategory="deleteCategory"
                               @onReassign="onDrop"
                               @open="selectForDetailedView"/>
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
        addNewCategoryTo(payload) {
            this.newCategory(payload.parentId)
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
</style>
