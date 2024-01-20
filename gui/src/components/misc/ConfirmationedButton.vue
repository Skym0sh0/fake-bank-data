<template>
    <v-btn :color="color"
           @click="onClick"
           :loading="isLoading"
           v-bind="$attrs">
        {{ caption }}
    </v-btn>
</template>

<script>

export default {
    name: "ConfirmationedButton",
    props: {
        defaultColor: {
            type: String,
            default() {
                return "info";
            },
        },
        defaultCaption: {
            type: String,
            required: true,
        },
        requestCaption: {
            type: String,
            required: true,
        },
        confirmedCaption: {
            type: String,
            required: true,
        },
        waitTimeMs: {
            type: Number,
            default() {
                return 1500
            },
        }
    },
    data() {
        return {
            isLoading: false,

            actionRequested: false,
            actionConfirmed: false,
        }
    },
    methods: {
        onClick() {
            const defer = (work) => {
                this.isLoading = true;
                setTimeout(() => {
                    work()
                    this.isLoading = false;
                }, this.waitTimeMs)
            };

            if (!this.actionRequested) {
                defer(() => this.actionRequested = true)
                return;
            }

            if (!this.actionConfirmed) {
                defer(() => this.actionConfirmed = true)
                return;
            }

            this.$emit("click")
        },
    },
    computed: {
        caption() {
            if (!this.actionRequested)
                return this.defaultCaption

            if (!this.actionConfirmed)
                return this.requestCaption

            return this.confirmedCaption
        },
        color() {
            if (!this.actionRequested)
                return this.defaultColor;

            if (!this.actionConfirmed)
                return "warning"

            return "error";
        },
    },
}
</script>

<style scoped>

</style>
