import {useCallback, useMemo} from "react";

export function useStorage() {
    const setItem = useCallback((key: string, value: string) => {
        localStorage.setItem(key, value);
    }, []);

    const getItem = useCallback((key: string) => {
        return localStorage.getItem(key);
    }, []);

    const removeItem = useCallback((key: string) => {
        localStorage.removeItem(key);
    }, []);

    return useMemo(() => {
        return {setItem, getItem, removeItem}
    }, [setItem, getItem, removeItem]);
}
