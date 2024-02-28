import {createBrowserRouter, RouteObject} from "react-router-dom";
import LoginForm from "../login/LoginForm";
import * as React from "react";
import Dashboard from "../dashboard/Dashboard";
import DashboardIcon from '@mui/icons-material/Dashboard';
import LoginIcon from '@mui/icons-material/Login';
import PaymentsIcon from '@mui/icons-material/Payments';

export type Page = {
    path: string;
    title: string;
    shortDescription?: string;
    description?: string;
    icon?: React.ReactNode;
    isStartPage?: boolean;
};

type RouterPage = Page & RouteObject;

const pageDefinitions: RouterPage[] = [
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
        title: 'Login', icon: <LoginIcon/>, // import LogoutIcon from '@mui/icons-material/Logout';
        element: <LoginForm/>,
    }
];

export function getRouter() {
    return createBrowserRouter(pageDefinitions);
}

export function getPages(): ReadonlyArray<Page> {
    return Object.freeze(pageDefinitions);
}
