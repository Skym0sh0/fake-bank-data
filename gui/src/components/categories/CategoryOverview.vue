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
            <div v-if="categoriesForTreeView && categoriesForTreeView.length"
                 class="overflow-y-auto" style="height: 55vh">
              <v-treeview :items="categoriesForTreeView"
                          :active.sync="selected"
                          :open.sync="opened"
                          :activatable="true"
                          :hoverable="true"
                          :dense="true"
                          :return-object="false"
                          @update:active="selectForDetailedView">
                <template v-slot:label="{ item }">
                  <drop @drop="onDrop(item, ...arguments)">
                    <drag :transfer-data="item">
                      {{ item.name }}
                    </drag>
                  </drop>
                </template>

                <template v-slot:append="{ item }">
                  <v-btn icon color="primary"
                         @click.stop="addNewCategoryTo(item.id)"
                         :loading="isLoading">
                    <v-icon>
                      mdi-plus
                    </v-icon>
                  </v-btn>

                  <v-btn icon color="secondary"
                         @click.stop="deleteCategory(item.id)"
                         :disabled="item.children && item.children.length > 0"
                         :loading="isLoading">
                    <v-icon>
                      mdi-delete
                    </v-icon>
                  </v-btn>
                </template>
              </v-treeview>
            </div>
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

export default {
  name: "CategoryOverview",
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

      selected: [],
      opened: [],
    }
  },
  methods: {
    loadCategories() {
      this.isLoading = true

      return api.getCategories()
          .fetchCategories()
          .then(res => {
            this.categories = res

            this.isLoading = false

            return this.categories
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
    addNewCategoryTo(parentId) {
      this.newCategory(parentId)
    },
    deleteCategory(id) {
      this.isLoading = true

      this.cancelActiveForm()

      api.getCategories().deleteCategory(this.categoriesById[id])
          .then(() => {
            this.loadCategories()
            this.isLoading = false
          })
    },
    addNewParentCategory() {
      this.newCategory(null)
    },
    selectForDetailedView(selected) {
      if (!selected)
        return
      if (Array.isArray(selected) && selected.length <= 0)
        return

      const id = Array.isArray(selected) ? selected[0] : selected

      this.selectedForDetails.isNew = false
      this.selectedForDetails.isSelected = true
      this.selectedForDetails.entity = this.categoriesById[id]
      this.selectedForDetails.parentId = this.categoriesById[id].parentId

      this.selected = [id]

      this.setOpenRecursively(id)
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
    setOpenRecursively(id) {
      const newlyOpened = new Set(this.opened)

      let current = this.categoriesById[id]
      while (current) {
        newlyOpened.add(current.id)
        current = this.categoriesById[current.parentId]
      }

      this.opened = [...newlyOpened]
    },
    onDrop(trgtItem, srcItem) {
      if (!this.reallocationEnabled || trgtItem === srcItem)
        return

      this.isLoading = true

      api.getCategories().reassignCategory(trgtItem, srcItem)
          .then(() => {
            this.loadCategories()
                .then(() => {
                  this.isLoading = false

                  this.opened.push(trgtItem.id, srcItem.id)
                })
          })
    },
  },
  computed: {
    categoriesById() {
      const mapping = {}
      this.categories.forEach(cat => mapping[cat.id] = cat)
      return mapping
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
    categoriesForTreeView() {
      const cmp = (a, b) => a.name.localeCompare(b.name)

      const resolver = cat => {
        const children = this.getFilteredCategories.filter(c => c.parentId === cat.id).map(resolver)

        children.sort(cmp)

        return {
          ...cat,
          children: children,
        }
      }

      const tmp = [...this.getParentCategories.map(resolver)]
      tmp.sort(cmp)
      return tmp
    },
  },
  mounted() {
    this.loadCategories()
        .then(() => {
          this.getParentCategories.filter((c, idx) => idx < 10)
              .map(c => c.id)
              .forEach(id => this.opened.push(id))
        })
  },
}
</script>

<style scoped>
</style>
