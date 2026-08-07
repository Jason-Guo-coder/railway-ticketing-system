import { createRouter, createWebHistory } from 'vue-router'
import store from '../store'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('../views/ConsoleView.vue'),
      redirect: '/welcome',
      meta: { requiresAuth: true },
      children: [
        {
          path: 'welcome',
          component: () => import('../views/HomeView.vue'),
          meta: { title: '欢迎' },
        },
        {
          path: 'ticket',
          component: () => import('../views/TicketView.vue'),
          meta: { title: '余票查询' },
        },
        {
          path: 'order',
          component: () => import('../views/OrderView.vue'),
          meta: { title: '确认订单' },
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
      redirect: '/welcome',
    },
  ],
})

router.beforeEach((to) => {
  if (to.meta.requiresAuth && !store.state.member.token) {
    return {
      path: '/login',
      query: { redirect: to.fullPath },
    }
  }

  if (to.path === '/login' && store.state.member.token) {
    return '/welcome'
  }

  return true
})

router.afterEach((to) => {
  document.title = `${to.meta.title || '铁路票务系统'} - 铁路票务系统`
})

export default router
