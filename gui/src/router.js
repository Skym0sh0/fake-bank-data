import Vue from 'vue'
import Router from 'vue-router'
import Home from './views/Home.vue'
import StatementOverview from './components/overview/StatementOverview'
import StatementEntering from "./components/enterings/StatementEntering";
import CategoryOverview from "./components/categories/CategoryOverview";
import ReportOverview from "./components/reports/ReportOverview";

Vue.use(Router)

export default new Router({
    routes: [
        {
            path: '/',
            name: 'home',
            component: Home,
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
            path: '/categories/',
            name: 'category-overview',
            component: CategoryOverview,
        },
        {
            path: '/reports/',
            name: 'reports-overview',
            component: ReportOverview,
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
})
