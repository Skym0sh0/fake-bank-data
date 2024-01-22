<template>
    <v-card>
        <v-treeview :items="categoriesAsTree"
                    :active.sync="selected"
                    :open.sync="opened"
                    :activatable="true"
                    :multiple-active="true"
                    :selectable="false"
                    :hoverable="true"
                    :dense="true"
                    :return-object="false"
                    :transition="true"
                    :rounded="true">

            <template v-slot:prepend="{ item }">
                <drop @drop="onDrop(item, ...arguments)">
                    <drag :transfer-data="item" @dragstart="onDragstart">
                        <v-icon class="drag-point">
                            mdi-drag
                        </v-icon>
                    </drag>
                </drop>
            </template>

            <template v-slot:label="{ item }">
                <drop @drop="onDrop(item, ...arguments)">
                    <v-badge :content="item.children.length"
                             :value="item.children.length"
                             color="accent"
                             :inline="true">
                        {{ item.name }}
                    </v-badge>
                </drop>
            </template>

            <template v-slot:append="{ item }">
                <b-btn-group>
                    <category-usage-dialog :category="item">
                        <template v-slot:button="{ clickCallback }">
                            <v-btn :icon="true"
                                   :small="true"
                                   color="warning"
                                   @click="clickCallback">
                                <v-icon :small="true">
                                    mdi-format-list-bulleted
                                </v-icon>
                            </v-btn>
                        </template>
                    </category-usage-dialog>

                    <v-btn :icon="true"
                           :small="true"
                           color="success"
                           @click.stop="editCategory(item.id)">
                        <v-icon :small="true">
                            mdi-playlist-edit
                        </v-icon>
                    </v-btn>

                    <v-btn :icon="true"
                           :small="true"
                           color="purple"
                           @click.stop="addNewCategoryTo(item.id)">
                        <v-icon :small="true">
                            mdi-playlist-plus
                        </v-icon>
                    </v-btn>

                    <v-btn :icon="true"
                           :small="true"
                           color="error"
                           @click.stop="deleteCategory(item.id)"
                           :disabled="isDeletionForbidden(item)">
                        <v-icon :small="true">
                            mdi-trash-can-outline
                        </v-icon>
                    </v-btn>
                </b-btn-group>
            </template>
        </v-treeview>

        <selected-category-info :selectedIds="selected"
                                :categories-by-id="categoriesById"
                                @clear="clearSelection">
            <template v-slot:prepend>
                <drag @dragstart="onDragstart">
                    <v-icon class="drag-point">
                        mdi-drag
                    </v-icon>
                </drag>
            </template>
        </selected-category-info>
    </v-card>
</template>

<script>
import _ from 'lodash';
import SelectedCategoryInfo from "@/components/categories/SelectedCategoryInfo.vue";
import CategoryUsageDialog from "@/components/categories/CategoryUsageDialog.vue";

export default {
    name: "CategoryTreeList",
    components: {
        CategoryUsageDialog,
        SelectedCategoryInfo
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
    },
    data() {
        return {
            selected: [],
            opened: [],
        };
    },
    methods: {
        clearSelection() {
            this.selected = [];
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
        editCategory(id) {
            if (!this.opened.includes(id))
                this.opened.push(id)

            this.$emit("edit", id)
        },
        addNewCategoryTo(id) {
            this.$emit("newCategory", {parentId: id});
            this.setOpenRecursively(id)
        },
        deleteCategory(id) {
            this.$emit("deleteCategory", this.categoriesById[id])
        },
        onDragstart(srcItem) {
            if (srcItem)
                this.selected.push(srcItem.id)
        },
        onDrop(trgtItem/*, srcItem*/) {
            if (this.selected.includes(trgtItem.id))
                return;

            this.$emit("onReassign", {
                sources: this.selected,
                target: trgtItem,
            });

            // this.$nextTick(() => this.opened.push(trgtItem.id, srcItem.id));
        },
        isDeletionForbidden(category) {
            const isRoot = !category.parentId;
            const hasChildren = category.children && category.children.length > 0;
            const isUsed = category.usageCount > 0;
            return hasChildren || (isRoot && isUsed);
        }
    },
    watch: {
        categories() {
            this.selected = [];
        }
    },
    computed: {
        parentCategories() {
            return _.sortBy(this.categories.filter(cat => !cat.parentId), x => x.name.toLowerCase())
        },
        categoriesAsTree() {
            const resolver = cat => {
                const children = this.categories.filter(c => c.parentId === cat.id).map(resolver)

                return {
                    ...cat,
                    children: _.sortBy(children, x => x.name.toLowerCase()),
                }
            }

            return this.parentCategories.map(resolver)
        },
    },
}
</script>

<style scoped>
.drag-point {
    cursor: grabbing;
}
</style>
