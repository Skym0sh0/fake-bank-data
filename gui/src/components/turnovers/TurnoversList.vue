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
                    <b-button variant="primary"
                              size="sm"
                              @click="() => onOpen(row.item)">
                        Öffnen
                    </b-button>
                    <b-button variant="danger"
                              size="sm"
                              @click="() => onDelete(row.item)">
                        Löschen
                    </b-button>
                </div>
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
                    label: "Import",
                    sortable: true,
                    formatter: (value) => this.formatTimestamp(value),
                },
                {
                    key: 'firstTurnover',
                    label: "From",
                    sortable: true,
                    formatter: (value) => this.formatDate(value),
                },
                {
                    key: 'lastTurnover',
                    label: "To",
                    sortable: true,
                    formatter: (value) => this.formatDate(value),
                },
                {
                    key: "turnovers",
                    label: "#Turnovers",
                    sortable: true,
                    sortByFormatted: true,
                    formatter: (value) => value.length,
                },
                {
                    key: "Actions",
                    label: "Actions"
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
