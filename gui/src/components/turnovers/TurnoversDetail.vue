<template>
    <div>
        <div>Details</div>

        <b-button @click="onBack">Zurück</b-button>
    </div>
</template>

<script>
export default {
    name: "TurnoversDetail",
    methods: {
        onBack() {
            this.$router.back()
        }
    },
}
</script>

<style scoped>

</style>