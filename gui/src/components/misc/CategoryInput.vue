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
            <option v-for="cat in flattedCategories" :key="cat.id" :value="cat.name">
                {{ cat.parentChain }}
            </option>
        </datalist>
    </b-input-group>
</template>

<script>
import {validationMixin} from 'vuelidate'
import {required} from 'vuelidate/dist/validators.min'

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
        flattedCategories: {
            required: true,
            type: Array,
        },
        categoriesById: {
            required: true,
            type: Object,
        },
        categoriesByName: {
            required: true,
            type: Object,
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
        categoriesByName(/*newOptions, oldOptions*/) {
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
