<template>
    <b-card title="Turnovers" sub-title="Overview">
        <b-card-body>
            <div class="action-buttons">
                <general-file-upload @uploadSucceeded="uploadSuccess"/>
            </div>

            <turnovers-list :imports="turnoverImports"/>
        </b-card-body>
    </b-card>
</template>

<script>
import TurnoversList from './TurnoversList';
import GeneralFileUpload from "@/components/turnovers/GeneralFileUpload.vue";
import {api} from "@/api/RegularIncomeAPI";

export default {
    name: "TurnoverOverview",
    data() {
        return {
            turnoverImports: [],
        };
    },
    components: {
        GeneralFileUpload,
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
