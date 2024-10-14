import type {InjectionKey} from 'vue';
import {ErrorNotifier} from "./auth/ErrorHandler.ts";

export const notifierRefKey = Symbol() as InjectionKey<ErrorNotifier>;
