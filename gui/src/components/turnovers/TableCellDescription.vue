<template>
    <div>
        <span :id="`value-element-${index}`">
            {{ shortValue }}
        </span>
        <b-tooltip :target="`value-element-${index}`" triggers="hover" v-if="isTooLong">
            <div>
                {{ value }}
            </div>
        </b-tooltip>
    </div>
</template>

<script>
import _ from 'lodash';

export default {
    name: "TableCellDescription",
    props: {
        index: {
            required: true,
            type: Number,
        },
        value: {
            required: true,
            type: String,
        }
    },
    computed: {
        isTooLong() {
            return this.value !== this.shortValue
        },
        shortValue() {
            return _.truncate(this.value, {length: 32});
        }
    },
}
</script>

<style scoped>
</style>