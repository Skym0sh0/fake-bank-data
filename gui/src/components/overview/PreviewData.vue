<template>
    <div>PREVIEW !

        <div>
            <b-table striped hover :items="rows" />
        </div>
    </div>
</template>

<script>
export default {
    name: "PreviewData",
    props: {
        rows: {
            required: true,
            type: Array,
        },
    },
}
</script>

<style scoped>

</style>