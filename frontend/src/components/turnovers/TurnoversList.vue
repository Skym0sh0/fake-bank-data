<template>
    <div>
        <b-table :striped="true"
                 :hover="true"
                 :items="sortedList"
                 :fields="fields"
                 primary-key="id"
                 :responsive="true"
                 :fixed="true">
            <template v-slot:cell(Actions)="row">
                <div class="action-buttons">
                    <v-btn color="primary"
                           :small="true"
                           @click="() => onOpen(row.item)">
                        Öffnen
                    </v-btn>
                    <confirmationed-button @click="() => onDelete(row.item)"
                                           default-caption="Löschen"
                                           request-caption="Löschen??"
                                           confirmed-caption="Löschen!!!"
                                           default-color="secondary"
                                           :small="true"/>
                </div>
            </template>
        </b-table>
    </div>
</template>

<script>
import moment from 'moment/moment';
import ConfirmationedButton from "@/components/misc/ConfirmationedButton.vue";

export default {
    name: "TurnoversList",
    components: {ConfirmationedButton},
    props: {
        imports: {
            required: true,
            type: Array,
        }
    },
    methods: {
        formatTimestamp(d) {
            if (!d)
                return null;
            return moment(d).format("YYYY-MM-DD HH:mm");
        },
        formatDate(d) {
            if (!d)
                return null;
            return moment(d).format("YYYY-MM-DD");
        },
        onOpen(item) {
            this.$router.push({name: "turnovers-detail", params: {id: item.id}});
        },
        onDelete(item) {
            this.$emit("onDelete", item);
        }
    },
    computed: {
        fields() {
            return [
                {
                    key: "importedAt",
                    label: "Importiert",
                    sortable: true,
                    formatter: (value) => this.formatTimestamp(value),
                },
                {
                    key: 'firstTurnover',
                    label: "Von",
                    sortable: true,
                    formatter: (value) => this.formatDate(value),
                },
                {
                    key: 'lastTurnover',
                    label: "Bis",
                    sortable: true,
                    formatter: (value) => this.formatDate(value),
                },
                {
                    key: "turnovers",
                    label: "#Umsätze",
                    sortable: true,
                    sortByFormatted: true,
                    formatter: (value) => value.length,
                },
                {
                    key: "Actions",
                    label: ""
                }
            ];
        },
        sortedList() {
            return this.imports || [];
        }
    }
}
</script>

<style scoped>
.action-buttons {
    display: flex;
    justify-content: flex-start;
    gap: 0.5em;
}
</style>
