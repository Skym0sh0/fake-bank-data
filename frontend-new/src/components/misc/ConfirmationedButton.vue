<script setup lang="ts">
import {computed, ref} from "vue";

type Props = {
  defaultCaption: string;
  requestCaption: string;
  confirmedCaption: string;
  defaultColor?: string;
  waitTimeMs?: number;
}

const {
  defaultCaption,
  requestCaption,
  confirmedCaption,
  defaultColor = 'info',
  waitTimeMs = 1500
} = defineProps<Props>()

const emit = defineEmits(['click'])

const isLoading = ref(false);
const actionRequested = ref(false);
const actionConfirmed = ref(false);

const caption = computed(() => {
  if (!actionRequested.value)
    return defaultCaption

  if (!actionConfirmed.value)
    return requestCaption

  return confirmedCaption
});

const color = computed(() => {
  if (!actionRequested.value)
    return defaultColor;

  if (!actionConfirmed.value)
    return "warning"

  return "error";
});

function onClick() {
  const defer = (work: () => void) => {
    isLoading.value = true;
    setTimeout(() => {
      work()
      isLoading.value = false;
    }, waitTimeMs)
  };

  if (!actionRequested.value) {
    defer(() => actionRequested.value = true)
    return;
  }

  if (!actionConfirmed.value) {
    defer(() => actionConfirmed.value = true)
    return;
  }

  emit("click")
  reset()
}

function reset() {
  actionRequested.value = false;
  actionConfirmed.value = false;
}
</script>

<template>
  <v-btn :color="color"
         @click="onClick"
         :loading="isLoading"
         v-bind="$attrs">
    {{ caption }}
  </v-btn>
</template>
