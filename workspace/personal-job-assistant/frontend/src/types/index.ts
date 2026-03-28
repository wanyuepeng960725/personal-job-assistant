// 用户类型
export interface User {
  id: number
  username: string
  email: string
  createdAt: string
}

// 登录请求
export interface LoginRequest {
  username: string
  password: string
}

// 注册请求
export interface RegisterRequest {
  username: string
  email: string
  password: string
}

// 登录响应
export interface LoginResponse {
  token: string
  user: User
}

// 面试状态枚举
export enum InterviewStatus {
  PENDING = 'PENDING',        // 待面试
  COMPLETED = 'COMPLETED',    // 已完成
  PASSED = 'PASSED',          // 通过
  FAILED = 'FAILED',          // 未通过
  CANCELLED = 'CANCELLED'     // 已取消
}

// 面试记录
export interface Interview {
  id: number
  userId: number
  company: string
  position: string
  interviewDate: string
  location: string
  status: InterviewStatus
  result?: string
  notes?: string
  createdAt: string
  updatedAt: string
}

// 创建面试请求
export interface CreateInterviewRequest {
  company: string
  position: string
  interviewDate: string
  location: string
  notes?: string
}

// 更新面试请求
export interface UpdateInterviewRequest {
  company?: string
  position?: string
  interviewDate?: string
  location?: string
  status?: InterviewStatus
  result?: string
  notes?: string
}
