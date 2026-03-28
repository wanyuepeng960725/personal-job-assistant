# ❓ 常见问题 FAQ

## 🚀 安装和配置问题

### Q1: 如何安装项目依赖？

**A:**

#### 前端依赖安装：
```bash
cd /workspace/projects/workspace/personal-job-assistant/frontend
npm install
```

#### 后端依赖安装：
```bash
cd /workspace/projects/workspace/personal-job-assistant/backend
mvn clean install
```

---

### Q2: 如何配置数据库？

**A:**

编辑 `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/job_assistant?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root           # 修改为你的MySQL用户名
    password: your_password  # 修改为你的MySQL密码
```

---

### Q3: 如何创建数据库？

**A:**

#### 方法1：使用命令行
```bash
mysql -u root -p
source /workspace/projects/workspace/personal-job-assistant/backend/src/main/resources/sql/schema.sql
```

#### 方法2：使用MySQL客户端
打开MySQL客户端，执行 `schema.sql` 文件中的SQL语句。

---

## 🔧 运行问题

### Q4: 后端启动失败，报错"Communications link failure"

**A:** 这个错误表示无法连接到MySQL数据库。

**解决方法：**
1. 检查MySQL是否启动：`sudo systemctl status mysql`
2. 启动MySQL：`sudo systemctl start mysql`
3. 检查数据库配置是否正确（用户名、密码、端口）
4. 检查防火墙是否允许3306端口

---

### Q5: 前端启动失败，报错"Port 5173 is already in use"

**A:** 端口5173已被占用。

**解决方法：**
```bash
# 查找占用端口的进程
lsof -i:5173

# 杀掉进程
kill -9 <PID>

# 或者修改端口
# 编辑 frontend/vite.config.ts
server: {
  port: 5174  # 改为其他端口
}
```

---

### Q6: 后端启动失败，报错"Address already in use"

**A:** 端口8080已被占用。

**解决方法：**
```bash
# 查找占用端口的进程
lsof -i:8080

# 杀掉进程
kill -9 <PID>

# 或者修改端口
# 编辑 backend/src/main/resources/application.yml
server:
  port: 8081  # 改为其他端口
```

---

## 🔐 认证问题

### Q7: 登录时提示"用户名或密码错误"

**A:** 可能的原因：

1. **用户名不存在** - 检查用户名是否正确，区分大小写
2. **密码错误** - 检查密码是否正确
3. **数据库中没有该用户** - 先注册一个账号

**解决方法：**
```bash
# 查看数据库中的用户
mysql -u root -p
USE job_assistant;
SELECT * FROM user;
```

---

### Q8: 登录后提示"Token无效或已过期"

**A:** Token已过期或格式错误。

**解决方法：**
1. 重新登录
2. 检查JWT配置中的 `expiration` 值（单位：毫秒）
3. 清除浏览器缓存和localStorage

---

### Q9: 如何修改Token过期时间？

**A:**

编辑 `backend/src/main/resources/application.yml`:

```yaml
jwt:
  expiration: 86400000  # 24小时（单位：毫秒）
```

可以修改为：
- 3600000 - 1小时
- 86400000 - 24小时（默认）
- 604800000 - 7天

---

## 📊 数据问题

### Q10: 如何重置数据库？

**A:**

```bash
# 删除数据库
mysql -u root -p
DROP DATABASE job_assistant;

# 重新创建
source /workspace/projects/workspace/personal-job-assistant/backend/src/main/resources/sql/schema.sql
```

⚠️ **注意：这会删除所有数据！**

---

### Q11: 如何备份数据库？

**A:**

```bash
# 备份数据库
mysqldump -u root -p job_assistant > backup_$(date +%Y%m%d).sql

# 恢复数据库
mysql -u root -p job_assistant < backup_20260315.sql
```

---

### Q12: 面试记录无法保存，报错"Required field is missing"

**A:** 必填字段未填写。

**必填字段：**
- 公司名称 ✅
- 职位 ✅
- 面试时间 ✅
- 面试地点 ✅

**解决方法：** 确保所有必填字段都已填写。

---

## 🌐 网络问题

### Q13: 前端无法访问后端API，报错"Network Error"

**A:** 跨域或网络连接问题。

