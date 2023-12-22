<template>
    <b-card title="Turnovers" sub-title="Overview">
        <b-card-body>
            <div class="action-buttons">
                <turnover-importing @uploadSucceeded="uploadSuccess"/>
            </div>

            <turnovers-list :imports="turnoverImports"/>
        </b-card-body>
    </b-card>
</template>

<script>
import TurnoversList from './TurnoversList';
import TurnoverImporting from "@/components/turnovers/TurnoverImporting.vue";
import {api} from "@/api/RegularIncomeAPI";

export default {
    name: "TurnoverOverview",
    data() {
        return {
            turnoverImports: [],
        };
    },
    components: {
        TurnoverImporting,
        TurnoversList,
    },
    methods: {
        loadImports() {
            api.getTurnovers()
                .fetchTurnoverImports()
                .then(imports => {
                    this.turnoverImports = imports;
                })
        },
        uploadSuccess() {
            console.log("Upload success")
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
