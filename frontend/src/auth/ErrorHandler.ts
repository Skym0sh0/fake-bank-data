import {ref} from "vue";
import {Problem} from "@api/api.ts";

const lastError = ref<Problem | null>(null)

function resetError() {
  lastError.value = null;
}

function setError(e: Problem) {
  lastError.value = e
}

export const errorReference = ref({
  setError,
  lastError,
  resetError
})
