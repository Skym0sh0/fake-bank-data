import {createBrowserRouter, redirect, RouteObject} from "react-router-dom";
import LoginForm from "../login/LoginForm";
import * as React from "react";
import Dashboard from "../dashboard/Dashboard";
import DashboardIcon from '@mui/icons-material/Dashboard';
import LoginIcon from '@mui/icons-material/Login';
import PaymentsIcon from '@mui/icons-material/Payments';
import {authenticationProvider} from "../login/LoginContext";

export type Page = {
    path: string;
    title: string;
    shortDescription?: string;
    description?: string;
    icon?: React.ReactNode;
    isStartPage?: boolean;
    isUnsecure?: boolean;
    isNotForDashboard?: boolean;
};

type RouterPage = Page & RouteObject;

function authGuardLoader(): any {
    console.log(authenticationProvider, authenticationProvider.isLoggedIn())

    if (!authenticationProvider.isLoggedIn())
        return redirect("/login")

    return null;
}

function addSecureGuards(pages: RouterPage[]): RouterPage[] {
    return pages.map(page => {
        const secureGuard = page.isUnsecure
            ? undefined
            : authGuardLoader;

        return Object.freeze({
            ...page,
            loader: secureGuard,
        });
    });
}

const pageDefinitions: RouterPage[] = addSecureGuards([
    {
        path: '/',
        title: 'Dashboard',
        description: 'Dashboard, wo alles ist',
        shortDescription: 'Dashboard',
        icon: <DashboardIcon/>,
        isStartPage: true,
        element: <Dashboard/>,
    },
    {
        path: '/turnovers',
        title: 'Umsätze',
        description: 'Umsätze eingeben, ändern und verwalten',
        shortDescription: 'Umsätze verwalten',
        icon: <PaymentsIcon/>,
        element: <div>Some turnovers</div>,
    },
    {
        path: '/login',
        title: 'Login',
        icon: <LoginIcon/>, // import LogoutIcon from '@mui/icons-material/Logout';
        element: <LoginForm/>,
        isUnsecure: true,
        isNotForDashboard: true,
    },
    {
        path: '/register',
        title: 'Register',
        icon: <LoginIcon/>, // import LogoutIcon from '@mui/icons-material/Logout';
        element: <LoginForm/>,
        isUnsecure: true,
        isNotForDashboard: true,
    }
]);

export function getRouter() {
    return createBrowserRouter(pageDefinitions);
}

export function getPages(): ReadonlyArray<Page> {
    return Object.freeze(pageDefinitions);
}

export function getPagesToDisplay(): ReadonlyArray<Page> {
    return Object.freeze(pageDefinitions).filter(p => !p.isNotForDashboard);
}
