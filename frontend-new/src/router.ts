import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router'
import Home from "./components/Home.vue";
import LoginPage from "./components/login/LoginPage.vue";
import {inject} from "vue";
import {authenticationKey} from "./keys.ts";
import RegisterPage from "./components/login/RegisterPage.vue";
import UserDetailsPage from "./components/login/UserDetailsPage.vue";

export type Page = {
  link: string;
  title: string;
  icon?: string;
  shortDescription?: string;
  isStartPage?: boolean;
}

type RouterPage = RouteRecordRaw & { page?: Omit<Page, 'link'> };

const routes: RouterPage[] = [
  {
    path: '/',
    name: 'home',
    component: Home,
    page: {
      title: "Dashboard",
      icon: 'mdi-view-dashboard',
      shortDescription: "Dashboard",
      isStartPage: true,
    }
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
  {
    path: '/user-details',
    name: 'user-details',
    component: UserDetailsPage,
  },

  {
    path: '/turnovers/',
    name: 'turnover-overview',
    component: Home,
    page: {
      title: "Umsätze",
      shortDescription: "Umsätze eintragen",
      icon: 'mdi-cash-multiple',
    }
  },
  {
    path: '/turnovers/:id',
    name: "turnovers-detail",
    props: true,
    component: Home,
  },
  {
    path: '/categories/',
    name: 'category-overview',
    component: Home,
    page: {
      title: "Kategorien",
      shortDescription: "Kategorien pflegen",
      icon: 'mdi-home-group',
    }
  },
  {
    path: '/reports/',
    name: 'reports-overview',
    component: Home,
    page: {
      title: "Berichte",
      shortDescription: "Einfache Berichte",
      icon: 'mdi-chart-bar',
    },
  },
  {
    path: '/timely-reports/',
    name: 'timely-reports-overview',
    component: Home,
    page: {
      title: "Zeitreihen Berichte",
      shortDescription: "Berichte über Zeitreihen",
      icon: 'mdi-chart-areaspline',
    }
  },
  {
    path: '/about',
    name: 'about',
    component: Home,
    page: {
      title: "Hilfe",
      shortDescription: "Hilfe",
      icon: 'mdi-help-circle',
    }
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _, next) => {
  const publicPages = ['/login', '/register'];
  const authRequired = !publicPages.includes(to.path);
  const userService = inject(authenticationKey)?.value
  const loggedIn = !!userService?.getUser();

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

function getPages(): Readonly<Page[]> {
  return routes.flatMap(p => !p.page ? [] : [{...p.page, link: p.path}]);
}

export {
  router,
  getPages
}
