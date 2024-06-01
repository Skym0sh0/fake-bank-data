<template>
    <v-card class="position-sticky">
        <v-card-title class="justify-content-between">
            <span>Kategorie Details</span>

            <category-usage-dialog v-if="entity && entity.id" :category="entity"/>
        </v-card-title>

        <v-card-subtitle class="py-0">
            <breadcrumps :items="allParentCategoryChain"/>
        </v-card-subtitle>

        <v-card-text>
            <v-form ref="form">
                <v-text-field v-model="entity.name"
                              label="Name"
                              placeholder="Name"
                              :rules="[value => !!value || 'Name is required']"
                              :outlined="true"
                              @input="changeAnything"/>

                <v-textarea v-model="entity.description"
                            label="Beschreibung"
                            placeholder="Beschreibung"
                            :outlined="true"
                            :auto-grow="true"
                            @input="changeAnything"/>

                <v-divider class="my-1"/>

                <budget-area v-model="entity.budget"
                             @newBudget="onNewBudget"
                             @deleteBudget="onDeleteBudget"
                             @changed="changeAnything"/>

                <v-divider class="my-1"/>

                <category-infos :entity="entity"/>

                <div class="d-flex justify-content-end mt-2">
                    <b-btn-group>
                        <v-btn @click="cancelActiveForm"
                               :loading="isLoading"
                               :small="true"
                               color="warning">
                            Abbrechen
                        </v-btn>
                        <v-btn @click="saveActiveForm"
                               :disabled="!hasChanged"
                               :loading="isLoading"
                               :small="true"
                               color="success">
                            Speichern
                        </v-btn>
                    </b-btn-group>
                </div>
            </v-form>
        </v-card-text>
    </v-card>
</template>

<script>
import {normalizeCategory} from "@/util/Normalizer";
import Breadcrumps from "@/components/misc/Breadcrumps.vue";
import CategoryUsageDialog from "@/components/categories/CategoryUsageDialog.vue";
import CategoryInfos from "@/components/categories/CategoryInfos.vue";
import BudgetArea from "@/components/categories/BudgetArea.vue";

export default {
    name: "CategoryDetails",
    components: {BudgetArea, CategoryInfos, CategoryUsageDialog, Breadcrumps},
    props: {
        categoriesById: {
            type: Object,
            required: true,
        },
        entity: {
            type: Object,
            required: true,
        },
        isNew: {
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
            hasChanged: false,
        };
    },
    methods: {
        reset() {
            this.$refs.form.resetValidation()
        },
        changeAnything() {
            this.hasChanged = true;
        },
        saveActiveForm() {
            if (this.$refs.form.validate()) {
                const normalized = normalizeCategory(this.entity)

                if (this.isNew) {
                    if (this.isChild) {
                        this.$emit("createAsChild", normalized)
                    } else {
                        this.$emit("createAsRoot", normalized)
                    }
                } else {
                    this.$emit("update", normalized)
                }

                this.hasChanged = false;
            }
        },
        cancelActiveForm() {
            this.$emit("close");
        },
        onNewBudget() {
            this.$set(this.entity, 'budget', {
                budget: null,
                exceedingThresholdPercent: 10,
            });
            this.changeAnything()
        },
        onDeleteBudget() {
            this.$set(this.entity, 'budget', null);
            this.changeAnything()
        },
    },
    watch: {
        entity(/*newValue, oldValue*/) {
            this.hasChanged = false;
        }
    },
    computed: {
        isChild() {
            return !!this.entity.parentId;
        },
        allParentCategoryChain() {
            let cur = this.entity.parentId;

            const chain = [this.entity.name];
            while (cur) {
                const current = this.categoriesById[cur];
                chain.push(current.name);
                cur = current.parentId;
            }
            return chain.reverse();
        }
    },
}
</script>

<style scoped>
</style>