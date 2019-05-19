import Vue from 'vue'
import Router from 'vue-router'
import Home from './views/Home.vue'
import Overview from './components/overview/Overview'

Vue.use(Router)

export default new Router({
    routes: [
        {
            path: '/',
            name: 'home',
            component: Home
        },
        {
            path: '/overview',
            name: 'overview',
            component: Overview
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
