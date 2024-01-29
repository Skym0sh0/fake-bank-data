<template>
    <v-row>
        <v-col :cols="5">
            <v-select v-model="value.year"
                      :items="years"
                      :rules="yearRules"
                      label="Jahr"
                      :clearable="true"
                      :placeholder="`${latest.year()}`"
            >
                <template v-slot:prepend>
                    <v-btn :icon="true"
                           :x-small="true"
                           :disabled="!value.year"
                           @click="decreaseYear">
                        <v-icon>
                            mdi-menu-left
                        </v-icon>
                    </v-btn>
                </template>
                <template v-slot:append-outer>
                    <v-btn :icon="true"
                           :x-small="true"
                           :disabled="!value.year"
                           @click="increaseYear">
                        <v-icon>
                            mdi-menu-right
                        </v-icon>
                    </v-btn>
                </template>
            </v-select>
        </v-col>

        <v-col :cols="5">
            <v-select v-model="value.month"
                      :items="months"
                      :rules="monthRules"
                      label="Monat"
                      :clearable="true"
                      :placeholder="`${latest.month()}`"
            >
                <template v-slot:prepend>
                    <v-btn :icon="true"
                           :x-small="true"
                           :disabled="!value.month"
                           @click="decreaseMonth">
                        <v-icon>
                            mdi-menu-left
                        </v-icon>
                    </v-btn>
                </template>
                <template v-slot:append-outer>
                    <v-btn :icon="true"
                           :x-small="true"
                           :disabled="!value.month"
                           @click="increaseMonth">
                        <v-icon>
                            mdi-menu-right
                        </v-icon>
                    </v-btn>
                </template>
            </v-select>
        </v-col>

        <v-col :cols="2">
            <v-select v-model="value.depth"
                      :items="depths"
                      :rules="depthRules"
                      label="Tiefe"
                      :clearable="false"
                      :placeholder="`${2}`"
            />
        </v-col>
    </v-row>
</template>

<script>

import {MonthIndexToName} from "@/util/months";

export default {
    name: "TimeboxSelector",
    props: {
        value: {
            type: Object,
            required: true,
        },
        earliest: {
            type: Object,
            required: true,
        },
        latest: {
            type: Object,
            required: true,
        },
        maxDepth: {
            type: Number,
            required: true,
        },
    },
    computed: {
        depths() {
            return Array(this.maxDepth)
                .fill(null)
                .map((x, idx) => idx + 1);
        },
        years() {
            const earliest = this.earliest.year();
            const latest = this.latest.year();

            return Array(latest - earliest + 1)
                .fill(null)
                .map((x, idx) => earliest + idx)
                .reverse();
        },
        months() {
            return [
                // {value: null, text: ""},
                ...Object.keys(MonthIndexToName)
                    .map(idx => ({
                        value: Number.parseInt(idx),
                        text: MonthIndexToName[idx],
                    })),
            ];
        },
        yearRules() {
            const earliest = this.earliest.year();
            const latest = this.latest.year();

            return [
                v => (v === null || (earliest <= v && v <= latest)) || `Jahr muss zwischen ${earliest} und ${latest} liegen`
            ];
        },
        monthRules() {
            return [
                v => (v === null || (0 < v && v <= 12)) || "Monat muss zwischen 1 und 12 liegen"
            ];
        },
        depthRules() {
            return [
                v => !!v || "Tiefe ist nötig",
                v => (v && v > 0) || "Tiefe muss positiv sein",
                v => (v && v <= this.maxDepth) || `Tiefe muss kleiner oder gleich ${this.maxDepth} sein`,
            ];
        },
    },
    methods: {
        decreaseYear() {
            this.value.year = Math.max(this.earliest.year(), this.value.year - 1)
        },
        increaseYear() {
            this.value.year = Math.min(this.value.year + 1, this.latest.year())
        },
        decreaseMonth() {
            this.value.month = 1 + (this.value.month - 1 - 1 + this.months.length) % this.months.length
        },
        increaseMonth() {
            this.value.month = 1 + (this.value.month - 1 + 1 + this.months.length) % this.months.length
        },
    },
    mounted() {
    },
}
</script>

<style scoped>

</style>