import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login.vue'),
  },
  {
    path: '/',
    component: () => import('@/views/main.vue'),
    redirect: '/welcome',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'welcome',
        name: 'welcome',
        component: () => import('@/views/main/welcome.vue'),
      },
      {
        path: 'about',
        name: 'about',
        component: () => import('@/views/main/about.vue'),
      },
      {
        path: 'base/station',
        name: 'station',
        component: () => import('@/views/main/base/station.vue'),
      },
      {
        path: 'base/train',
        name: 'train',
        component: () => import('@/views/main/base/train.vue'),
      },
      {
        path: 'base/train-station',
        name: 'train-station',
        component: () => import('@/views/main/base/train-station.vue'),
      },
      {
        path: 'base/train-carriage',
        name: 'train-carriage',
        component: () => import('@/views/main/base/train-carriage.vue'),
      },
      {
        path: 'base/train-seat',
        name: 'train-seat',
        component: () => import('@/views/main/base/train-seat.vue'),
      },
      {
        path: 'batch/job',
        name: 'batch-job',
        component: () => import('@/views/main/batch/job.vue'),
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  if (to.matched.some((record) => record.meta.requiresAuth)
      && !store.state.admin.token) {
    return {
      path: '/login',
      query: { redirect: to.fullPath },
    }
  }

  if (to.path === '/login' && store.state.admin.token) {
    return '/welcome'
  }

  return true
})

export default router
