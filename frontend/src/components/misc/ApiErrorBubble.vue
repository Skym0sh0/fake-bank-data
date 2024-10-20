<script setup lang="ts">
import {computed, nextTick, ref, watch} from "vue";
import {NotificationEvent, useNotification} from "../../store/use-notification.ts";

const notifications = useNotification();

const notification = ref<NotificationEvent>()

const color = computed(() => {
  return notification.value?.isError ? "error" : "success";
})

function reset() {
  notification.value = undefined
  notifications.reset()
}

watch(
  () => notifications.lastNotification,
  async () => {
    notification.value = undefined

    await nextTick()
    notification.value = notifications.lastNotification;
  }
)
</script>

<template>
  <v-snackbar :model-value="!!notification"
              @update:model-value="reset"
              :multi-line="notification?.isError ?? false"
              :close-on-content-click="!notification?.isError"
              :timer="true"
              :color="color">
    <template #actions>
      <v-btn variant="text"
             icon="mdi-close"
             size="sm"
             @click="reset"/>
    </template>

    <template #default v-if="notification">
      <div class="d-flex flex-column justify-start cursor-help">
        <span class="font-weight-bold">{{ notification.message }}</span>

        <span v-if="notification.error" class="text-caption text-center">
          {{ notification.error.message }}
        </span>
      </div>
    </template>
  </v-snackbar>
</template>
