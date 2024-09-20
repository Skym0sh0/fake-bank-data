<script setup lang="ts">

const {transferData} = defineProps<{ transferData?: any }>();

const emit = defineEmits<{
  (e: "onDragStart", src?: any): void;
}>();

function startDrag(ev: DragEvent) {
  if (ev.dataTransfer) {
    ev.dataTransfer.dropEffect = "move";
    ev.dataTransfer.effectAllowed = "move";
    if (transferData)
      ev.dataTransfer.setData("item", JSON.stringify(transferData));
  }

  emit("onDragStart", transferData)
}
</script>

<template>
  <div :draggable="true" @dragstart="startDrag">
    <slot/>
  </div>
</template>
