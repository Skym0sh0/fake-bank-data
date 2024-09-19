<script setup lang="ts">
import {Category} from "@api/api.ts";
import {DateTime} from "luxon";

const {entity} = defineProps<{
  entity: Category;
}>()

function formatDate(date?: string) {
  if (!date)
    return null;

  return DateTime.fromISO(date).toLocaleString(DateTime.DATETIME_SHORT_WITH_SECONDS);
}
</script>

<template>
  <div class="category-infos">
    <div v-show="entity.updatedAt" class="category-info">
      <div>Zuletzt geändert</div>
      <div>{{ formatDate(entity.updatedAt) }}</div>
    </div>
    <div v-show="entity.createdAt" class="category-info">
      <div>Erstellt am</div>
      <div>{{ formatDate(entity.createdAt) }}</div>
    </div>
    <div v-show="entity.usageCount" class="category-info">
      <div>Benutzt in</div>
      <div>{{ entity.usageCount }}</div>
    </div>
  </div>
</template>

<style scoped>
.category-infos {
  font-size: 10px;
  display: flex;
  flex-direction: column;
}

.category-infos > * {
  margin: 0;
}

.category-info {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
</style>
