import Vue from 'vue'
import Router from 'vue-router'
import Home from './components/Home.vue'
import StatementOverview from './components/statements/StatementOverview.vue'
import StatementEntering from "./components/statements/StatementEntering.vue";
import TurnoverOverview from "./components/turnovers/TurnoverOverview";
import TurnoversDetail from "./components/turnovers/TurnoversDetail";
import {getUser} from "@/auth/auth-header";
import LoginPage from "@/components/login/LoginPage.vue";

Vue.use(Router)

let router = new Router({
    routes: [
        {
            path: '/',
            name: 'home',
            component: Home,
        },
        {
            path: '/login',
            name: 'login',
            component: LoginPage,
        },
        {
            path: '/statements/',
            name: 'statements-overview',
            component: StatementOverview,
        },
        {
            path: '/statements/:id',
            name: 'statement-edit',
            props: true,
            component: StatementEntering,
        },
        {
            path: '/turnovers/',
            name: 'turnover-overview',
            component: TurnoverOverview,
        },
        {
            path: '/turnovers/:id',
            name: "turnovers-detail",
            component: TurnoversDetail,
        },
        {
            path: '/categories/',
            name: 'category-overview',
            component: () => import("./components/categories/CategoryOverview"),
        },
        {
            path: '/reports/',
            name: 'reports-overview',
            component: () => import("./components/reports/ReportOverview"),
        },
        {
            path: '/about',
            name: 'about',
            // route level code-splitting
            // this generates a separate chunk (about.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import(/* webpackChunkName: "about" */ './components/about/About.vue'),
        }
    ]
});

router.beforeEach((to, from, next) => {
    const publicPages = ['/login'];
    const authRequired = !publicPages.includes(to.path);
    const loggedIn = getUser();

    console.log("before", authRequired, loggedIn)

    if (authRequired && !loggedIn) {
        return next({
            path: '/login',
            query: {
                returnUrl: to.path
            }
        });
    }

    next();
});

export default router
