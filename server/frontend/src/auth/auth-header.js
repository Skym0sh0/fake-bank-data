const STORAGE_USER_KEY = "user";

// const storage = sessionStorage;
const storage = localStorage;

const userReference = {
    user: null,
}

const storageAccess = {
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

function setUser(user) {
    storageAccess.store(STORAGE_USER_KEY, JSON.stringify(user))
    userReference.user = user;
}

function getUser() {
    const userJson = storageAccess.read(STORAGE_USER_KEY);
    return JSON.parse(userJson)
}

function initUser() {
    const userJson = storageAccess.read(STORAGE_USER_KEY);
    userReference.user = JSON.parse(userJson)
}

function login(user, password) {
    setUser({...user, password: password});
}

function logout() {
    storageAccess.delete(STORAGE_USER_KEY);
    userReference.user = null;

    // location.reload();
}

export const userService = {
    userReference,
    login,
    initUser,
    logout,
    getUser,
};
