<script setup lang="ts">
import {TurnoverImport} from "@api/api.ts";
import {computed} from "vue";
import type {VDataTable} from "vuetify/components";
import ConfirmationedButton from "../misc/ConfirmationedButton.vue";
import {DateTime} from "luxon";
import {useRouter} from "vue-router";

const router = useRouter()

const {imports, isLoading} = defineProps<{
  imports: TurnoverImport[];
  isLoading?: boolean;
}>();

const emit = defineEmits<{
  (e: 'onDelete', item: TurnoverImport): void;
}>();

function formatTimestamp(d?: string) {
  if (!d)
    return null;

  return DateTime.fromISO(d).toLocaleString(DateTime.DATETIME_MED);
}

function formatDate(d?: string) {
  if (!d)
    return null;

  return DateTime.fromISO(d).toLocaleString(DateTime.DATE_FULL);
}

function onOpen(item: TurnoverImport) {
   router.push({name: "turnovers-detail", params: {id: item.id}});
}

function onDelete(item: TurnoverImport) {
   emit("onDelete", item);
}

const sortedList = computed(() => {
  return imports;
})

// see https://stackoverflow.com/a/75993081
type ReadonlyHeaders = VDataTable['$props']['headers']
type UnwrapReadonlyArray<A> = A extends Readonly<Array<infer I>> ? I : never;
type ReadonlyDataTableHeader = UnwrapReadonlyArray<ReadonlyHeaders>;

const fields = computed<ReadonlyDataTableHeader[]>((): ReadonlyDataTableHeader[] => {
  return [
    {
      key: "importedAt",
      title: "Importiert",
      sortable: true,
      value: it => formatTimestamp(it.importedAt),
    },
    {
      key: 'firstTurnover',
      title: "Von",
      sortable: true,
      value: it => formatDate(it.firstTurnover),
    },
    {
      key: 'lastTurnover',
      title: "Bis",
      sortable: true,
      value: it => formatDate(it.lastTurnover),
    },
    {
      key: "turnovers",
      title: "#Umsätze",
      align: "end",
      sortable: true,
      // sortByFormatted: true,
      value: it => it.turnovers.length,
      // formatter: (value) => value.length,
    },
    {
      key: "actions",
      title: ""
    }
  ];
})
</script>

<template>
  <div>
    <v-data-table :loading="isLoading"
                  :items="sortedList"
                  :headers="fields"
                  :hide-default-footer="true">
      <template v-slot:item.actions="{item}">
        <div class="action-buttons">
          <v-btn color="primary"
                 :disabled="isLoading"
                 :small="true"
                 @click="() => onOpen(item)">
            Öffnen
          </v-btn>

          <confirmationed-button @click="() => onDelete(item)"
                                 :disabled="isLoading"
                                 default-caption="Löschen"
                                 request-caption="Löschen??"
                                 confirmed-caption="Löschen!!!"
                                 default-color="secondary"
                                 :small="true"/>
        </div>
      </template>
    </v-data-table>
  </div>
</template>

<style scoped>

</style>