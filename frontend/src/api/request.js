import axios from 'axios'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/',
  timeout: 10000,
})

request.interceptors.request.use(
  (config) => {
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
