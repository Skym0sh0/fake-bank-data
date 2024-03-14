import React from "react";

export interface UserContext {
    isLoggedIn(): boolean;

    login(): Promise<void>;

    logout(): Promise<void>;
}

export const authenticationProvider: UserContext = {
    isLoggedIn(): boolean {
        return !!localStorage.getItem("is-logged-in");
    },
    login(): Promise<void> {
        return new Promise(resolve => {
            setTimeout(() => {
                localStorage.setItem("is-logged-in", "true")
                resolve();
            }, 500)
        });
    },
    logout(): Promise<void> {
        return new Promise(resolve => {
            setTimeout(() => {
                localStorage.removeItem("is-logged-in")
                resolve();
            }, 500)
        });
    },
};

export const LoginContext = React.createContext(authenticationProvider);
