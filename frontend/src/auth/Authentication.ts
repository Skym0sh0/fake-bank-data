import {readonly, ref} from "vue";
import {User} from "@api/api.ts";

const STORAGE_USER_KEY = "user";

// const storage = sessionStorage;
const storage = localStorage;

type StorageAccess = {
  store: (key: string, data: string) => void;
  read: (key: string) => string | null;
  delete: (key: string) => void;
}

const storageAccess: StorageAccess = {
  store: (key, data) => {
    storage.setItem(key, btoa(data))
  },
  read: (key) => {
    const item = storage.getItem(key);
    if (!item)
      return null;
    return atob(item)
  },
  delete: (key) => {
    storage.removeItem(key)
  },
};

export type UserType = User & { password: string }

const userRef = ref<UserType | null>(null)

function setUser(user: UserType) {
  storageAccess.store(STORAGE_USER_KEY, JSON.stringify(user))
  userRef.value = user;
}

function getUser() {
  const userJson = storageAccess.read(STORAGE_USER_KEY);
  if (!userJson)
    return null;

  return JSON.parse(userJson)
}

function initUser() {
  const userJson = storageAccess.read(STORAGE_USER_KEY);
  if (!userJson)
    return null;

  userRef.value = JSON.parse(userJson)
}

function login(user: User, password: string) {
  setUser({...user, password: password});
}

function logout() {
  storageAccess.delete(STORAGE_USER_KEY);
  userRef.value = null;

  // location.reload();
}

export const userReference = ref(readonly({
  user: userRef,
  getUser,
  setUser,
  initUser,
  login,
  logout
}))
