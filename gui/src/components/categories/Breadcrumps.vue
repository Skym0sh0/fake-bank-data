<template>
    <ul class="theme--light px-0" :style="{fontSize: size}">
        <template v-for="item in items">
            <li :key="`${item}-divider`" class="breadcrump-divider px-1">
                {{ divider }}
            </li>
            <li :key="item" style="display: inline">
                {{ item }}
            </li>
        </template>
    </ul>
</template>

<script>
export default {
    name: "Breadcrumps",
    props: {
        items: {
            type: Array,
            required: true,
        },
        divider: {
            type: String,
            default: "/",
        },
        size: {
            type: String,
            default: "10px",
        },
    },
}
</script>

<style scoped>
.breadcrump-divider {
    display: inline;
}
</style>