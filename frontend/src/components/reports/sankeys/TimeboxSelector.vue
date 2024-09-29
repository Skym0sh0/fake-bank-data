<script setup lang="ts">

import {DateTime} from "luxon";
import {computed} from "vue";
import {AssociationsType, SelectType} from "./types.ts";
import {ReportTimeUnits} from "@api/api.ts";

const {value, earliest, latest, maxDepth} = defineProps<{
  value: SelectType;
  earliest: DateTime;
  latest: DateTime;
  maxDepth: number;
}>()

const emit = defineEmits<{
  (e: "input"): void;
}>()

const earliestDate = computed(() => {
  return earliest.toFormat("YYYY-MM-DD");
})

const latestDate = computed(() => {
  return /*latest*/ DateTime.now().toFormat("YYYY-MM-DD");
})

const associations = computed<string[]>(() => {
  return Object.keys(AssociationsType)
      .map(k => ({
        value: AssociationsType[k],
        title: AssociationsType[k],
      }))
})

const isAbsolute = computed(() => {
  return value.mode === AssociationsType.Absolute;
})

const isRelative = computed(() => {
  return value.mode === AssociationsType.Relative;
})

const timeunits = computed(() => {
  return Object.keys(ReportTimeUnits)
      .map(key => ({
        value: ReportTimeUnits[key],
        title: key,
      }))
})

const depths = computed(() => {
  return Array(maxDepth)
      .fill(null)
      .map((x, idx) => idx + 1);
})

const years = computed(() => {
  const earl = earliest.year;
  const late = latest.year;

  return Array(late - earl + 1)
      .fill(null)
      .map((x, idx) => earl + idx)
      .reverse();
})

const months = computed(() => {
  return Array(12)
      .fill(null)
      .map((_, idx) => idx + 1)
      .map(m => ({
        value: m,
        title: DateTime.now()
            .setLocale("DE")
            .set({month: m})
            .toFormat("LLLL")
      }))
})

const yearRules = computed(() => {
  const earl = earliest.year;
  const late = latest.year;

  return [
    v => (v === null || (earl <= v && v <= late)) || `Jahr muss zwischen ${earl} und ${late} liegen`
  ];
})

const monthRules = computed(() => {
  return [
    v => (v === null || (0 < v && v <= 12)) || "Monat muss zwischen 1 und 12 liegen"
  ];
})

const depthRules = computed(() => {
  return [
    v => !!v || "Tiefe ist nötig",
    v => (v && v > 0) || "Tiefe muss positiv sein",
    v => (v && v <= maxDepth) || `Tiefe muss kleiner oder gleich ${maxDepth} sein`,
  ];
})

const unitsRules = computed(() => {
  return [
    v => !!v || "Muss vorhanden sein",
    v => v > 0 || "Muss positiv sein",
  ];
})

function decreaseYear() {
  value.year = Math.max(earliest.year, value.year - 1)
}

function increaseYear() {
  value.year = Math.min(value.year + 1, latest.year)
}

function decreaseMonth() {
  value.month = 1 + (value.month - 1 - 1 + months.value.length) % months.value.length
}

function increaseMonth() {
  value.month = 1 + (value.month - 1 + 1 + months.value.length) % months.value.length
}
</script>

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
                  :placeholder="`${latest.year}`">
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
          <template v-slot:append>
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
                  :placeholder="`${latest.month}`">
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
          <template v-slot:append>
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
                      type="number"
                      :rules="unitsRules"/>
      </v-col>

      <v-col :cols="2" class="d-flex justify-content-center align-items-baseline">
        <div class="w-100">
          <v-date-input label="Referenzdatum"
                        placeholder="Referenzdatum"
                        :value="value.referenceDate.toJSDate()"
                        @update:modelValue="val => value.referenceDate = DateTime.fromJSDate(val)"
                        :prepend-icon="null"
                        :first-day-of-week="1"
                        :min="earliestDate"
                        :max="latestDate"/>
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
