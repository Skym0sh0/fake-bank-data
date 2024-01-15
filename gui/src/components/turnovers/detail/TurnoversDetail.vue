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
                <turnover-rows-table v-if="categories"
                                     :rows="turnoverImport.turnovers"
                                     :touchedRowsIdsById="currentRowCategoryChangesById"
                                     :categories="categories"
                                     @onCreateCategory="onCreateCategory"/>
            </v-card-text>

            <v-card-actions class="d-flex justify-content-end">
                <v-btn @click="onBack">
                    Zurück
                </v-btn>
                <v-btn @click="onSave"
                       :disabled="!isValidToSave"
                       color="info">
                    Speichern
                </v-btn>
            </v-card-actions>
        </v-card>
    </div>
</template>

<script>
import {api} from "@/api/RegularIncomeAPI";
import WaitingIndicator from "@/components/misc/WaitingIndicator.vue";
import TurnoverRowsTable from "@/components/turnovers/detail/TurnoverRowsTable.vue";
import {normalizeCategory} from "@/util/Normalizer";

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
            categories: null,

            initialTurnoverRowsCategories: [],
        }
    },
    methods: {
        reload() {
            this.isLoading = true

            Promise.all([this.loadImport(), this.loadCategories()])
                .finally(() => this.isLoading = false)
        },
        loadImport() {
            return api.getTurnovers()
                .fetchTurnoverImport(this.id)
                .then(res => {
                    this.turnoverImport = res
                    this.initialTurnoverRowsCategories = this.extractTurnoverRowsWithCategories(res)
                })
        },
        loadCategories() {
            return api.getCategories()
                .fetchCategoryTree()
                .then(res => {
                    this.categories = res
                })
        },
        onCreateCategory(categoryName) {
            const normalized = normalizeCategory({
                name: categoryName,
            });

            this.isLoading = true
            api.getCategories()
                .postCategory(normalized)
                .then(() => this.loadCategories())
                .finally(() => this.isLoading = false)
        },
        onBack() {
            this.$router.back()
        },
        onSave() {
            const changes = {rows: this.currentRowCategoryChanges};

            this.isLoading = true
            api.getTurnovers()
                .patchTurnovers(this.id, changes)
                .then(() => this.loadImport())
                .finally(() => this.isLoading = false)
        },
        extractTurnoverRowsWithCategories(turnoverImport) {
            if (!turnoverImport || !turnoverImport.turnovers)
                return [];

            return turnoverImport.turnovers.map(row => ({
                id: row.id,
                categoryId: row.categoryId,
            }))
        },
    },
    computed: {
        importTimestamp() {
            return this.turnoverImport.importedAt;
        },
        currentRowCategoryChanges() {
            const rowsById = this.initialTurnoverRowsCategories.reduce((prev, cur) => ({...prev, [cur.id]: cur}), {})

            return this.extractTurnoverRowsWithCategories(this.turnoverImport)
                .filter(row => rowsById[row.id].categoryId !== row.categoryId);
        },
        currentRowCategoryChangesById() {
            return this.currentRowCategoryChanges.reduce((prev, cur) => ({...prev, [cur.id]: cur}), {})
        },
        isValidToSave() {
            return this.currentRowCategoryChanges.length > 0 && this.currentRowCategoryChanges.every(row => !!row.categoryId)
        },
    },
    mounted() {
        this.reload()
    }
}
</script>

<style scoped>

</style>
