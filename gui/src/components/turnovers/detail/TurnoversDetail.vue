<template>
    <div>
        <waiting-indicator :is-loading="isLoading"/>

        <v-card v-if="!!turnoverImport">
            <v-card-title>
                Turnover File Import
            </v-card-title>

            <v-card-subtitle>
                <div class="d-flex justify-content-between">
                <span>
                    {{ turnoverImport.format }}: "{{ turnoverImport.filename }}" ({{ turnoverImport.encoding }})
                </span>

                    <span>
                    {{ importTimestamp }}
                </span>
                </div>
            </v-card-subtitle>

            <v-card-text>
                <turnover-rows-table :rows="turnoverImport.turnovers"/>
            </v-card-text>

            <v-card-actions class="d-flex justify-content-end">
                <v-btn @click="onBack">Zurück</v-btn>
                <v-btn @click="onSave" color="info">Speichern</v-btn>
            </v-card-actions>
        </v-card>
    </div>
</template>

<script>
import {api} from "@/api/RegularIncomeAPI";
import WaitingIndicator from "@/components/misc/WaitingIndicator.vue";
import TurnoverRowsTable from "@/components/turnovers/detail/TurnoverRowsTable.vue";

export default {
    name: "TurnoversDetail",
    components: {TurnoverRowsTable, WaitingIndicator},
    props: {
        id: {
            type: String,
            required: true,
        }
    },
    data() {
        return {
            isLoading: false,
            turnoverImport: null,
        }
    },
    methods: {
        reload() {
            this.isLoading = true

            api.getTurnovers()
                .fetchTurnoverImport(this.id)
                .then(res => {
                    this.turnoverImport = res
                })
                .finally(() => this.isLoading = false)
        },
        onBack() {
            this.$router.back()
        },
        onSave() {
            console.log("save")
        },
    },
    computed: {
        importTimestamp() {
            return this.turnoverImport.importedAt;
        },
    },
    mounted() {
        this.reload();
    }
}
</script>

<style scoped>

</style>
