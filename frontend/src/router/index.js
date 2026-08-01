import { createRouter, createWebHistory } from 'vue-router'
import store from '../store'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('../views/ConsoleView.vue'),
      redirect: '/home',
      meta: { requiresAuth: true },
      children: [
        {
          path: 'home',
          component: () => import('../views/HomeView.vue'),
          meta: { title: '工作台' },
        },
        {
          path: 'ticket',
          component: () => import('../views/TicketView.vue'),
          meta: { title: '车票查询' },
        },
        {
          path: 'passenger',
          component: () => import('../views/PassengerView.vue'),
          meta: { title: '乘车人管理' },
        },
      ],
    },
    {
      path: '/login',
      component: () => import('../views/LoginView.vue'),
      meta: { title: '会员登录' },
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/home',
    },
  ],
})

router.beforeEach((to) => {
  // ponytail: replace the member id check when token authentication is added.
  if (to.meta.requiresAuth && !store.state.member.id) {
    return {
      path: '/login',
      query: { redirect: to.fullPath },
    }
  }

  if (to.path === '/login' && store.state.member.id) {
    return '/home'
  }

  return true
})

router.afterEach((to) => {
  document.title = `${to.meta.title || '铁路票务系统'} - 铁路票务系统`
})

export default router
