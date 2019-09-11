import Vue from 'vue'
import Router from 'vue-router'
import Home from './views/Home.vue'
import StatementOverview from './components/overview/StatementOverview'

Vue.use(Router)

export default new Router({
    routes: [
        {
            path: '/',
            name: 'home',
            component: Home
        },
        {
            path: '/statement-overview',
            name: 'statement-overview',
            component: StatementOverview
        },
        {
            path: '/about',
            name: 'about',
            // route level code-splitting
            // this generates a separate chunk (about.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import(/* webpackChunkName: "about" */ './components/about/About.vue')
        }
    ]
})
