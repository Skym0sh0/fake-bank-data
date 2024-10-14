import {defineStore} from "pinia";
import {ref} from "vue";

export const useLocalStorage = defineStore('local-store', () => {
  const storage = ref(localStorage);

  const store = (key: string, data: string) => {
    storage.value.setItem(key, data);
  }

  const read = (key: string): string | undefined => {
    return storage.value.getItem(key) ?? undefined;
  }

  const remove = (key: string) => {
    storage.value.removeItem(key);
  }

  return {
    store,
    read,
    remove
  }
})

export const useEncodedStorage = defineStore('encoded-store', () => {
  const storage = useLocalStorage();

  const store = (key: string, data: string) => {
    storage.store(key, btoa(data));
  }

  const read = (key: string): string | undefined => {
    const val = storage.read(key);
    if (!val)
      return undefined
    return atob(val)
  }

  const remove = (key: string) => {
    storage.remove(key);
  }

  return {
    store,
    read,
    remove
  }
})
