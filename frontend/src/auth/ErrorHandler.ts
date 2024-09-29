import {ref} from "vue";

const lastError = ref<Problem | null>(null)

function resetError() {
    lastError.value = null;
}

export const errorReference = ref({
    lastError,
    resetError
})
