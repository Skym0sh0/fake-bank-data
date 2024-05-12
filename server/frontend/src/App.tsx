import * as React from 'react';
import {useMemo, useState} from 'react';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import AppLayout from "./layout/AppLayout";
import {RouterProvider,} from "react-router-dom";
import {getRouter} from './router/Routes';
import {useAuth} from "./login/useAuth";
import {AuthContext} from "./login/AuthContext";

export default function App() {
    const auth = useAuth();

    console.log("refresh auth layer", auth)

    const router = useMemo(() => getRouter(), []);

    return <React.StrictMode>
        <AuthContext.Provider value={{user: auth.user, setUser: auth.setUser}}>
            <ThemeLayer>
                <AppLayout>
                    <RouterProvider router={router}/>
                </AppLayout>
            </ThemeLayer>
        </AuthContext.Provider>
    </React.StrictMode>;
}

function ThemeLayer({children}: { children?: React.ReactNode }) {
    const [theme/*, setTheme*/] = useState(() => createTheme());

    return <ThemeProvider theme={theme}>
        {children}
    </ThemeProvider>;
}
