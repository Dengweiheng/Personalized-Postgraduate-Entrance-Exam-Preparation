import axios from 'axios'
import { ElMessage } from 'element-plus'

const http = axios.create({
  baseURL: '/api',
  timeout: 10000
})

http.interceptors.request.use((config) => {
  const username = localStorage.getItem('auth.username') || ''
  const password = localStorage.getItem('auth.password') || ''
  if (username && password) {
    const token = btoa(`${username}:${password}`)
    config.headers.Authorization = `Basic ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const payload = response?.data
    if (payload && typeof payload === 'object' && 'code' in payload && payload.code !== 200) {
      return Promise.reject({
        response: {
          data: payload,
          status: 200
        }
      })
    }
    return response
  },
  (error) => {
    if (error?.response?.status === 401) {
      ElMessage.error('未登录或登录已失效，请重新登录')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default http
