import Vue from 'vue'
import App from './App.vue'
import router from './router'
import Vuelidate from 'vuelidate'
import vuetify from './plugins/vuetify';

import { Drag, Drop } from 'vue-drag-drop';

import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

Vue.config.productionTip = false

Vue.component('drag', Drag);
Vue.component('drop', Drop);

Vue.use(BootstrapVue)
Vue.use(Vuelidate)

new Vue({
    router,
    vuetify,
    render: h => h(App)
}).$mount('#app')
