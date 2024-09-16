import type {InjectionKey} from 'vue';
import {userReference} from "./auth/Authentication.ts";
import {errorReference} from "./auth/ErrorHandler.ts";
import {CategoryApi, ReportsApi, UserAuthApi} from "@api/index.ts"

export type ApiType = {
  authApi: UserAuthApi;
  reportsApi: ReportsApi;
  categoriesApi: CategoryApi;
}

export const authenticationKey = Symbol() as InjectionKey<typeof userReference>
export const errorRefKey = Symbol() as InjectionKey<typeof errorReference>;
export const apiRefKey = Symbol() as InjectionKey<ApiType>;
