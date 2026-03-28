# 🚀 项目启动指南

## 前置条件

1. **Node.js** >= 16.x
2. **Java** >= 17
3. **Maven** >= 3.x
4. **MySQL** >= 5.7 或 8.0

---

## 📦 安装步骤

### 1. 创建数据库

```sql
-- 连接MySQL
mysql -u root -p

-- 执行建表脚本
source /workspace/projects/workspace/personal-job-assistant/backend/src/main/resources/sql/schema.sql
```

或者直接在MySQL客户端中执行 `schema.sql` 文件。

### 2. 配置数据库

编辑 `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/job_assistant?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root           # 修改为你的MySQL用户名
    password: your_password  # 修改为你的MySQL密码
```

### 3. 配置JWT密钥

在 `backend/src/main/resources/application.yml` 中：

```yaml
jwt:
  secret: your-secret-key-at-least-256-bits-long  # 修改为安全的密钥
  expiration: 86400000  # 24小时（可调整）
```

---

## 🏃 运行步骤

### 方式一：分别启动（推荐开发时使用）

#### 启动后端

```bash
cd /workspace/projects/workspace/personal-job-assistant/backend

# 方式A：使用Maven直接运行
mvn spring-boot:run

# 方式B：编译后运行JAR包
mvn clean package
java -jar target/personal-job-assistant-1.0.0.jar
```

后端启动后访问：`http://localhost:8080/api`

#### 启动前端

```bash
cd /workspace/projects/workspace/personal-job-assistant/frontend

# 安装依赖（如果还没安装）
npm install

# 启动开发服务器
npm run dev
```

前端启动后访问：`http://localhost:5173`

---

### 方式二：后台启动（推荐生产环境）

#### 后台启动后端

```bash
cd /workspace/projects/workspace/personal-job-assistant/backend

# 使用nohup后台启动
nohup mvn spring-boot:run > backend.log 2>&1 &

# 查看日志
tail -f backend.log
```

#### 后台启动前端

```bash
cd /workspace/projects/workspace/personal-job-assistant/frontend

# 使用nohup后台启动
nohup npm run dev > frontend.log 2>&1 &

# 查看日志
tail -f frontend.log
```

---

## ✅ 验证启动

### 检查后端

```bash
# 检查端口8080是否监听
netstat -an | grep 8080

# 或者测试API
curl http://localhost:8080/api
```

### 检查前端

```bash
# 检查端口5173是否监听
netstat -an | grep 5173

# 或在浏览器访问
http://localhost:5173
```

---

## 🎯 快速测试

### 1. 注册用户

访问 `http://localhost:5173/register`

填写：
- 用户名：testuser
- 邮箱：test@example.com
- 密码：123456

点击"注册"按钮

### 2. 登录

访问 `http://localhost:5173/login`

使用注册的账号密码登录

### 3. 添加面试记录

登录后，进入"求职进度"页面

点击"添加面试"按钮，填写：
- 公司名称：字节跳动
- 职位：前端工程师
- 面试时间：选择一个时间
- 面试地点：北京市海淀区

点击"确定"

---

## 📡 API接口文档

### 认证接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录 |

### 用户接口

| 方法 | 路径 | 说明 | 需要Token |
|------|------|------|-----------|
| GET | `/api/user/info` | 获取当前用户信息 | ✅ |
| GET | `/api/user/{id}` | 获取用户信息 | ❌ |

### 面试接口

| 方法 | 路径 | 说明 | 需要Token |
|------|------|------|-----------|
| GET | `/api/interview/list` | 获取面试列表 | ✅ |
| GET | `/api/interview/{id}` | 获取面试详情 | ❌ |
| POST | `/api/interview/create` | 创建面试记录 | ✅ |
| PUT | `/api/interview/{id}` | 更新面试记录 | ❌ |
| POST | `/api/interview/{id}/complete` | 完成面试 | ❌ |
| DELETE | `/api/interview/{id}` | 删除面试记录 | ❌ |

---

## 🔍 常见问题

### 1. 后端启动失败

**问题：** 无法连接数据库

**解决：**
- 检查MySQL是否启动
- 检查数据库配置是否正确
- 检查数据库是否已创建

**问题：** 端口被占用

**解决：**
```bash
# 查找占用8080端口的进程
lsof -i:8080

# 杀掉进程
kill -9 <PID>
```

### 2. 前端启动失败

**问题：** 端口5173被占用

**解决：**
```bash
# 修改vite.config.ts中的端口配置
server: {
  port: 5174  # 改为其他端口
}
```

**问题：** 依赖安装失败

**解决：**
```bash
# 清除缓存重新安装
rm -rf node_modules package-lock.json
npm install
```

### 3. 跨域问题

如果遇到CORS错误，检查：
- 后端CORS配置是否正确
- 前端请求地址是否正确（/api）
- Token是否正确传递

---

## 📝 测试账号

系统会自动创建一个测试账号：

- 用户名：testuser
- 邮箱：test@example.com
- 密码：123456

密码已MD5加密，可在数据库中查看。

---

## 🎉 完成后

现在你可以：
1. 注册新用户
2. 登录系统
3. 添加、编辑、删除面试记录
4. 搜索和筛选面试记录
5. 标记面试完成状态

享受使用个人求职助手！ 🚀
