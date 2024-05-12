import {useCallback, useMemo, useState} from "react";
import {useStorage} from "../utils/useStorage";

export interface User {
    id: string;
    name: string;
    email: string;
    authToken?: string;
}

export function useUser() {
    const storage = useStorage();
    // const {user, setUser} = useContext(AuthContext);
    const [user, setUser] = useState<User | null>(null);

    console.log("use User Hook", user)

    const addUser = useCallback((user: User) => {
        setUser(user);
        storage.setItem("user", JSON.stringify(user));
    }, [setUser, storage]);

    const removeUser = useCallback(() => {
        setUser(null);
        storage.removeItem("user");
    }, [setUser, storage]);

    return useMemo(() => {
        return {user, addUser, removeUser, setUser};
    }, [user, addUser, removeUser, setUser]);
}
