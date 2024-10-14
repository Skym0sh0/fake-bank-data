<script setup lang="ts">

import {AssociationsType, SelectType} from "./types.ts";
import {DateTime} from "luxon";
import Waiter from "../../misc/Waiter.vue";
import {computed, inject, nextTick, onBeforeUnmount, ref, useTemplateRef, watch} from "vue";
import {FlowDataPoint, IncomeExpenseFlowReport} from "@api/api.ts";
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import {SankeyDiagram} from "@amcharts/amcharts4/charts";
import {notifierRefKey} from "../../../keys.ts";
import {AxiosResponse} from "axios";
import {useApi} from "../../../store/use-api.ts";

const api = useApi()
const notifierRef = inject(notifierRefKey);

const {select, height} = defineProps<{
  select: SelectType;
  height: number;
}>()

const width = ref<number | null>(null);

const target = useTemplateRef<HTMLDivElement>("sankey-report-chart-target-div");

const chartRef = ref<SankeyDiagram | null>(null);
const isLoading = ref(false);
const sankeyData = ref<IncomeExpenseFlowReport | null>(null);

const incomeExpensesSankey = computed<FlowDataPoint[]>(() => {
  if (!sankeyData.value)
    return [];

  return (sankeyData.value.flows || [])
    .filter(dp => dp.depthLevel < (select.depth ?? 0));
})

const rootCategories = computed(() => {
  const categories = new Set(incomeExpensesSankey.value.flatMap(dp => [dp.fromCategory, dp.toCategory]))

  const permutate = (candidate: string) => {
    while (categories.has(candidate))
      candidate = "_" + candidate;
    return candidate;
  }

  return {
    income: permutate("Einkommen"),
    expense: permutate("Ausgaben"),
  }
})

type SankeyDataPoint = {
  from: string;
  to: string;
  amount: number;
}

const processedData = computed<SankeyDataPoint[]>(() => {
  const totalIncome = incomeExpensesSankey.value.filter(dp => dp.fromCategory && !dp.toCategory)
    .reduce((prev, cur) => prev + cur.amountInCents, 0);

  const connectionIncomeExpense = {
    from: rootCategories.value.income,
    to: rootCategories.value.expense,
    amount: totalIncome / 100.0,
  };

  return [
    connectionIncomeExpense,
    ...incomeExpensesSankey.value.map(dp => {
      return {
        from: dp.fromCategory || rootCategories.value.expense,
        to: dp.toCategory || rootCategories.value.income,
        amount: Math.abs(dp.amountInCents) / 100.0,
      };
    })
  ];

})

const getWidth = computed(() => {
  if (!width.value)
    return '100%'

  return `${width.value}px`
})

const getHeight = computed(() => {
  return `${height}px`
})

const isReportPresent = computed(() => {
  return incomeExpensesSankey.value.length > 0
})

const isAbsolutTimespan = computed(() => {
  return select.mode === AssociationsType.Absolute
})

const isRelativeTimespan = computed(() => {
  return select.mode === AssociationsType.Relative
})

const helpText = computed(() => {
  const prefix = "Alle Ein- und Ausgaben"
  const levels = `aufgefächert auf bis zu ${select.depth} Ebene(n).`;

  if (isAbsolutTimespan.value) {
    if (select.year && select.month)
      return `${prefix} für ${DateTime.local(select.year, select.month).setLocale("DE").toFormat("LLLL yyyy")} ${levels}`;
    if (select.year)
      return `${prefix} für ${select.year} ${levels}`;
  }
  if (isRelativeTimespan.value) {
    return `${prefix} aus ${select.units} ${select.timeunit} normiert auf einzelne ${select.timeunit} ${levels}`;
  }

  return `${prefix} ${levels}`
})

const timespanText = computed(() => {
  if (!sankeyData.value)
    return "";

  return `Von ${sankeyData.value.start} bis ${sankeyData.value.end}`;
})

function draw() {
  chartRef.value?.dispose()

  if (!isReportPresent || !target.value)
    return;

  const chart = am4core.create(target.value, am4charts.SankeyDiagram)

  // chart.orientation = "vertical";

  chart.data = processedData.value
  chart.dataFields.fromName = "from";
  chart.dataFields.toName = "to";
  chart.dataFields.value = "amount";
  // chart.dataFields.color = "nodeColor";
  chart.sortBy = "value";

  chart.paddingRight = 30;

  const nodeTemplate = chart.nodes.template;
  // nodeTemplate.inert = true;
  // nodeTemplate.clickable = true;
  // nodeTemplate.draggable = true;
  // nodeTemplate.readerTitle = "Drag me!";
  // nodeTemplate.showSystemTooltip = true;
  // nodeTemplate.width = 50;
  // nodeTemplate.nameLabel.label
  nodeTemplate.nameLabel.label.hideOversized = true;
  // nodeTemplate.nameLabel.label.textAlign = "middle";

  const linkTemplate = chart.links.template;
  // linkTemplate.inert = true;
  // linkTemplate.tension = 0.5;
  // linkTemplate.controlPointDistance = 0.1;
  linkTemplate.colorMode = "toNode";

  chart.exporting.menu = new am4core.ExportMenu();

  chartRef.value = chart
}

function loadData() {
  if (select.depth < 0)
    return;

  const doApiCall: () => Promise<AxiosResponse<IncomeExpenseFlowReport>> | undefined = () => {
    if (isAbsolutTimespan.value)
      return api.reportsApi.fetchIncomeExpenseFlowReport(select.year, select.month)

    if (isRelativeTimespan.value) {
      if (!select.timeunit || select.units <= 0)
        return undefined;

      return api.reportsApi.fetchIncomeExpenseFlowSlidingWindowReport(select.timeunit, select.units, select.referenceDate.toISODate() ?? undefined)
    }

    throw new Error("Unknown Timespan type: " + select.mode);
  };

  isLoading.value = true

  doApiCall?.()
    ?.then(res => sankeyData.value = res.data)
    ?.catch(e => notifierRef?.notifyError("Report konnte nicht geladen werden", e))
    ?.finally(() => {
      isLoading.value = false

      nextTick(() => draw())
    })
}

function reload() {
  loadData()
}

watch(() => select, () => reload(), {deep: true})

onBeforeUnmount(() => {
  chartRef.value?.dispose()
})

loadData()
</script>

<template>
  <v-card title="Sankey">
    <template v-slot:subtitle class="d-flex justify-content-between">
      <span>{{ helpText }}</span>
      <span>{{ timespanText }}</span>
    </template>

    <template v-slot:text>
      <v-sheet :height="getHeight" :width="getWidth">
        <waiter :is-loading="isLoading">
          <div class="h-100 w-100">
            <div v-if="!isReportPresent" class="h-100 w-100 m-auto">
              Keine Daten vorhanden
            </div>
            <div v-else ref="sankey-report-chart-target-div" class="h-100 w-100"/>
          </div>
        </waiter>
      </v-sheet>
    </template>
  </v-card>
</template>
