<template>
    <v-row>
        <v-col :cols="2">
            <v-select v-model="value.mode"
                      :items="associations"
                      label="Bezug"
                      :placeholder="`Relativ`"/>
        </v-col>

        <template v-if="isAbsolute">
            <v-col :cols="4">
                <v-select v-model="value.year"
                          :items="years"
                          :rules="yearRules"
                          label="Jahr"
                          :clearable="true"
                          :placeholder="`${latest.year()}`">
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

            <v-col :cols="4">
                <v-select v-model="value.month"
                          :items="months"
                          :rules="monthRules"
                          label="Monat"
                          :clearable="true"
                          :placeholder="`${latest.month()}`">
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
        </template>

        <template v-if="isRelative">
            <v-col :cols="3">
                <v-select v-model="value.timeunit"
                          :items="timeunits"
                          label="Zeiteinheit"
                          :placeholder="`Jahr`"/>
            </v-col>

            <v-col :cols="3" class="d-flex align-items-end">
                <v-text-field v-model="value.units"
                              label="Einheiten"
                              type="number"/>
            </v-col>

            <v-col :cols="2" class="d-flex justify-content-center align-items-baseline">
                <div class="w-100">
                    <label for="reference-date-datepicker" class="m-0" style="font-size: 12px">
                        Referenzdatum
                    </label>
                    <b-form-datepicker id="reference-date-datepicker"
                                       placeholder="Referenzdatum"
                                       v-model="value.referenceDate"
                                       :min="earliestDate"
                                       :max="latestDate"
                                       :start-weekday="1"
                                       :date-format-options="{ year: 'numeric', month: 'numeric', day: 'numeric' }"
                                       size="sm"/>
                </div>
            </v-col>
        </template>

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
import {ABSOLUTE, ASSOCIATIONS, RELATIVE} from "@/util/association";
import moment from "moment/moment";

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
        earliestDate() {
            return this.earliest.format("YYYY-MM-DD");
        },
        latestDate() {
            return /*this.latest*/ moment().format("YYYY-MM-DD");
        },
        associations() {
            return ASSOCIATIONS
        },
        isAbsolute() {
            return this.value.mode === ABSOLUTE;
        },
        isRelative() {
            return this.value.mode === RELATIVE;
        },
        timeunits() {
            return [
                {
                    value: "DECADES",
                    text: "Dekaden",
                },
                {
                    value: "YEARS",
                    text: "Jahre",
                },
                {
                    value: "MONTHS",
                    text: "Monate",
                },
                {
                    value: "WEEKS",
                    text: "Wochen",
                },
                {
                    value: "DAYS",
                    text: "Tage",
                }
            ];
        },
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