**检查清单：**
1. 后端是否已启动：`curl http://localhost:8080/api`
2. 前端代理配置是否正确（`vite.config.ts`）
3. CORS配置是否正确（`CorsConfig.java`）
4. 浏览器控制台查看具体错误信息

---

### Q14: 如何配置反向代理？

**A:**

#### 使用Nginx：

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态资源
    location / {
        root /path/to/frontend/dist;
        try_files $uri $uri/ /index.html;
    }

    # 后端API代理
    location /api {
        proxy_pass http://localhost:8080/api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 🎨 界面问题

### Q15: 页面样式显示异常

**A:** Element Plus未正确加载。

**解决方法：**
1. 检查 `main.ts` 中是否正确导入Element Plus
2. 重新安装依赖：`npm install element-plus`
3. 清除缓存并重新构建：
```bash
rm -rf node_modules dist
npm install
npm run build
```

---

### Q16: 日期选择器显示的日期格式不正确

**A:** 需要配置日期格式。

**解决方法：**

在Vue组件中：
```typescript
import { ref } from 'vue'
import dayjs from 'dayjs'

// 使用dayjs格式化日期
const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}
```

---

## 🐛 错误处理

### Q17: 如何查看后端日志？

**A:**

```bash
# 如果使用start.sh启动
tail -f /workspace/projects/workspace/personal-job-assistant/backend.log

# 如果使用Maven直接运行
# 日志会输出到控制台
```

### Q18: 如何查看前端日志？

**A:**

```bash
# 如果使用start.sh启动
tail -f /workspace/projects/workspace/personal-job-assistant/frontend.log

# 否则在浏览器控制台查看
# 按F12打开开发者工具，切换到Console标签
```

---

### Q19: 接口返回500错误

**A:** 服务器内部错误。

**排查步骤：**
1. 查看后端日志，找到具体错误信息
2. 检查数据库连接是否正常
3. 检查数据库表结构是否正确
4. 检查请求参数格式是否正确

---

## 📱 移动端问题

### Q20: 在手机上无法正常访问

**A:** 响应式适配问题。

**解决方法：**
1. 在 `index.html` 中添加viewport meta标签（已完成）
2. 确保CSS使用相对单位（rem、vw、vh）
3. 测试不同屏幕尺寸的显示效果

---

## 🔐 安全问题

### Q21: 如何提高密码安全性？

**A:** 当前使用MD5加密，建议使用BCrypt。

**修改步骤：**

1. 添加依赖（`pom.xml`）：
```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

2. 修改 `PasswordUtil.java`：
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class String encrypt(String password) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    return encoder.encode(password);
}
```

---

### Q22: 如何防止SQL注入？

**A:** 当前使用MyBatis参数化查询，已基本防止SQL注入。

**安全建议：**
1. 继续使用参数化查询（#{param}）
2. 避免使用字符串拼接SQL（${param}）
3. 定期更新依赖库

---

## 🚀 性能问题

### Q23: 面试列表加载很慢

**A:** 可能是数据量大或查询效率低。

**优化方法：**
1. 添加索引（已在schema.sql中添加）
2. 使用分页查询（已实现）
3. 考虑添加缓存（Redis）
4. 优化SQL查询语句

---

### Q24: 前端构建很慢

**A:** 可能是依赖太多或配置问题。

**优化方法：**
1. 清除缓存：`rm -rf node_modules package-lock.json`
2. 重新安装：`npm install`
3. 使用缓存构建：`npm run build -- --cache`
4. 考虑使用CDN加速依赖

---

## 📞 获取帮助

### 如果以上FAQ没有解决你的问题：

1. 查看项目文档
   - `PROJECT_GUIDE.md` - 详细说明
   - `START_GUIDE.md` - 启动指南
   - `INTEGRATION_TEST.md` - 测试指南

2. 查看错误日志
   - 后端日志：`backend.log`
   - 前端日志：浏览器控制台

3. 在飞书上反馈问题

---

## 💡 最佳实践

### 开发环境
- 使用热重载（Vite Dev Server）
- 开启日志输出
- 使用开发数据库

### 生产环境
- 使用生产数据库
- 关闭详细日志
- 启用HTTPS
- 配置CDN
- 定期备份数据
- 监控系统性能

---

**最后更新**: 2026-03-15
