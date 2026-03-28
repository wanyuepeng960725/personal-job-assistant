# 个人求职助手 - 项目说明

## 📋 项目概述

这是一个基于 **Vue 3 + Spring Boot** 的求职进度管理系统，帮助求职者记录和管理面试信息。

---

## 🎯 已实现的功能

### 前端功能

1. ✅ **用户认证**
   - 用户登录
   - 用户注册
   - 退出登录

2. ✅ **求职进度管理**
   - 添加面试记录
   - 编辑面试信息
   - 完成面试（标记结果）
   - 删除面试记录
   - 搜索筛选（按公司名称、状态）
   - 分页显示

### 后端功能

- ✅ Spring Boot 项目结构已创建
- ✅ 数据库配置（MyBatis + MySQL）
- ✅ JWT 认证配置
- ✅ API 基础架构

---

## 📁 项目结构

```
personal-job-assistant/
├── frontend/                    # Vue 3 前端项目
│   ├── src/
│   │   ├── api/               # API 接口定义
│   │   │   └── index.ts       # 统一的axios封装和API方法
│   │   ├── assets/            # 静态资源
│   │   ├── components/        # 组件
│   │   ├── router/            # 路由配置
│   │   │   └── index.ts       # Vue Router 配置
│   │   ├── stores/            # 状态管理（Pinia）
│   │   ├── types/             # TypeScript 类型定义
│   │   │   └── index.ts       # 用户、面试等相关类型
│   │   ├── views/             # 页面组件
│   │   │   ├── Login.vue      # 登录页
│   │   │   ├── Register.vue   # 注册页
│   │   │   ├── Dashboard.vue  # 仪表盘（主布局）
│   │   │   └── Interview.vue  # 求职进度管理页
│   │   ├── App.vue            # 根组件
│   │   └── main.ts            # 入口文件
│   ├── index.html             # HTML 模板
│   ├── package.json           # 依赖配置
│   ├── tsconfig.json          # TypeScript 配置
│   ├── tsconfig.node.json     # Node TypeScript 配置
│   └── vite.config.ts         # Vite 配置
│
└── backend/                     # Spring Boot 后端项目
    ├── src/main/
    │   ├── java/com/job/assistant/
    │   │   └── PersonalJobAssistantApplication.java  # 主类
    │   └── resources/
    │       └── application.yml                        # 配置文件
    └── pom.xml                                           # Maven 配置
```

---

## 🚀 如何运行

### 前端运行

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产版本
npm run preview
```

前端运行在：`http://localhost:5173`

### 后端运行

```bash
cd backend

# 编译项目
mvn clean package

# 运行项目
java -jar target/personal-job-assistant-1.0.0.jar

# 或使用 Maven 直接运行
mvn spring-boot:run
```

后端运行在：`http://localhost:8080/api`

---

## 📊 数据库设计

### 用户表 (user)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 用户名 |
| email | VARCHAR(100) | 邮箱 |
| password | VARCHAR(255) | 密码（加密） |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

### 面试表 (interview)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID（外键） |
| company | VARCHAR(100) | 公司名称 |
| position | VARCHAR(100) | 职位 |
| interview_date | TIMESTAMP | 面试时间 |
| location | VARCHAR(255) | 面试地点 |
| status | VARCHAR(20) | 状态（PENDING/COMPLETED/PASSED/FAILED/CANCELLED） |
| result | VARCHAR(50) | 面试结果 |
| notes | TEXT | 备注 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

---

## 🔌 API 接口设计

### 认证接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/login` | 用户登录 |
| POST | `/auth/register` | 用户注册 |
| GET | `/user/info` | 获取用户信息 |

### 面试接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/interview/list` | 获取面试列表 |
| POST | `/interview/create` | 创建面试记录 |
| PUT | `/interview/{id}` | 更新面试记录 |
| DELETE | `/interview/{id}` | 删除面试记录 |
| POST | `/interview/{id}/complete` | 完成面试 |

---

## 📝 面试状态说明

| 状态值 | 说明 |
|--------|------|
| PENDING | 待面试 |
| COMPLETED | 已完成 |
| PASSED | 通过 |
| FAILED | 未通过 |
| CANCELLED | 已取消 |

---

## ⚙️ 配置说明

### 数据库配置

在 `backend/src/main/resources/application.yml` 中修改：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/job_assistant
    username: root
    password: your_password
```

### JWT 配置

```yaml
jwt:
  secret: your-secret-key-at-least-256-bits-long
  expiration: 86400000  # 24小时
```

---

## 🎨 技术栈

### 前端

- **框架**: Vue 3
- **语言**: TypeScript
- **路由**: Vue Router 4
- **状态管理**: Pinia
- **UI组件**: Element Plus
- **HTTP客户端**: Axios
- **构建工具**: Vite

### 后端

- **框架**: Spring Boot 3.2
- **语言**: Java 17
- **数据库**: MySQL
- **ORM**: MyBatis
- **认证**: JWT
- **构建工具**: Maven

---

## 📦 已安装的依赖

### 前端依赖

```json
{
  "vue": "^3.4.0",
  "vue-router": "^4.2.0",
  "pinia": "^2.1.0",
  "axios": "^1.6.0",
  "element-plus": "^2.4.0"
}
```

### 后端依赖

- Spring Boot Web
- Spring Boot Validation
- MyBatis
- MySQL Connector
- JWT (jjwt)
- Lombok

---

## 🔄 下一步开发计划

1. **完善后端代码**
   - 实现Controller层
   - 实现Service层
   - 实现Mapper层
   - 创建数据库表

2. **完善前端功能**
   - 集成后端API
   - 实现数据持久化
   - 添加数据校验
   - 优化用户体验

3. **添加新功能**
   - 数据统计图表
   - 面试提醒功能
   - 面试记录导出
   - 个人简历管理

---

## 💡 使用说明

1. **首次使用**：先注册账号
2. **登录**：使用注册的账号密码登录
3. **添加面试**：点击"添加面试"按钮，填写面试信息
4. **管理进度**：可以编辑、完成、删除面试记录
5. **搜索筛选**：按公司名称或状态筛选面试记录

---

## ⚠️ 注意事项

1. 数据库需要先创建：`CREATE DATABASE job_assistant;`
2. 修改 `application.yml` 中的数据库配置
3. 前端代理配置已设置，无需额外配置跨域
4. JWT密钥请修改为生产环境安全的密钥

---

## 📞 联系方式

如有问题，请在飞书上联系我！

---

**项目创建时间**: 2026-03-15
**版本**: v1.0.0
