<template>
    <div v-if="categoriesForTreeView && categoriesForTreeView.length"
         class="overflow-y-auto" style="background-color: lightcoral; height: 55vh">
        <v-treeview :items="categoriesForTreeView"
                    :active.sync="selected"
                    :open.sync="opened"
                    :activatable="true"
                    :selectable="!true"
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
</template>

<script>
import _ from 'lodash';

export default {
    name: "CategoryList",
    props: {
        categories: {
            type: Array,
            required: true,
        },
        quickfilter: {
            type: String,
            required: false
        },
        reallocationEnabled: {
            type: Boolean,
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
            console.log("add new category to parent with id", id)
            this.$emit("newCategory", {parentId: id});
        },
        deleteCategory(id) {
            console.log("delete category with id", id);
            this.$emit("deleteCategory", this.categoriesById[id])
        },
        onDrop(trgtItem, srcItem) {
            if (!this.reallocationEnabled || trgtItem.id === srcItem.id)
                return

            console.log("Move item",
                {id: srcItem.id, name: srcItem.name},
                "to target",
                {id: trgtItem.id, name: trgtItem.name}
            );

            this.$emit("onReassign", {
                source: srcItem,
                target: trgtItem,
            });

            // this.$nextTick(() => this.opened.push(trgtItem.id, srcItem.id));
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
        categoriesForTreeView() {
            const resolver = cat => {
                const children = this.getFilteredCategories.filter(c => c.parentId === cat.id).map(resolver)

                return {
                    ...cat,
                    children: _.sortBy(children, x => x.name),
                }
            }

            return _.sortBy([...this.getParentCategories.map(resolver)], x => x.name)
                .filter((x, idx) => idx <= 20)
        },
    },
    mounted() {
        this.getParentCategories.filter((c, idx) => idx < 10)
            .map(c => c.id)
            .forEach(id => this.opened.push(id))
    },
}
</script>

<style scoped>

</style>