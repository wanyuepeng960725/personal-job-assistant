import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    ElMessage.error(error.response?.data?.message || '请求失败')
    return Promise.reject(error)
  }
)

export default request

// 用户相关API
export const loginAPI = (data: { username: string; password: string }) => {
  return request.post('/auth/login', data)
}

export const registerAPI = (data: {
  username: string
  email: string
  password: string
}) => {
  return request.post('/auth/register', data)
}

export const getUserInfoAPI = () => {
  return request.get('/user/info')
}

// 面试相关API
export const getInterviewListAPI = (params: any) => {
  return request.get('/interview/list', { params })
}

export const createInterviewAPI = (data: any) => {
  return request.post('/interview/create', data)
}

export const updateInterviewAPI = (id: number, data: any) => {
  return request.put(`/interview/${id}`, data)
}

export const deleteInterviewAPI = (id: number) => {
  return request.delete(`/interview/${id}`)
}

export const completeInterviewAPI = (id: number, data: any) => {
  return request.post(`/interview/${id}/complete`, data)
}
