import {defineStore} from 'pinia'
import {User} from "@api/api.ts";
import {computed, ref} from "vue";
import {useEncodedStorage} from "./use-storage.ts";

export type UserType = User & { password: string }

const STORAGE_USER_KEY = "user";

export const useUserStore = defineStore('user', () => {
  const storage = useEncodedStorage();

  const currentUser = ref<UserType>()

  const isLoggedIn = computed(() => {
    return !!currentUser.value
  })

  const initUser = () => {
    const user = storage.read(STORAGE_USER_KEY)

    if (user)
      currentUser.value = JSON.parse(user)
  }

  const login = (user: User, password: string) => {
    const cur: UserType = {...user, password: password};

    storage.store(STORAGE_USER_KEY, JSON.stringify(cur));
    currentUser.value = cur;
  }

  const logout = () => {
    storage.remove(STORAGE_USER_KEY)
    currentUser.value = undefined

    // location.reload()
  }

  return {
    currentUser,
    isLoggedIn,
    initUser,
    login,
    logout
  }
})
