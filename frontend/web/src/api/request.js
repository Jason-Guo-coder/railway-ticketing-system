import axios from 'axios'
import router from '@/router'
import store from '@/store'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/',
  timeout: 10000,
})

request.interceptors.request.use(
  (config) => {
    const token = store.state.member.token
    if (token) {
      config.headers.token = token
    }

    if (import.meta.env.DEV) {
      console.log('Axios 请求：', {
        method: config.method?.toUpperCase(),
        url: config.url,
        params: config.params,
        data: config.data,
      })
    }
    return config
  },
  (error) => {
    if (import.meta.env.DEV) {
      console.error('Axios 请求错误：', error)
    }
    return Promise.reject(error)
  },
)

request.interceptors.response.use(
  (response) => {
    if (import.meta.env.DEV) {
      console.log('Axios 响应：', {
        url: response.config.url,
        status: response.status,
        data: response.data,
      })
    }
    return response.data
  },
  (error) => {
    if (error.response?.status === 401) {
      const redirect = router.currentRoute.value.fullPath
      store.commit('clearMember')
      if (router.currentRoute.value.path !== '/login') {
        void router.replace({
          path: '/login',
          query: { redirect },
        })
      }
    }

    if (import.meta.env.DEV) {
      console.error('Axios 响应错误：', {
        url: error.config?.url,
        status: error.response?.status,
        data: error.response?.data,
        message: error.message,
      })
    }
    return Promise.reject(error)
  },
)

export default request
