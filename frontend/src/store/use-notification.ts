import {defineStore} from "pinia";
import {computed, ref} from "vue";

export type NotificationEvent = {
  message: string;
  isError: boolean;
  error?: Error;
}

export const useNotification = defineStore("notification", () => {
  const lastNotification = ref<NotificationEvent>()

  const hasError = computed(() => !!lastNotification.value)

  const reset = () => {
    lastNotification.value = undefined;
  }

  const setNotification = (e: NotificationEvent) => {
    lastNotification.value = e
  }

  const notifyError = (msg: string, error: Error) => {
    setNotification({
      message: msg,
      isError: true,
      error: error,
    })
  }

  const notifySuccess = (msg: string) => {
    setNotification({
      message: msg,
      isError: false,
      error: undefined
    })
  }

  return {
    lastNotification,
    hasError,
    notifyError,
    notifySuccess,
    reset
  }
})
