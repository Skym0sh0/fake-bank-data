import {defineStore} from "pinia";
import {Category, CategoryPatch} from "@api/api.ts";
import {computed, ref} from "vue";
import {
  flatCategoryTreeWithParentChain,
  mapCategoriesById,
  mapCategoriesByName
} from "../components/misc/categoryHelpers.ts";
import {useApi} from "./use-api.ts";
import {useNotification} from "./use-notification.ts";

export const useCategories = defineStore('categories', () => {
  const api = useApi()
  const notifications = useNotification();

  const isInitialized = ref(false)
  const isLoading = ref(false)
  const currentlyLoading = ref<Promise<void>>()
  const categories = ref<Category[]>([])

  const reload = () => {
    isLoading.value = true

    const tmp = api.categoriesApi.getCategoriesAsTree()
      .then(res => categories.value = res.data)
      .then(() => {
        isInitialized.value = true
      })
      .catch(e => notifications.notifyError("Kategorien konnten nicht geladen werden", e))
      .finally(() => {
        isLoading.value = false;
        currentlyLoading.value = undefined
      })

    currentlyLoading.value = tmp

    return tmp;
  }

  const createCategory = (categoryName: string) => {
    const normalized: CategoryPatch = {
      name: categoryName,
    };

    isLoading.value = true
    return api.categoriesApi.createCategory(normalized)
      .catch(e => notifications.notifyError(`Neue Kategorie ${categoryName} konnte nicht erstellt werden`, e))
      .then(() => reload())
      .finally(() => isLoading.value = false)
  }

  const flattedCategories = computed(() => {
    return flatCategoryTreeWithParentChain(categories.value, parents => parents.join(" > "));
  })

  const categoriesByName = computed(() => {
    return mapCategoriesByName(flattedCategories.value)
  })

  const categoriesById = computed(() => {
    return mapCategoriesById(flattedCategories.value)
  })

  if (!isInitialized.value)
    reload()

  return {
    isInitialized,
    isLoading,
    currentlyLoading,
    categories,
    flattedCategories,
    categoriesByName,
    categoriesById,
    createCategory,
    reload,
  };
})
