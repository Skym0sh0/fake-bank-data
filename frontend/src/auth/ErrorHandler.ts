import {reactive, ref} from "vue";

export type NotificationEvent = {
  message: string;
  isError: boolean;
  error?: Error;
}

export type ErrorNotifier = {
  notifyError: (msg: string, error: Error) => void;
  notifySuccess: (msg: string) => void;
  reset: () => void;
  lastNotification?: NotificationEvent;
}

const lastNotification = ref<NotificationEvent>()

function reset() {
  lastNotification.value = undefined;
}

function setNotification(e: NotificationEvent) {
  lastNotification.value = e
}

function notifyError(msg: string, error: Error) {
  setNotification({
    message: msg,
    isError: true,
    error: error,
  })
}

function notifySuccess(msg: string) {
  setNotification({
    message: msg,
    isError: false,
    error: undefined
  })
}

export const errorReference: ErrorNotifier = reactive({
  lastNotification: lastNotification,
  notifyError,
  notifySuccess,
  reset,
})
