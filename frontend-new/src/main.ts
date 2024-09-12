import {createApp} from 'vue'
import './style.css'
import '@mdi/font/css/materialdesignicons.css' // Ensure you are using css-loader
import 'vuetify/styles'
import {createVuetify} from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

import App from './App.vue'
import {authenticationKey, errorRefKey} from "./keys.ts";
import {userReference} from "./auth/Authentication.js";
import {errorReference} from "./auth/ErrorHandler.ts";

const vuetify = createVuetify({
    components,
    directives,
    icons: {
        defaultSet: 'mdi',
    },
})

createApp(App)
    .use(vuetify)
    .provide(authenticationKey, userReference)
    .provide(errorRefKey, errorReference)
    .mount('#app')
