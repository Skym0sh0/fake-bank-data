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
                    :rounded="false">

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
                    <div class="d-flex justify-content-between">
                        <v-badge :content="item.children.length"
                                 :value="item.children.length"
                                 color="accent"
                                 :inline="true">
                            {{ item.name }}
                        </v-badge>

                        <div class="d-flex" style="gap: 0.25em">
                            <v-icon v-if="item.isNew" color="success">
                                mdi-new-box
                            </v-icon>

                            <v-tooltip v-if="item.budget" :top="true">
                                <template v-slot:activator="{on, attrs}">
                                    <v-icon color="red"
                                            :small="true"
                                            v-bind="attrs"
                                            v-on="on">
                                        mdi-finance
                                    </v-icon>
                                </template>

                                Budget: {{ new MoneyFormatter().formatCents(item.budget.budget * 100) }} +
                                {{ item.budget.exceedingThresholdPercent }} %
                            </v-tooltip>
                        </div>
                    </div>
                </drop>
            </template>

            <template v-slot:append="{ item }">
                <category-tree-item-buttons :category="item"
                                            @editCategory="editCategory"
                                            @addNewChildCategory="addNewCategoryTo"
                                            @deleteCategory="deleteCategory"/>
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
import CategoryTreeItemButtons from "@/components/categories/CategoryTreeItemButtons.vue";
import {MoneyFormatter} from "../../util/Formatters";

export default {
    name: "CategoryTreeList",
    components: {
        CategoryTreeItemButtons,
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
    },
    watch: {
        categories() {
            this.selected = [];
        }
    },
    computed: {
        MoneyFormatter() {
            return MoneyFormatter
        },
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
