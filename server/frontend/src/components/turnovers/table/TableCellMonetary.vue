<template>
    <div>
        {{ formattedMonetary }}
    </div>
</template>

<script>
export default {
    name: "TableCellMonetary",
    props: {
        value: {
            required: true,
            type: Number,
        },
    },
    computed: {
        formattedMonetary() {
            const eu = new Intl.NumberFormat('de-DE', {
                style: 'currency',
                currency: 'EUR',
            });
            return eu.format(this.value / 100);
        }
    }
}
</script>

<style scoped>

</style>