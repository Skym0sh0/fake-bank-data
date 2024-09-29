<script setup lang="ts">
import {CategoriesById} from "../../misc/categoryHelpers.ts";
import {computed} from "vue";
import * as _ from "lodash";
import {CategorySuggestion} from "@api/api.ts";

const {checksum, disabled = false, suggestions, categoriesById, selectedCategoryId} = defineProps<{
  checksum: string;
  disabled?: boolean;
  selectedCategoryId?: string;
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
  const originMap: Record<string, string> = {
    amount_value_cents: "Betrag",
    suggested_category: "Bankvorschlag",
    recipient: "Empfänger/Sender",
    description: "Beschreibung",
  };
  const colors = [
    "success",
    "primary",
    // "dark",
    "secondary",
    "warning",
    // "danger",
    "info",
    //  "light",
  ];

  return _.orderBy(suggestions, [s => s.precedence], ['desc'])
    .map((s, idx) => ({
      id: s.categoryId,
      label: categoriesById[s.categoryId].name,
      frequency: s.frequency,
      precedence: s.precedence,
      origins: `Hergeleitet aus ${(s.origins ?? [])
        .map(o => originMap[o] ?? o)
        .map(o => `"${o}"`)
        .join(" & ")}`,
      color: colors[idx % colors.length],
    }))
})

function onClick(categoryId: string) {
  emit("select", {checksum: checksum, categoryId: categoryId})
}
</script>

<template>
  <div class="d-flex justify-start flex-wrap align-start" style="max-width: 35vh; gap: 0.1em">
    <v-tooltip v-for="s in enrichedSuggestions" :key="s.id">
      <template v-slot:activator="{ props }">
        <v-btn :id="`index-${checksum}-suggestion-${s.id}`"
               v-bind="props"
               @click="onClick(s.id)"
               :disabled="disabled"
               :color="s.color"
               size="x-small"
               :variant="s.id === selectedCategoryId ? 'elevated' : 'outlined'"
               :text="`${s.label} - ${s.frequency}`"
               style="font-size: 0.75em; padding: 0 2px"
        />
      </template>

      <table class="w-100">
        <tbody>
        <tr>
          <th>Kategorie</th>
          <td>{{ s.label }}</td>
        </tr>
        <tr>
          <th>Häufigkeit</th>
          <td>{{ s.frequency }}</td>
        </tr>
        </tbody>
      </table>
      <div>{{ s.origins }}</div>
    </v-tooltip>
  </div>
</template>
