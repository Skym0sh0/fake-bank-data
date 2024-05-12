import {useCallback, useEffect, useMemo} from "react";
import {User, useUser} from "./useUser";
import {useStorage} from "../utils/useStorage";

export function useAuth() {
    // we can re export the user methods or object from this hook
    const user = useUser();
    const storage = useStorage();

    console.log("useAuth Hook", user)

    useEffect(() => {
        const userInfo = storage.getItem("user");
        console.log("init auth", user)
        if (userInfo) {
            user.addUser(JSON.parse(userInfo));
        }
    }, []);

    const login = useCallback((userInfo: User): Promise<void> => {
        return new Promise(resolve => {
            setTimeout(() => {
                user.addUser(userInfo)
                resolve();
            }, 500)
        });
    }, [user]);

    const logout = useCallback((): Promise<void> => {
        return new Promise(resolve => {
            setTimeout(() => {
                user.removeUser();
                resolve();
            }, 500)
        });
    }, [user]);

    return useMemo(() => {
        return {user: user.user, login, logout, setUser: user.setUser};
    }, [user, login, logout]);
}
