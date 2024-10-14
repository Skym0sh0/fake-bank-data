import type {InjectionKey} from 'vue';
import {ErrorNotifier} from "./auth/ErrorHandler.ts";
import {CategoryApi, ReportsApi, TurnoversApi, UserAuthApi} from "@api/index.ts"

export type ApiType = {
  authApi: UserAuthApi;
  reportsApi: ReportsApi;
  categoriesApi: CategoryApi;
  turnoversApi: TurnoversApi;
}

export const notifierRefKey = Symbol() as InjectionKey<ErrorNotifier>;
export const apiRefKey = Symbol() as InjectionKey<ApiType>;
