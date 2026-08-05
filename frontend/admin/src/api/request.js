import axios from 'axios'
import router from '@/router'
import store from '@/store'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/',
  timeout: 10000,
})

request.interceptors.request.use((config) => {
  const token = store.state.admin.token
  if (token) {
    config.headers.token = token
  }
  return config
})

request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      const redirect = router.currentRoute.value.fullPath
      store.commit('clearAdmin')
      if (router.currentRoute.value.path !== '/login') {
        void router.replace({
          path: '/login',
          query: { redirect },
        })
      }
    }
    return Promise.reject(error)
  },
)

export default request
