<template>
    <div class="w-100 h-100">
        <div v-if="isLoading"
             class="d-flex justify-content-center align-items-center p-3"
             style="background-color: rgb(0,0,0, 0.1)">
            <b-spinner label="Loading ..." variant="primary" v-bind="$attrs"/>
        </div>
        <div v-else>
            <slot/>
        </div>
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