import * as React from 'react';
import {useMemo, useState} from 'react';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import AppLayout from "./layout/AppLayout";
import {RouterProvider,} from "react-router-dom";
import {getRouter} from './router/Routes';
import {LoginContext, UserContext} from "./login/LoginContext";

export default function App() {
    const [theme/*, setTheme*/] = useState(() => createTheme());

    const router = useMemo(() => getRouter(), []);

    return (
        <React.StrictMode>
            <ThemeProvider theme={theme}>
                <AuthLayer>
                    <AppLayout>
                        <RouterProvider router={router}/>
                    </AppLayout>
                </AuthLayer>
            </ThemeProvider>
        </React.StrictMode>
    );
}

function AuthLayer({children}: { children?: React.ReactNode }) {
    const userLogin: UserContext = {
        isLoggedIn(): boolean {
            return !!localStorage.getItem("is-logged-in");
        },

        login(): void {
            localStorage.setItem("is-logged-in", "true")
        },

        logout(): void {
            localStorage.removeItem("is-logged-in")
        }
    };

    return <LoginContext.Provider value={userLogin}>
        {children}
    </LoginContext.Provider>;
}
