import {createBrowserRouter, Navigate, RouteObject} from "react-router-dom";
import LoginForm from "../login/LoginForm";
import * as React from "react";
import Dashboard from "../dashboard/Dashboard";
import DashboardIcon from '@mui/icons-material/Dashboard';
import LoginIcon from '@mui/icons-material/Login';
import PaymentsIcon from '@mui/icons-material/Payments';
import {useAuth} from "../login/useAuth";

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

function ProtectedRoute({children}: { children: React.ReactNode }): JSX.Element {
    const auth = useAuth();

    console.log("routes auth", auth)

    if (!auth.user) {
        console.log("            ", "not logged in -> redirecting")
        return <Navigate to="/login"/>
    }

    return <>{children}</>;
}

function addSecureGuards(pages: RouterPage[]): RouterPage[] {
    return pages.map(page => {
        const securedRoute: React.ReactNode = page.isUnsecure
            ? page.element
            : <ProtectedRoute>{page.element}</ProtectedRoute>;

        return Object.freeze({
            ...page,
            element: securedRoute,
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
