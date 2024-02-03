<template>
    <b-card>
        <waiting-indicator :is-loading="isLoading"/>

        <b-card-header>
            <div class="d-flex justify-content-between align-items-center">
                <h4>
                    Überblick Umsätze
                </h4>

                <b-btn-group>
                    <b-btn variant="secondary" @click="onReload">
                        Neuladen
                    </b-btn>

                    <turnover-importing @uploadSucceeded="uploadSuccess"/>
                </b-btn-group>
            </div>
        </b-card-header>

        <b-card-body>
            <turnovers-list :imports="turnoverImports"
                            @onDelete="onDelete"/>
        </b-card-body>
    </b-card>
</template>

<script>
import TurnoversList from './TurnoversList';
import TurnoverImporting from "@/components/turnovers/importing/TurnoverImporting.vue";
import {api} from "@/api/RegularIncomeAPI";
import WaitingIndicator from "@/components/misc/WaitingIndicator.vue";

export default {
    name: "TurnoverOverview",
    data() {
        return {
            isLoading: false,
            turnoverImports: [],
        };
    },
    components: {
        WaitingIndicator,
        TurnoverImporting,
        TurnoversList,
    },
    methods: {
        loadImports() {
            this.isLoading = true;

            api.getTurnovers()
                .fetchTurnoverImports()
                .then(imports => this.turnoverImports = imports)
                .finally(() => this.isLoading = false)
        },
        onDelete(fileImport) {
            this.isLoading = true;

            api.getTurnovers()
                .deleteTurnoverImport(fileImport)
                .then(() => this.loadImports())
                .finally(() => this.isLoading = false)
        },
        uploadSuccess() {
            this.loadImports()
        },
        onReload() {
            this.loadImports()
        },
    },
    mounted() {
        this.loadImports()
    }
}
</script>

<style scoped>
.action-buttons {
    display: flex;
    justify-content: flex-end;
}
</style>
