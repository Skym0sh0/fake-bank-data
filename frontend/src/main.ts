import {createApp} from 'vue'
import './style.css'
import '@mdi/font/css/materialdesignicons.css' // Ensure you are using css-loader
import 'vuetify/styles'
import {createVuetify} from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import {VDateInput} from 'vuetify/labs/VDateInput'
import {VTreeview} from 'vuetify/labs/VTreeview'

import App from './App.vue'
import { notifierRefKey} from "./keys.ts";
import {errorReference} from "./auth/ErrorHandler.ts";
import {router} from "./router.ts";
import {createPinia} from "pinia";

const vuetify = createVuetify({
  components: {
    ...components,
    VDateInput,
    VTreeview,
  },
  directives,
  icons: {
    defaultSet: 'mdi',
  },
})

const pinia = createPinia()

createApp(App)
  .provide(notifierRefKey, errorReference)
  .use(vuetify)
  .use(pinia)
  .use(router)
  .mount('#app')
