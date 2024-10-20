import {defineStore} from "pinia";
import {Category, CategoryPatch} from "@api/api.ts";
import {computed, inject, ref} from "vue";
import {
  flatCategoryTreeWithParentChain,
  mapCategoriesById,
  mapCategoriesByName
} from "../components/misc/categoryHelpers.ts";
import {notifierRefKey} from "../keys.ts";
import {useApi} from "./use-api.ts";

export const useCategories = defineStore('categories', () => {
  const api = useApi()
  const notifier = inject(notifierRefKey);

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
      .catch(e => notifier?.notifyError("Kategorien konnten nicht geladen werden", e))
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
    api.categoriesApi.createCategory(normalized)
      .catch(e => notifier?.notifyError(`Neue Kategorie ${categoryName} konnte nicht erstellt werden`, e))
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
