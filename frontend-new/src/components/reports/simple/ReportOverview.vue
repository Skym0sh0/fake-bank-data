<script setup lang="ts">

import {computed, ref, shallowRef} from "vue";
import type {Component} from "vue";
import BalanceProgressionReport from "./BalanceProgressionReport.vue";

type ChartTemplate = {
  id: string;
  title: string;
  component: Component;
}

const chartHeight = ref(800)
const openCharts = ref<string[]>([])
const charts = shallowRef<ChartTemplate[]>([
  {
    title: 'Kontostand Entwicklung',
    component: BalanceProgressionReport,
  }
].map((c, idx) => ({...c, id: idx.toString()})))

const areAllChartsOpen = computed(() => {
  return openCharts.value.length == charts.value.length
})
const areNoChartsOpen = computed(() => {
  return openCharts.value.length === 0
})

function showAll() {
  hideAll()
  openCharts.value.push(...charts.value.map(c => c.id))
}

function hideAll() {
  openCharts.value = []
}
</script>

<template>
  <v-container fluid>
    <v-row justify="end">
      <v-col :cols="3">
        <v-item-group>
          <v-item>
            <v-btn :disabled="areAllChartsOpen"
                   color="secondary"
                   @click="showAll()" x-small>
              Show all
            </v-btn>
          </v-item>
          <v-item>
            <v-btn :disabled="areNoChartsOpen"
                   color="accent"
                   @click="hideAll()" x-small>
              Show none
            </v-btn>
          </v-item>
        </v-item-group>
      </v-col>
    </v-row>

    <v-row>
      <v-col>
        <v-expansion-panels id="report-overview-chart-panels"
                            v-model="openCharts"
                            focusable multiple
                            class="pa-2">
          <v-expansion-panel v-for="graph in charts"
                             :key="graph.id"
                             :id="`report-overview-graph-panel-${graph.id}`"
                             class="ma-2">

            <v-expansion-panel-title :id="`report-overview-graph-panel-header-${graph.id}`">
              {{ graph.title }}
            </v-expansion-panel-title>

            <v-expansion-panel-text :id="`report-overview-graph-panel-content-${graph.id}`" eager>
              <keep-alive>
                <component :id="`report-overview-graph-${graph.id}`"
                           :is="graph.component"
                           :height="chartHeight"/>
              </keep-alive>
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>
      </v-col>
    </v-row>
  </v-container>
</template>
