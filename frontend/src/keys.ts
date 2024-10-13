import type {InjectionKey} from 'vue';
import {userReference} from "./auth/Authentication.ts";
import {ErrorNotifier} from "./auth/ErrorHandler.ts";
import {CategoryApi, ReportsApi, TurnoversApi, UserAuthApi} from "@api/index.ts"

export type ApiType = {
  authApi: UserAuthApi;
  reportsApi: ReportsApi;
  categoriesApi: CategoryApi;
  turnoversApi: TurnoversApi;
}

export const authenticationKey = Symbol() as InjectionKey<typeof userReference>
export const notifierRefKey = Symbol() as InjectionKey<ErrorNotifier>;
export const apiRefKey = Symbol() as InjectionKey<ApiType>;
