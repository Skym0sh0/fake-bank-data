import React from "react";

export interface UserContext {
    isLoggedIn(): boolean;

    login(): void;

    logout(): void;
}

const noopCtx: UserContext = {
    isLoggedIn(): boolean {
        return false;
    },
    login(): void {
    },
    logout(): void {
    },
};

export const LoginContext = React.createContext(noopCtx);
