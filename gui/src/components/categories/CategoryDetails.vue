<template>
    <v-card class="position-sticky">
        <v-card-title class="justify-content-between">
            <span>Category Details</span>

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
                            label="Description"
                            placeholder="Description"
                            :outlined="true"
                            @input="changeAnything"/>

                <div class="category-info px-1">
                    <p v-show="entity.updatedAt">Updated at: {{ formatDate(entity.updatedAt) }}</p>
                    <p v-show="entity.createdAt">Created at: {{ formatDate(entity.createdAt) }}</p>
                    <p v-show="entity.usageCount">Used in entities: {{ entity.usageCount }}</p>
                </div>

                <div class="d-flex justify-content-end">
                    <b-btn-group>
                        <v-btn @click="cancelActiveForm"
                               :loading="isLoading"
                               color="warning">
                            Cancel
                        </v-btn>
                        <v-btn @click="saveActiveForm"
                               :disabled="!hasChanged"
                               :loading="isLoading"
                               color="success">
                            Save
                        </v-btn>
                    </b-btn-group>
                </div>
            </v-form>
        </v-card-text>
    </v-card>
</template>

<script>
import * as moment from "moment";
import {normalizeCategory} from "@/util/Normalizer";
import Breadcrumps from "@/components/misc/Breadcrumps.vue";
import CategoryUsageDialog from "@/components/categories/CategoryUsageDialog.vue";

export default {
    name: "CategoryDetails",
    components: {CategoryUsageDialog, Breadcrumps},
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
        formatDate(date) {
            if (!date)
                return null
            return moment(date).format("YYYY-MM-DD HH:mm:ss")
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
.category-info {
    font-size: 10px;
    display: flex;
    flex-direction: column;
}

.category-info > * {
    margin: 0;
}
</style>