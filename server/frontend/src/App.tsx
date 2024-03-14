import * as React from 'react';
import {useMemo, useState} from 'react';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import AppLayout from "./layout/AppLayout";
import {RouterProvider,} from "react-router-dom";
import {getRouter} from './router/Routes';
import {authenticationProvider, LoginContext} from "./login/LoginContext";

export default function App() {
    const router = useMemo(() => getRouter(), []);

    return <React.StrictMode>
        <AuthLayer>
            <ThemeLayer>
                <AppLayout>
                    <RouterProvider router={router}/>
                </AppLayout>
            </ThemeLayer>
        </AuthLayer>
    </React.StrictMode>;
}

function AuthLayer({children}: { children?: React.ReactNode }) {
    return <LoginContext.Provider value={authenticationProvider}>
        {children}
    </LoginContext.Provider>;
}

function ThemeLayer({children}: { children?: React.ReactNode }) {
    const [theme/*, setTheme*/] = useState(() => createTheme());

    return <ThemeProvider theme={theme}>
        {children}
    </ThemeProvider>;
}
