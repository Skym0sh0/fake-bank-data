import {provide, readonly, ref} from "vue";
import {authenticationKey} from "../keys.ts";

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

export interface UserType {
    username: string;
    firstname?: string;
    lastname?: string;
}

const user = ref<UserType | null>(null)

function setUser(user) {
    storageAccess.store(STORAGE_USER_KEY, JSON.stringify(user))
    user.value = user;
}

function getUser() {
    const userJson = storageAccess.read(STORAGE_USER_KEY);
    return JSON.parse(userJson)
}

function initUser() {
    const userJson = storageAccess.read(STORAGE_USER_KEY);
    user.value = JSON.parse(userJson)
}

function login(user, password) {
    setUser({...user, password: password});
}

function logout() {
    storageAccess.delete(STORAGE_USER_KEY);
    user.value = null;

    // location.reload();
}

export const userReference = ref({
    user,
    getUser,
    setUser,
    initUser,
    login,
    logout
})
