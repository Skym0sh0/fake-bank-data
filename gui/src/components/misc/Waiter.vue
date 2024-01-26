<template>
    <div class="w-100 h-100">
        <div v-if="isLoading"
             class="d-flex justify-content-center align-items-center p-3 w-100 h-100"
             style="background-color: rgb(0,0,0, 0.1)">
            <b-spinner label="Loading ..." variant="primary" v-bind="$attrs"/>
        </div>
        <template v-else>
            <slot/>
        </template>
    </div>
</template>

<script>
export default {
    name: "Waiter",
    props: {
        isLoading: {
            type: Boolean,
            required: true,
        }
    },
}
</script>

<style scoped>

</style>
