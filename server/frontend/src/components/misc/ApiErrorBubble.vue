<template>
    <v-snackbar :value="hasError"
                :multi-line="true"
                color="red"
                :timeout="-1">
        <template v-slot:action>
            <v-btn
                    variant="text"
                    @click="resetError">
                Close
            </v-btn>
        </template>

        <template v-slot:default>
            <h5> {{ error }} - {{ status }} </h5>
            <h6> {{ target }}</h6>

            <div>
                {{ details }}
            </div>

            <div style="font-size: 0.675em">
                {{ timestamp }}
            </div>
        </template>
    </v-snackbar>
</template>


<script>
import moment from "moment";

export default {
    name: "ApiErrorBubble",
    computed: {
        hasError() {
            return !!this.$root.errorRef.lastError
        },
        timestamp() {
            if (!this.hasError)
                return null;

            return moment(this.$root.errorRef.lastError.timestamp).format("YYYY-MM-DD HH:mm:ss.SSS");
        },
        status() {
            if (!this.hasError)
                return null;

            return this.$root.errorRef.lastError.status;
        },
        target() {
            if (!this.hasError)
                return null;

            return this.$root.errorRef.lastError.path;
        },
        error() {
            if (!this.hasError)
                return null;

            return this.$root.errorRef.lastError.error;
        },
        details() {
            if (!this.hasError)
                return null;

            return this.$root.errorRef.lastError.errorDetails;
        },
    },
    methods: {
        resetError() {
            this.$root.errorRef.lastError = null
        },
    },
}
</script>

<style scoped>

</style>
