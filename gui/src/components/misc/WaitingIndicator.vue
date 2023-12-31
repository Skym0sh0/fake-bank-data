<template>
    <v-overlay :value="isLoading">
        <v-progress-circular
                :indeterminate="true"
                :size="64"/>
    </v-overlay>
</template>

<script>
export default {
    name: "WaitingIndicator",
    props: {
        isLoading: {
            type: Boolean,
            required: true,
        },
    },
}
</script>

<style scoped>

</style>