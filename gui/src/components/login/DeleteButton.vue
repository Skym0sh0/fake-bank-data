<template>
    <v-btn color="error"
           @click="onDelete"
           :loading="deleteIsLoading">
        {{ deleteCaption }}
    </v-btn>
</template>

<script>
export default {
    name: "DeleteButton",
    data() {
        return {
            deleteIsLoading: false,

            deletionRequested: false,
            deletionConfirmed: false,
        }
    }, methods: {
        onDelete() {
            const defer = (work) => {
                this.deleteIsLoading = true;
                setTimeout(() => {
                    work()
                    this.deleteIsLoading = false;
                }, 1500)
            };

            if (!this.deletionRequested) {
                defer(() => this.deletionRequested = true)
                return;
            }

            if (!this.deletionConfirmed) {
                defer(() => this.deletionConfirmed = true)
                return;
            }

            this.$emit("delete")
        },
    },
    computed: {
        deleteCaption() {
            if (!this.deletionRequested)
                return "Account Löschen?"

            if (!this.deletionConfirmed)
                return "Account wirklich löschen?"

            return "Jetzt Löschen !"
        },
    },
}
</script>

<style scoped>

</style>
