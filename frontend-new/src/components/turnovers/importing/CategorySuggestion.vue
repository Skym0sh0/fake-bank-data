<script setup lang="ts">
import {CategoriesById} from "../../misc/categoryHelpers.ts";
import {computed} from "vue";
import * as _ from "lodash";
import {CategorySuggestion} from "@api/api.ts";

const {checksum, disabled = false, suggestions, categoriesById} = defineProps<{
  checksum: string;
  disabled?: boolean;
  suggestions: CategorySuggestion[];
  categoriesById: CategoriesById;
}>();

export type CategorySelectionType = {
  checksum: string;
  categoryId: string;
};

const emit = defineEmits<{
  (e: 'select', item: CategorySelectionType): void;
}>();

const enrichedSuggestions = computed(() => {
  const originMap = {
    amount_value_cents: "Betrag",
    suggested_category: "Bankvorschlag",
    recipient: "Empfänger/Sender",
    description: "Beschreibung",
  };
  const colors = [
    "success",
    "primary",
    "dark",
    "secondary",
    "warning",
    "danger",
    "info",
    "light",
  ];

  return _.orderBy(suggestions, [s => s.precedence], ['desc'])
    .map((s, idx) => ({
      id: s.categoryId,
      label: categoriesById[s.categoryId].name,
      frequency: s.frequency,
      precedence: s.precedence,
      origins: `Hergeleitet aus ${s.origins.map(o => originMap[o] || o).join(" & ")}`,
      color: colors[idx % colors.length],
    }))
})

function onClick(categoryId: string) {
  emit("select", {checksum: checksum, categoryId: categoryId})
}
</script>

<template>
  <div class="d-flex justify-content-start flex-wrap align-content-start" style="max-width: 35vh">
    <template v-for="s in enrichedSuggestions" :key="s.id">

      <v-btn :id="`index-${checksum}-suggestion-${s.id}`"
             @click="onClick(s.id)"
             :disabled="disabled"
             :color="s.color"
             size="sm"
             style="font-size: 0.75em; padding: 0 2px; margin: 0.1em">
        {{ s.label }} - {{ s.frequency }}

        <v-tooltip activator="parent">
          <table>
            <tbody>
            <tr>
              <th>Kategorie</th>
              <td style="float: right">{{ s.label }}</td>
            </tr>
            <tr>
              <th>Häufigkeit</th>
              <td style="float: right">{{ s.frequency }}</td>
            </tr>
            </tbody>
          </table>
          <div>{{ s.origins }}</div>
        </v-tooltip>
      </v-btn>
    </template>
  </div>
</template>
