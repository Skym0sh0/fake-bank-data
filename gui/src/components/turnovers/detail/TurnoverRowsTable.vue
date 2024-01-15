<template>
    <div>
        <div style="font-size: 0.5em">
            <template v-for="row in rows" >
                <div :key="row.id">{{ row }}</div>
            </template>
        </div>
    </div>
</template>

<script>

export default {
    name: "TurnoverRowsTable",
    props: {
        rows: {
            type: Array,
            required: true,
        }
    },
}
</script>


<style scoped>

</style>
