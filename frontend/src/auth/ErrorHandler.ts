import {ref} from "vue";
import {Problem} from "@api/api.ts";

const lastError = ref<Problem | undefined>(undefined)

function resetError() {
  lastError.value = undefined;
}

function setError(e: Problem) {
  console.log("new error", e)
  lastError.value = e
}

export const errorReference = ref({
  lastError,
  setError,
  resetError
})
