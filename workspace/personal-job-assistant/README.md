# 个人求职助手

一个简单高效的个人求职面试记录管理系统，帮助你管理求职过程中的面试安排和结果。

## 🌟 功能特性

- ✅ **用户认证**：注册、登录、JWT Token 鉴权
- ✅ **面试管理**：创建、查看、更新面试记录
- ✅ **状态跟踪**：PENDING（待面试）、COMPLETED（已完成）、PASSED（通过）、FAILED（未通过）、CANCELLED（已取消）
- ✅ **信息记录**：公司名称、职位、面试时间、地点、面试结果、备注
- ✅ **响应式设计**：支持移动端和桌面端
- ✅ **现代化技术栈**：Vue.js + Spring Boot + MySQL

## 🏗️ 技术架构

### 前端
- Vue.js 3
- Vite
- Element Plus
- Axios
- Vue Router

### 后端
- Spring Boot 3.2.0
- Spring Security
- MyBatis
- MySQL 8.0
- JWT (jsonwebtoken 0.12.3)

## 🚀 快速开始

### 前置要求
- Node.js 18+
- Java 21+
- MySQL 8.0+
- Maven 3.8+

### 本地开发

#### 1. 克隆项目
```bash
git clone https://github.com/wanyuepeng960725/personal-job-assistant.git
cd personal-job-assistant
```

#### 2. 配置数据库
```bash
mysql -u root -p
```

```sql
CREATE DATABASE job_assistant CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'jobapp'@'localhost' IDENTIFIED BY 'StrongPassword123!';
GRANT ALL PRIVILEGES ON job_assistant.* TO 'jobapp'@'localhost';
FLUSH PRIVILEGES;
```

#### 3. 启动后端
```bash
cd backend
mvn spring-boot:run
```

后端将在 `http://localhost:8080/api` 启动

#### 4. 启动前端
```bash
cd frontend
npm install
npm run dev
```

前端将在 `http://localhost:5173` 启动

## 📱 使用说明

### 注册
1. 访问应用首页
2. 点击"注册"按钮
3. 填写用户名、邮箱、密码
4. 提交注册

### 登录
1. 使用注册的用户名/邮箱和密码登录
2. 系统会生成 JWT Token
3. Token 有效期为 24 小时

### 管理面试记录
1. 点击"新增面试"创建新的面试记录
2. 填写公司名称、职位、面试时间等信息
3. 根据面试结果更新状态
4. 添加面试备注和结果反馈

## 🔧 API 文档

### 认证接口

#### 注册
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "string",
  "email": "string",
  "password": "string"
}
```

#### 登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}
```

### 面试接口

#### 获取面试列表
```
GET /api/interview
Authorization: Bearer <token>
```

#### 创建面试记录
```
POST /api/interview
Authorization: Bearer <token>
Content-Type: application/json

{
  "company": "string",
  "position": "string",
  "interviewDate": "ISO-8601 datetime",
  "location": "string",
  "status": "PENDING",
  "result": "string",
  "notes": "string"
}
```

#### 更新面试记录
```
PUT /api/interview/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "COMPLETED",
  "result": "通过面试",
  "notes": "面试官很满意，等待HR进一步沟通"
}
```

#### 删除面试记录
```
DELETE /api/interview/{id}
Authorization: Bearer <token>
```

## 🔐 环境配置

### 后端配置文件
`backend/src/main/resources/application.yml`

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/job_assistant
    username: jobapp
    password: StrongPassword123!

jwt:
  secret: your-secret-key-at-least-256-bits-long
  expiration: 86400000  # 24 hours
```

### 前端配置文件
`frontend/.env`

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## 🌐 部署指南

### 方式一：阿里云部署（推荐）

详细部署指南请参考项目中的部署文档，或查看：
[阿里云部署完整指南](#)

### 方式二：Docker 部署

```bash
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d
```

### 方式三：云平台部署

- **前端**：Vercel / Netlify（免费）
- **后端**：Render / Railway / 阿里云云服务器
- **数据库**：PlanetScale / 阿里云 RDS

## 📊 数据库设计

### 用户表 (user)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 用户ID（主键） |
| username | VARCHAR(50) | 用户名（唯一） |
| email | VARCHAR(100) | 邮箱（唯一） |
| password | VARCHAR(255) | 密码（加密） |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 面试记录表 (interview)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 记录ID（主键） |
| user_id | BIGINT | 用户ID（外键） |
| company | VARCHAR(100) | 公司名称 |
| position | VARCHAR(100) | 职位名称 |
| interview_date | DATETIME | 面试时间 |
| location | VARCHAR(255) | 面试地点 |
| status | VARCHAR(20) | 面试状态 |
| result | TEXT | 面试结果 |
| notes | TEXT | 备注 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

## 🛡️ 安全说明

- 密码使用 BCrypt 加密存储
- JWT Token 用于身份验证
- CORS 配置防止跨域攻击
- SQL 注入防护（MyBatis 预编译）
- XSS 防护

## 📝 开发计划

- [ ] 添加面试提醒功能
- [ ] 支持上传面试资料（简历、Offer等）
- [ ] 数据统计和报表
- [ ] 面试经验笔记
- [ ] 导出数据功能
- [ ] 多语言支持

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证

## 👨‍💻 作者

[wanyuepeng960725](https://github.com/wanyuepeng960725)

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者！

---

**如有问题，请提交 Issue 或联系作者**
