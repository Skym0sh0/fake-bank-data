<template>
    <div :id="id">
        <b-form-input :id="`${id}-form-input`"
                      v-model="rawInput"
                      :disabled="disabled"
                      :size="size"
                      :placeholder="placeholder"
                      :state="state"
                      @focus="onFocus()"
                      @blur="onFocusLost()"/>

        <div v-if="false" class="debug">
            <div>Id: {{id}}</div>
            <div>RawInput: {{rawInput}}</div>
            <div>Value: {{value}}</div>
            <div>InCents: {{amountInCents()}}</div>
            <div>Frmted: {{amountFormatted()}}</div>
            <div>Decimal: {{amountInDecimal()}}</div>
        </div>
    </div>
</template>

<script>
    import {moneyFormat} from "../../util/Formatters";

    export default {
        name: "MonetaryInput",
        props: {
            id: {
                required: true,
                type: String,
            },
            value: {
                required: true,
                // type: Number,
            },
            state: {
                required: false,
            },
            disabled: {
                required: false,
                type: Boolean,
                default() {
                    return false
                }
            },
            placeholder: {
                required: false,
                type: String,
            },
            size: {
                required: false,
            },
        },
        data() {
            return {
                rawInput: null,
                isFocused: false,
            }
        },
        watch: {
            value() {
                this.rawInput = this.forRawInput()
            },
            isFocused() {
                this.rawInput = this.forRawInput()
            },
        },
        methods: {
            amountInCents() {
                return this.value
            },
            amountInDecimal() {
                return this.amountInCents() / 100.0
            },
            amountFormatted() {
                return this.formatCentsToMoney(this.amountInDecimal())
            },
            forRawInput() {
                if (!this.value && this.value !== 0)
                    return null

                if (!this.isFocused)
                    return this.amountFormatted()

                return this.amountInDecimal().toFixed(2)
            },
            formatCentsToMoney(value) {
                return moneyFormat.formatCents(value * 100)
            },
            onFocus() {
                this.isFocused = true

                this.rawInput = this.forRawInput()
            },
            onFocusLost() {
                this.isFocused = false

                if (!this.rawInput && this.rawInput !== 0)
                    return;

                const parser = str => {
                    return parseFloat(str.replace(',', '.'))
                }

                const newCents = Math.round(parser(this.rawInput) * 100)
                this.$emit('input', newCents)
            },
        },
        mounted() {
            this.rawInput = this.forRawInput()
        },
    }
</script>

<style scoped>
    .debug {
        background-color: #999999;
        color: lightblue;
    }
</style>