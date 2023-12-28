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
                <category-list :categories="categories"
                               :quickfilter="quickfilter"
                               :reallocation-enabled="reallocationEnabled"
                               :is-loading="isLoading"
                               @newCategory="addNewCategoryTo"
                               @deleteCategory="deleteCategory"
                               @onReassign="onDrop"
                               @open="selectForDetailedView"
                               @close="onNoSelection"/>
            </v-card-text>
        </v-card>
      </v-col>

      <v-col>
        <v-card v-if="selectedForDetails.isSelected" class="position-sticky">
          <v-card-title>
            Category Details
          </v-card-title>

          <v-card-text>
            <v-form ref="form">
              <v-text-field v-model="selectedForDetails.entity.name"
                            label="Name"
                            placeholder="Name"
                            :rules="[value => !!value || 'Name is required']"
                            outlined/>

              <v-textarea v-model="selectedForDetails.entity.description"
                          label="Description"
                          placeholder="Description"
                          outlined/>

              <v-text-field v-show="selectedForDetails.entity.createdAt"
                            :value="formatDate(selectedForDetails.entity.createdAt)"
                            label="Created At"
                            readonly outlined/>

              <v-text-field v-show="selectedForDetails.entity.updatedAt"
                            :value="formatDate(selectedForDetails.entity.updatedAt)"
                            label="Updated At"
                            readonly outlined/>

              <v-btn @click="saveActiveForm"
                     :disabled="isLoading"
                     color="success">
                Save
              </v-btn>
              <v-btn @click="cancelActiveForm"
                     :disabled="isLoading"
                     color="warning">
                Cancel
              </v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-card>
</template>

<script>
import {api} from "@/api/RegularIncomeAPI"
import * as moment from "moment";
import {normalizeCategory} from "@/util/Normalizer";
import CategoryList from "@/components/categories/CategoryList.vue";

export default {
  name: "CategoryOverview",
  components: {
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
          .finally(()=>{
              this.isLoading = false
          })
    },
    formatDate(date) {
      if (!date)
        return null

      return moment(date).format("YYYY-MM-DD HH:mm:ss")
    },
    newCategory(parentId) {
      if (this.selectedForDetails.isSelected)
        this.$refs.form.resetValidation()

      this.selectedForDetails.isNew = true
      this.selectedForDetails.isSelected = true
      this.selectedForDetails.parentId = parentId
      this.selectedForDetails.entity = {
        name: "",
        description: "",
      }
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
    selectForDetailedView(id) {
      this.selectedForDetails.isNew = false
      this.selectedForDetails.isSelected = true
      this.selectedForDetails.entity = this.categoriesById[id]
      this.selectedForDetails.parentId = this.categoriesById[id].parentId

      this.selected = [id]
    },
    onNoSelection(){
      console.log("nothing selected")
    },
    cancelActiveForm() {
      this.selectedForDetails.isNew = null
      this.selectedForDetails.isSelected = false
      this.selectedForDetails.parentId = null
      this.selected = []
    },
    saveActiveForm() {
      if (this.$refs.form.validate()) {
        this.isLoading = true

        const normalized = normalizeCategory(this.selectedForDetails.entity)

        const finisher = res => {
          this.categories.push(res)
          this.selectForDetailedView(res.id)
          this.isLoading = false
        }

        if (this.selectedForDetails.isNew) {
          if (!this.selectedForDetails.parentId) {
            api.getCategories()
                .postCategory(normalized)
                .then(res => finisher(res))
          } else {
            api.getCategories()
                .postChildCategory(this.selectedForDetails.parentId, normalized)
                .then(res => finisher(res))
          }
        } else {
          api.getCategories()
              .patchCategory(normalized)
              .then(res => {
                const idx = this.categories.findIndex(cat => cat.id === res.id)
                if (idx >= 0)
                  this.categories.splice(idx, 1)

                finisher(res)
              })
        }
      }
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
    getFilteredCategories() {
      if (!this.quickfilter)
        return this.categories

      const regex = new RegExp(this.quickfilter, "i")

      return this.categories.filter(cat => cat.name.search(regex) >= 0)
    },
    getParentCategories() {
      return [...this.getFilteredCategories.filter(cat => !cat.parentId)]
    },
  },
  mounted() {
    this.loadCategories()
  },
}
</script>

<style scoped>
</style>
