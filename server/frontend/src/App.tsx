import * as React from 'react';
import {useState} from 'react';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import AppLayout from "./layout/AppLayout";

export default function App() {
    const [theme, setTheme] = useState(() => createTheme());

    return (
        <ThemeProvider theme={theme}>
            <AppLayout/>
        </ThemeProvider>
    );
}
