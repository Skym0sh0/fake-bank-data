import {createWebHistory, createRouter, RouteRecordRaw} from 'vue-router'
import Home from "./components/Home.vue";
import LoginPage from "./components/login/LoginPage.vue";
import {inject} from "vue";
import {authenticationKey} from "./keys.ts";
import RegisterPage from "./components/login/RegisterPage.vue";

const routes: RouteRecordRaw[] = [
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
        path: '/register',
        name: 'register',
        component: RegisterPage,
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
})

router.beforeEach((to, from, next) => {
    const publicPages = ['/login', '/register'];
    const authRequired = !publicPages.includes(to.path);
    const userService = inject(authenticationKey).value
    const loggedIn = !!userService.getUser();

    if (!to.matched.length)
        return next({name: "home"});

    if (authRequired && !loggedIn) {
        return next({
            name: 'login',
            query: {
                returnUrl: to.path
            }
        });
    }

    next();
})

export {
    router
}
