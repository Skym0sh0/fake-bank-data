<template>
    <b-input-group>
        <b-form-input :id="`${id}-category-input`"
                      :list="`${id}-category-input-list`"
                      @input="onCategoryInput"
                      :value="currentSearch"
                      :state="isValidState"
                      autocomplete="off"
                      size="sm"
                      type="text">
        </b-form-input>
        <b-input-group-append v-if="isUnknownCategory">
            <b-button size="sm"
                      @click="onAddCategory"
                      :disabled="$v.currentSearch.$invalid">
                +
            </b-button>
        </b-input-group-append>

        <datalist :id="`${id}-category-input-list`">
            <option v-for="cat in categories" :key="cat.id">
                {{ cat.name }}
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
        options: {
            required: true,
            type: Array,
        },
        state: {
            required: true,
            type: Boolean,
        }
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
        categories() {
            const tmp = [...this.options]

            tmp.sort((a, b) => {
                return a.name.localeCompare(b.name);
            });

            return tmp;
        },
        categoriesByName() {
            const map = {};
            this.options.forEach(cat => map[cat.name] = cat);
            return map;
        },
        categoriesById() {
            const map = {};
            this.options.forEach(cat => map[cat.id] = cat);
            return map;
        },
        isValidState() {
            return this.state && !this.isUnknownCategory && !this.$v.currentSearch.$invalid;
        },
    },
    watch: {
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
    },
    mounted() {
        if (this.value) {
            this.currentSearch = this.categoriesById[this.value].name;
        }
    },
    mixins: [
        validationMixin,
    ],
}
</script>

<style scoped>
</style>