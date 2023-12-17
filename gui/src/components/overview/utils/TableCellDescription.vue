<template>
    <div>
        <div :id="`value-element-${index}`">
            {{ shortValue }}
        </div>
        <b-tooltip :target="`value-element-${index}`" triggers="hover">
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
        shortValue() {
            return _.truncate(this.value, {length: 32});
        }
    },
}
</script>

<style scoped>
</style>