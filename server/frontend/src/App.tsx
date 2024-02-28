import * as React from 'react';
import {useMemo, useState} from 'react';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import AppLayout from "./layout/AppLayout";
import {RouterProvider,} from "react-router-dom";
import {getRouter} from './router/Routes';

export default function App() {
    const [theme, setTheme] = useState(() => createTheme());

    const router = useMemo(() => getRouter(), []);

    return (
        <React.StrictMode>
            <ThemeProvider theme={theme}>
                <AppLayout>
                    <RouterProvider router={router}/>
                </AppLayout>
            </ThemeProvider>
        </React.StrictMode>
    );
}
