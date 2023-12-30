<template>
    <v-card class="position-sticky">
        <v-card-title>
            Category Details
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

                <v-text-field v-show="entity.createdAt"
                              :value="formatDate(entity.createdAt)"
                              label="Created At"
                              :readonly="true"
                              :outlined="true"/>

                <v-text-field v-show="entity.updatedAt"
                              :value="formatDate(entity.updatedAt)"
                              label="Updated At"
                              :readonly="true"
                              :outlined="true"/>

                <v-btn @click="saveActiveForm"
                       :disabled="!hasChanged"
                       :loading="isLoading"
                       color="success">
                    Save
                </v-btn>
                <v-btn @click="cancelActiveForm"
                       :loading="isLoading"
                       color="warning">
                    Cancel
                </v-btn>
            </v-form>
        </v-card-text>
    </v-card>
</template>

<script>
import * as moment from "moment";
import {normalizeCategory} from "@/util/Normalizer";
import Breadcrumps from "@/components/categories/Breadcrumps.vue";

export default {
    name: "CategoryDetails",
    components: {Breadcrumps},
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

</style>