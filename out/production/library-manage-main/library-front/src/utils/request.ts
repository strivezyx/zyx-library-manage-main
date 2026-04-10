import axios from 'axios'
import router from '@/router'
import { ElMessage } from 'element-plus'
import { useManagerStore } from '@/store'

const baseURL = '/api'
const instance = axios.create({ baseURL })

instance.interceptors.request.use(
  (config) => {
    const store = useManagerStore()
    const token =
      store.loginRole === 'reader'
        ? store.readerInfo?.token
        : store.managerInfo?.token || store.readerInfo?.token

    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  (error) => Promise.reject(error),
)

instance.interceptors.response.use(
  (response) => {
    if ('code' in response.data && response.data.code !== 0) {
      ElMessage.error(response.data.msg)
    }
    return response
  },
  (error) => {
    const store = useManagerStore()
    if (error.response?.status === 401) {
      store.clearAuth()
      ElMessage.error('登录状态已过期，请重新登录')
      router.push('/login')
    } else if (error.response?.status === 403) {
      ElMessage.error('无权限访问该页面')
    }
    return Promise.reject(error)
  },
)

export default instance
