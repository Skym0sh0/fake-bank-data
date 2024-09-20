<script setup lang="ts">
const emit = defineEmits<{
  (e: "drop", item?: any): void;
}>();

function onDrop(e: DragEvent) {
  const rawData = e.dataTransfer?.getData("item")
  const data = !rawData ? undefined : JSON.parse(rawData);

  emit("drop", data)
}
</script>

<template>
  <div @drop="onDrop"
       @dragover.prevent
       @dragenter.prevent>
    <slot/>
  </div>
</template>
