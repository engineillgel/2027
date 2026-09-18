import axios from 'axios'
const api = axios.create({ baseURL: 'http://localhost:8080/api' })
api.interceptors.request.use(cfg => {
  const token = localStorage.getItem('token')
  if(token) cfg.headers.Authorization = `Bearer ${token}`
  return cfg
})
// 401（token 失效/过期）→ 清 token + 派发全局事件，由 store 处理并弹出登录框
api.interceptors.response.use(
  res => res,
  err => {
    if (err.response && err.response.status === 401) {
      localStorage.removeItem('token')
      window.dispatchEvent(new CustomEvent('auth:expired'))
    }
    return Promise.reject(err)
  }
)
export default api
