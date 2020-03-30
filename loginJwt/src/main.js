// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import axios from 'axios';
import global from './assets/js/global'
import VueCookies from 'vue-cookies'

Vue.use(VueCookies);
Vue.prototype.$axios = axios;
Vue.config.productionTip = false;
Vue.prototype.global = global;
/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'

})

axios.interceptors.request.use(
  config=>{
    let thisToken = "Bearer "+Vue.$cookies.get('token');
    config.headers.Authorization=thisToken;
    console.log("设置token成功"+thisToken);
    return config;
  }
);


axios.interceptors.response.use((res=>{
  console.log("进入response拦截器");
  let thisToken = res.headers['authorization'];
  if (thisToken!=null){
    //console.log(Vue.$cookies.get('username'));
    Vue.$cookies.set('token',thisToken);
    //const info = {name:Vue.$cookies.get('username'),token:thisToken};
    //localStorage.setItem('userInfo',JSON.stringify(info));
    console.log("设置cookie，localStorage成功："+Vue.$cookies.get('token'));
  }
  return res;
}));
