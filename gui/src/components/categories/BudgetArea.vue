<template>
    <div>
        <div class="d-flex justify-content-between align-items-center p-1">
            <div>Ausgaben Budget</div>

            <v-btn size="small"
                   :icon="true"
                   @click="onHeaderClick">
                <v-icon>
                    {{ isBudgetAbsent ? 'mdi-plus' : 'mdi-minus' }}
                </v-icon>
            </v-btn>
        </div>

        <div v-if="!isBudgetAbsent">
            <v-text-field label="Monatliches Budget"
                          suffix="€"
                          type="number"
                          placeholder="1000"
                          v-model="value.budget"
                          :rules="[
                                v => !!v || 'Muss vorhanden sein',
                                v => Number.parseInt(v) > 0 || 'Muss positiv sein'
                              ]"
                          @input="changeAnything"/>

            <v-slider :min="0" :max="100"
                      color="orange"
                      :disabled="!this.budget"
                      :thumb-label="true"
                      v-model="value.exceedingThresholdPercent"
                      @input="changeAnything"
                      label="Warnung"
                      messages="msg">
                <template v-slot:message>
                    <div class="d-flex justify-content-between">
                        <p> {{ fractionMessage }}</p>
                        <p> {{ rangeMessage }}</p>
                    </div>
                </template>
                <template v-slot:thumb-label="{value}">
                    {{ value }} %
                </template>
            </v-slider>
        </div>
    </div>
</template>

<script>

export default {
    name: "BudgetArea",
    props: {
        value: {
            type: Object,
            // required: true,
        },
    },
    methods: {
        onHeaderClick() {
            if (this.isBudgetAbsent)
                this.onNewBudget();
            else
                this.onRemoveBudget();
            this.changeAnything()
        },
        onNewBudget() {
            console.assert(this.isBudgetAbsent, "Budget must not exist")
            this.$emit("newBudget");
        },
        onRemoveBudget() {
            console.assert(!this.isBudgetAbsent, "Budget must exist")
            this.$emit("deleteBudget");
        },
        changeAnything() {
            this.$emit("changed")
        },
        formatMonetary(value) {
            if (!value && value !== 0)
                return null;

            return `${Math.round(value)} €`;
        },
    },
    computed: {
        isBudgetAbsent() {
            return !this.value;
        },
        budget() {
            return Math.round(Number.parseFloat(this.value.budget) * 100) / 100.0;
        },
        budgetFormatted() {
            return this.formatMonetary(this.budget)
        },
        exceedingThreshold() {
            return this.value.exceedingThresholdPercent / 100.0;
        },
        budgetThresholdFraction() {
            return this.budget * this.exceedingThreshold;
        },
        budgetThresholdFractionFormatted() {
            return this.formatMonetary(this.budgetThresholdFraction)
        },
        thresholdRange() {
            return [
                this.budget - this.budgetThresholdFraction,
                this.budget + this.budgetThresholdFraction
            ];
        },
        thresholdRangeFormatted() {
            return this.thresholdRange.map(v => this.formatMonetary(v))
        },
        fractionMessage() {
            return `${this.value.exceedingThresholdPercent}% von ${this.budgetFormatted || '?'} = ${this.budgetThresholdFractionFormatted || '?'}`;
        },
        rangeMessage() {
            if (!this.budget)
                return null

            return `Bereich ${this.thresholdRangeFormatted[0]} - ${this.thresholdRangeFormatted[1]}`;
        }
    },
}
</script>

<style scoped>

</style>
