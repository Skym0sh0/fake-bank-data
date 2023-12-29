<template>
    <v-card v-if="categoriesAsTree && categoriesAsTree.length"
            class="overflow-y-auto">
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
                        <v-icon class="drag-point"
                                :disabled="isLoading">
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
                <v-btn-toggle :dense="true">
                    <v-btn :icon="true"
                           color="primary"
                           @click.stop="editCategory(item.id)"
                           :loading="isLoading">
                        <v-icon>
                            mdi-pen
                        </v-icon>
                    </v-btn>

                    <v-btn :icon="true"
                           color="primary"
                           @click.stop="addNewCategoryTo(item.id)"
                           :loading="isLoading">
                        <v-icon>
                            mdi-plus
                        </v-icon>
                    </v-btn>

                    <v-btn :icon="true"
                           color="secondary"
                           @click.stop="deleteCategory(item.id)"
                           :disabled="item.children && item.children.length > 0"
                           :loading="isLoading">
                        <v-icon>
                            mdi-delete
                        </v-icon>
                    </v-btn>
                </v-btn-toggle>
            </template>
        </v-treeview>

        <selected-category-info :selectedIds="selected"
                                :categories-by-id="categoriesById"
                                @clear="clearSelection"/>
    </v-card>
</template>

<script>
import _ from 'lodash';
import SelectedCategoryInfo from "@/components/categories/SelectedCategoryInfo.vue";

export default {
    name: "CategoryTreeList",
    components: {SelectedCategoryInfo},
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
            this.selected.push(srcItem.id)
        },
        onDrop(trgtItem, srcItem) {
            if (trgtItem.id === srcItem.id)
                return;

            this.$emit("onReassign", {
                source: srcItem,
                target: trgtItem,
            });

            // this.$nextTick(() => this.opened.push(trgtItem.id, srcItem.id));
        },
    },
    watch: {
        categories() {
            this.selected = [];
        }
    },
    computed: {
        parentCategories() {
            return _.sortBy(this.categories.filter(cat => !cat.parentId), x => x.name)
                .filter((x, idx) => idx < 15)
        },
        categoriesAsTree() {
            const resolver = cat => {
                const children = this.categories.filter(c => c.parentId === cat.id).map(resolver)

                return {
                    ...cat,
                    children: _.sortBy(children, x => x.name),
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

.selection-info {
    position: fixed;
    opacity: 50%;
    right: 50%;
    bottom: 1em;
    z-index: 100;
}

.selection-info:hover {
    opacity: 100%;
}
</style>