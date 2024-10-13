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
import {authenticationKey, notifierRefKey} from "./keys.ts";
import {userReference} from "./auth/Authentication.js";
import {errorReference} from "./auth/ErrorHandler.ts";
import {router} from "./router.ts";

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

createApp(App)
    .provide(authenticationKey, userReference)
    .provide(notifierRefKey, errorReference)
    .use(vuetify)
    .use(router)
    .mount('#app')
