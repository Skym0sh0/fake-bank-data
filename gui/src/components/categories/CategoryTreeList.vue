<template>
    <v-card v-if="categoriesForTreeView && categoriesForTreeView.length"
            class="overflow-y-auto">
        <v-treeview :items="categoriesForTreeView"
                    :active.sync="selected"
                    :open.sync="opened"
                    :activatable="true"
                    :selectable="false"
                    :hoverable="true"
                    :dense="true"
                    :return-object="false"
                    :transition="true"
                    :rounded="true"
                    @update:active="selectForDetailedView">

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
    </v-card>
</template>

<script>
import _ from 'lodash';

export default {
    name: "CategoryTreeList",
    props: {
        categoriesById: {
            type: Object,
            required: true,
        },
        categories: {
            type: Array,
            required: true,
        },
        quickfilter: {
            type: String,
            required: false
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
        selectForDetailedView(selected) {
            if (selected.length === 0) {
                this.$emit("close");
                return;
            }

            if (selected.length === 1) {
                const id = selected[0];

                this.$emit("open", id);
                this.setOpenRecursively(id);
                return;
            }

            console.error("Not expected to have multi openings", selected);
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
        addNewCategoryTo(id) {
            this.$emit("newCategory", {parentId: id});
            this.setOpenRecursively(id)
        },
        deleteCategory(id) {
            this.$emit("deleteCategory", this.categoriesById[id])
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
    computed: {
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
            const resolver = cat => {
                const children = this.getFilteredCategories.filter(c => c.parentId === cat.id).map(resolver)

                return {
                    ...cat,
                    children: _.sortBy(children, x => x.name),
                }
            }

            return _.sortBy([...this.getParentCategories.map(resolver)], x => x.name)
        },
    },
}
</script>

<style scoped>
.drag-point {
    cursor: grabbing;
}
</style>