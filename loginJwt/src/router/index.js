import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/views/login/login.vue'
import Main from '@/views/main/main.vue'
import Home from '@/views/home/home.vue'
import Success from '@/views/home/success.vue'
Vue.use(Router)
//Vue.use(Login)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    },
    {
      path: '/main',
      name: 'Main',
      component: Main
    },
    {
      path:'/login',
      name:'Login',
      component:Login
    },
    {
      path:'/success',
      name:'Success',
      component:Success
    }

  ]
})
