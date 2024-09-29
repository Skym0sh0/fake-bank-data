<template>
    <div>
        <v-divider :vertical="true"/>

        <b-btn-group>
            <category-volume-graph :category="category">
                <template v-slot:button="{ clickCallback }">
                    <v-tooltip :top="true">
                        <template v-slot:activator="{ on, attrs }">
                            <v-btn :icon="true"
                                   :small="true"
                                   color="warning"
                                   @click.stop="clickCallback"
                                   v-bind="attrs" v-on="on">
                                <v-icon :small="true">
                                    mdi-chart-timeline-variant-shimmer
                                </v-icon>
                            </v-btn>
                        </template>
                        Graph
                    </v-tooltip>
                </template>
            </category-volume-graph>

            <category-usage-dialog :category="category">
                <template v-slot:button="{ clickCallback }">
                    <v-tooltip :top="true">
                        <template v-slot:activator="{ on, attrs }">
                            <v-btn :icon="true"
                                   :small="true"
                                   color="warning"
                                   @click.stop="clickCallback"
                                   v-bind="attrs" v-on="on">
                                <v-icon :small="true">
                                    mdi-format-list-bulleted
                                </v-icon>
                            </v-btn>
                        </template>
                        Zeige benutzte Turnovers
                    </v-tooltip>
                </template>
            </category-usage-dialog>

            <v-tooltip :top="true">
                <template v-slot:activator="{ on, attrs }">
                    <v-btn :icon="true"
                           :small="true"
                           color="success"
                           @click.stop="editCategory"
                           v-bind="attrs" v-on="on">
                        <v-icon :small="true">
                            mdi-playlist-edit
                        </v-icon>
                    </v-btn>
                </template>
                Kategorie editieren
            </v-tooltip>

            <v-tooltip :top="true">
                <template v-slot:activator="{ on, attrs }">
                    <v-btn :icon="true"
                           :small="true"
                           color="purple"
                           @click.stop="addNewCategoryTo"
                           v-bind="attrs" v-on="on">
                        <v-icon :small="true">
                            mdi-playlist-plus
                        </v-icon>
                    </v-btn>
                </template>
                Erstelle neue Unterkategorie
            </v-tooltip>

            <v-tooltip :top="true">
                <template v-slot:activator="{ on, attrs }">
                    <div v-bind="attrs" v-on="on">
                        <v-btn :icon="true"
                               :small="true"
                               color="error"
                               @click.stop="deleteCategory"
                               :disabled="isDeletionForbidden">
                            <v-icon :small="true">
                                mdi-trash-can-outline
                            </v-icon>
                        </v-btn>
                    </div>
                </template>
                {{ deletionButtonTooltip }}
            </v-tooltip>
        </b-btn-group>
    </div>
</template>

<script>
import CategoryUsageDialog from "@/components/categories/CategoryUsageDialog.vue";
import CategoryVolumeGraph from "@/components/categories/CategoryVolumeGraph.vue";

export default {
    name: "CategoryTreeItemButtons",
    components: {CategoryVolumeGraph, CategoryUsageDialog},
    props: {
        category: {
            type: Object,
            required: true,
        },
    },
    computed: {
        isRootCategory() {
            return !this.category.parentId;
        },
        hasChildren() {
            return this.category.children && this.category.children.length > 0;
        },
        isUsed() {
            return this.category.usageCount > 0;
        },
        isDeletionForbidden() {
            return this.hasChildren || (this.isRootCategory && this.isUsed);
        },
        deletionButtonTooltip() {
            if (!this.isDeletionForbidden) {
                return "Kategorie löschen";
            }

            if (this.hasChildren)
                return "Kategorie kann nicht gelöscht werden, da noch Unterkategorien existieren."

            return `Die Kategorie kann nicht gelöscht werden, da sie noch in ${this.category.usageCount} Turnovers benutzt wird.`;
        },
    },
    methods: {
        editCategory() {
            this.$emit('editCategory', this.category.id)
        },
        addNewCategoryTo() {
            this.$emit('addNewChildCategory', this.category.id)
        },
        deleteCategory() {
            this.$emit('deleteCategory', this.category.id)
        },
    },
}
</script>

<style scoped>

</style>
