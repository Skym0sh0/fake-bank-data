import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router'
import Home from "./components/Home.vue";
import LoginPage from "./components/login/LoginPage.vue";
import RegisterPage from "./components/login/RegisterPage.vue";
import UserDetailsPage from "./components/login/UserDetailsPage.vue";
import About from "./components/About.vue";
import ReportOverview from "./components/reports/simple/ReportOverview.vue";
import TimelyReportOverview from "./components/reports/sankeys/TimelyReportOverview.vue";
import CategoryOverview from "./components/category/CategoryOverview.vue";
import TurnoverImportsOverview from "./components/turnovers/TurnoverImportsOverview.vue";
import TurnoversDetail from "./components/turnovers/detail/TurnoversDetail.vue";
import {useUserStore} from "./store/user-store.ts";
import TurnoverMonthOverview from "./components/turnovers/TurnoverMonthOverview.vue";

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
    path: '/turnover-imports/',
    name: 'turnover-imports-overview',
    component: TurnoverImportsOverview,
    page: {
      title: "Umsatz Importe",
      shortDescription: "Umsätze eintragen",
      icon: 'mdi-cash-multiple',
    }
  },
  {
    path: '/turnover-months/',
    name: 'turnover-months-overview',
    component: TurnoverMonthOverview,
    page: {
      title: "Monatliche Umsätze",
      shortDescription: "Umsätze pro Monat",
      icon: 'mdi-cash-clock',
    }
  },
  {
    path: '/turnovers/:id',
    name: "turnovers-detail",
    props: true,
    component: TurnoversDetail,
  },
  {
    path: '/categories/',
    name: 'category-overview',
    component: CategoryOverview,
    page: {
      title: "Kategorien",
      shortDescription: "Kategorien pflegen",
      icon: 'mdi-home-group',
    }
  },
  {
    path: '/reports/',
    name: 'reports-overview',
    component: ReportOverview,
    page: {
      title: "Berichte",
      shortDescription: "Einfache Berichte",
      icon: 'mdi-chart-bar',
    },
  },
  {
    path: '/timely-reports/',
    name: 'timely-reports-overview',
    component: TimelyReportOverview,
    page: {
      title: "Zeitreihen Berichte",
      shortDescription: "Berichte über Zeitreihen",
      icon: 'mdi-chart-areaspline',
    }
  },
  {
    path: '/about',
    name: 'about',
    component: About,
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

  const userStore = useUserStore();

  if (!to.matched.length)
    return next({name: "home"});

  if (authRequired && !userStore.isLoggedIn) {
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
