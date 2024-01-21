<template>
    <b-input-group>
        <b-form-input :id="`${id}-category-input`"
                      :list="`${id}-category-input-list`"
                      @input="onCategoryInput"
                      :value="currentSearch"
                      :state="showValidationState"
                      :disabled="disabled"
                      autocomplete="off"
                      size="sm"
                      type="text">
        </b-form-input>
        <b-input-group-append>
            <b-button size="sm"
                      @click="onAddCategory"
                      :variant="isAddableCategory ? 'primary' : 'secondary'"
                      :disabled="!isAddableCategory">
                +
            </b-button>
        </b-input-group-append>

        <datalist :id="`${id}-category-input-list`">
            <option v-for="cat in completableOptions" :key="cat.id" :value="cat.name">
                {{ cat.displayParentChain }}
            </option>
        </datalist>
    </b-input-group>
</template>

<script>
import {validationMixin} from 'vuelidate'
import {required} from 'vuelidate/dist/validators.min'
import _ from 'lodash'

export default {
    name: "CategoryInput",
    components: {},
    props: {
        id: {
            required: true,
            type: String,
        },
        value: {
            // required: true,
            type: String,
        },
        disabled: {
            type: Boolean,
            default: false,
        },
        categories: {
            required: true,
            type: Array,
        },
        state: {
            type: Boolean,
            default: true,
        },
        required: {
            type: Boolean,
            default: true,
        },
    },
    data() {
        return {
            currentSearch: '',
            isUnknownCategory: false,
        };
    },
    validations: {
        currentSearch: {
            required
        }
    },
    computed: {
        flattedCategories() {
            const extract = (cat, parentCategoryNames) => {
                return [
                    {
                        ...cat,
                        parentCategoryNames: parentCategoryNames
                    },
                    ...cat.subCategories.flatMap(c => extract(c, [...parentCategoryNames, cat.name]))
                ];
            };

            return this.categories.flatMap(c => extract(c, []))
                .map(c => ({
                        ...c,
                        displayParentChain: [...c.parentCategoryNames, c.name].join(" > ")
                    })
                )
        },
        completableOptions() {
            return _.sortBy(this.flattedCategories, cat => cat.name)
        },
        categoriesByName() {
            return this.flattedCategories.reduce((old, cur) => ({...old, [cur.name]: cur}), {})
        },
        categoriesById() {
            return this.flattedCategories.reduce((old, cur) => ({...old, [cur.id]: cur}), {})
        },
        isValidState() {
            return this.state && !this.isUnknownCategory && !this.$v.currentSearch.$invalid;
        },
        showValidationState() {
            if (!this.required) {
                if (this.isUnknownCategory && this.currentSearch !== '')
                    return false;

                return null;
            }

            return this.isValidState
        },
        isAddableCategory() {
            return !(!this.isUnknownCategory || this.$v.currentSearch.$invalid)
        },
    },
    watch: {
        value(/*newValue, oldValue*/) {
            this.initWhenOnlyValueIsSet()
        },
        options(/*newOptions, oldOptions*/) {
            this.findOption()
        },
    },
    methods: {
        onCategoryInput(newValue) {
            this.currentSearch = newValue;

            this.findOption()
        },
        findOption() {
            const foundCategory = this.categoriesByName[this.currentSearch];
            if (foundCategory) {
                this.isUnknownCategory = false;
                this.$emit('input', foundCategory.id)
            } else {
                this.isUnknownCategory = true;
                this.$emit('input', null)
            }
        },
        onAddCategory() {
            this.$emit('createCategory', this.currentSearch)
        },
        initWhenOnlyValueIsSet() {
            if (this.value) {
                const foundCategory = this.categoriesById[this.value];
                if (foundCategory) {
                    this.currentSearch = foundCategory.name;
                    this.isUnknownCategory = false;
                    this.$emit('input', foundCategory.id);
                } else {
                    this.currentSearch = "";
                    this.isUnknownCategory = true;
                    this.$emit('input', null);
                }
            }
        },
    },
    mounted() {
        this.initWhenOnlyValueIsSet()
    },
    mixins: [
        validationMixin,
    ],
}
</script>

<style scoped>
</style>
