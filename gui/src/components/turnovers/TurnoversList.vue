<template>
    <div>
        <b-table :striped="true"
                 :hover="true"
                 :items="sortedList"
                 :fields="fields"
                 :responsive="true">
            <template v-slot:cell(Date)="row">
                {{ row.item.date}}
            </template>
            <template v-slot:cell(Turnovers)="row">
                {{ row.item.turnovers.length}}
            </template>
            <template v-slot:cell(Actions)>
                ...
            </template>
        </b-table>
    </div>
</template>

<script>
export default {
    name: "TurnoversList",
    props: {
        imports: {
            required: true,
            type: Array,
        }
    },
    computed: {
        fields() {
            return ["Date", "Turnovers", "Actions"];
        },
        sortedList() {
            return this.imports;
        }
    }
}
</script>

<style scoped>

</style>