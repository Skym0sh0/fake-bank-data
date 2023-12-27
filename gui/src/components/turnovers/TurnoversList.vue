<template>
    <div>
        <b-table :striped="true"
                 :hover="true"
                 :items="sortedList"
                 :fields="fields"
                 :responsive="true">
            <template v-slot:cell(Date)="row">
                {{ formatDate(row.item.importedAt) }}
            </template>
            <template v-slot:cell(Turnovers)="row">
                {{ row.item.turnovers.length }}
            </template>
            <template v-slot:cell(Actions)="row">
                <b-button variant="primary" @click="() => onOpen(row.item)">
                    Öffnen
                </b-button>

                <b-button variant="danger" @click="() => onDelete(row.item)">
                    Löschen
                </b-button>
            </template>
        </b-table>
    </div>
</template>

<script>
import moment from 'moment/moment';

export default {
    name: "TurnoversList",
    props: {
        imports: {
            required: true,
            type: Array,
        }
    },
    methods: {
        formatDate(d) {
            return moment(d).format("YYYY-MM-DD HH:mm");
        },
        onOpen(item) {
            console.log("open button", item)

            this.$router.push({name: "turnovers-detail", params: {id: item.id}});
        },
        onDelete(item) {
            this.$emit("onDelete", item);
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