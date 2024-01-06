const STORAGE_USER_KEY = "user";

const userReference = {
    user: null,
}

function setUser(user) {
    sessionStorage.setItem(STORAGE_USER_KEY, JSON.stringify(user))
    userReference.user = user;
}

function getUser() {
    const userJson = sessionStorage.getItem(STORAGE_USER_KEY);
    return JSON.parse(userJson)
}

function initUser() {
    const userJson = sessionStorage.getItem(STORAGE_USER_KEY);
    userReference.user = JSON.parse(userJson)
}

export function authHeader() {
    const user = getUser();

    if (user && user.authdata) {
        return {'Authorization': 'Basic ' + user.authdata};
    } else {
        return {};
    }
}

function login(username, password) {
    const user = {
        username: username,
        password: password,

        authData: window.btoa(username + ":" + password)
    }

    setUser(user);
}

function logout() {
    sessionStorage.removeItem(STORAGE_USER_KEY);
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

/*
 TODO: mit axios verbinden
 -> bei auth error automatisch ausloggen
 -> auth header immer mitsenden
 */

