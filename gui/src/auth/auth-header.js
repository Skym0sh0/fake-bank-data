const STORAGE_USER_KEY = "user";

function setUser(user) {
    sessionStorage.setItem(STORAGE_USER_KEY, JSON.stringify(user))
}

export function getUser() {
    return sessionStorage.getItem(STORAGE_USER_KEY)
}

export function authHeader() {
    const user = JSON.parse(getUser());

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

    setUser(user)
}

function logout() {
    sessionStorage.removeItem(STORAGE_USER_KEY);
}

export const userService = {
    login,
    logout
};

/*
 TODO: mit axios verbinden
 -> bei auth error automatisch ausloggen
 -> auth header immer mitsenden
 */

