import Vue from 'vue'
import App from './App.vue'
import router from './router'
import Vuelidate from 'vuelidate'
import vuetify from './plugins/vuetify';

import GlobalEvents from 'vue-global-events'
import {Drag, Drop} from 'vue-drag-drop';

import {BootstrapVue, BootstrapVueIcons} from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import {userService} from "@/auth/auth-header";
import {errorReference} from "@/api/RegularIncomeAPI";

Vue.config.productionTip = false

Vue.component('drag', Drag);
Vue.component('drop', Drop);
Vue.component('GlobalEvents', GlobalEvents)

Vue.use(BootstrapVue)
Vue.use(BootstrapVueIcons)
Vue.use(Vuelidate)

new Vue({
    router,
    vuetify,
    render: h => h(App),
    data: {
        errorRef: errorReference,
        userRef: userService.userReference,
    }
}).$mount('#app')
