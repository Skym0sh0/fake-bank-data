import {ref} from "vue";

const lastError = ref<Error | undefined>(undefined)

function resetError() {
  lastError.value = undefined;
}

function setError(e: Error) {
  lastError.value = e
}

export const errorReference = ref({
  lastError,
  setError,
  resetError
})
