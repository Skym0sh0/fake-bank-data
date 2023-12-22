<template>
    <b-card title="Turnovers" sub-title="Overview">
        <b-card-body>
            <div class="action-buttons">
                <general-file-upload @uploadSucceeded="uploadSuccess"/>
            </div>

            <turnovers-list/>
        </b-card-body>
    </b-card>
</template>

<script>
import TurnoversList from './TurnoversList';
import GeneralFileUpload from "@/components/turnovers/GeneralFileUpload.vue";

export default {
    name: "TurnoverOverview",
    components: {
        GeneralFileUpload,
        TurnoversList,
    },
    methods: {
        uploadSuccess() {
            console.log("Upload success")
        },
    }
}
</script>

<style scoped>
.action-buttons {
    display: flex;
    justify-content: flex-end;
}
</style>
