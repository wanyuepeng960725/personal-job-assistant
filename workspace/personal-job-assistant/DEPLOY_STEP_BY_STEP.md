# 服务器部署详细操作步骤

> 按照以下步骤依次操作，每步完成后继续下一步

---

## 前置信息

请先准备好以下信息：

- [ ] **ECS 公网IP地址**：_______________ （例如：47.100.123.45）
- [ ] **服务器登录密码**：________________________
- [ ] **数据库密码**：__________________________ （建议强密码）
- [ ] **JWT Secret**：__________________________ （32位随机字符串）

---

## 第一步：连接服务器

### 1.1 确认信息
在本地终端执行，测试网络连接：
```bash
# 测试服务器是否可达
ping 你的ECS公网IP

# 例如
ping 47.100.123.45
```

### 1.2 连接服务器

**Windows 用户：**
1. 下载 PuTTY: https://www.putty.org/
2. 打开 PuTTY，填写：
   - **Host Name**: `你的ECS公网IP`
   - **Port**: `22`
3. 点击 "Open"
4. 输入 `root` 作为用户名
5. 输入密码（输入时不会显示，直接输入后回车）

**Mac/Linux 用户：**
```bash
ssh root@你的ECS公网IP
# 例如
ssh root@47.100.123.45
```

**连接成功标志：**
看到类似 `[root@i-xxxxx ~]#` 的提示符表示连接成功。

---

## 第二步：安装必要软件

**⚠️ 重要：在连接服务器后，依次执行以下命令**

### 2.1 更新系统
```bash
yum update -y
```
*等待完成（约3-5分钟）*

### 2.2 安装基础工具
```bash
yum install -y wget vim git
```
*等待完成（约1分钟）*

### 2.3 安装 Nginx
```bash
yum install -y epel-release
yum install -y nginx
```
*等待完成*

### 2.4 安装 Java 17
```bash
yum install -y java-17-openjdk
```
*等待完成*

### 2.5 安装 MySQL 8.0
```bash
# 下载 MySQL Yum 源
wget https://dev.mysql.com/get/mysql80-community-release-el7-11.noarch.rpm

# 安装 Yum 源
rpm -Uvh mysql80-community-release-el7-11.noarch.rpm

# 安装 MySQL 服务器
yum install -y mysql-community-server
```
*等待完成（约5分钟）*

### 2.6 启动 MySQL
```bash
# 启动 MySQL 服务
systemctl start mysqld

# 设置开机自启
systemctl enable mysqld

# 检查状态
systemctl status mysqld
```
*看到 `active (running)` 表示成功*

### 2.7 启动 Nginx
```bash
# 启动 Nginx
systemctl start nginx

# 设置开机自启
systemctl enable nginx

# 检查状态
systemctl status nginx
```

### 2.8 关闭防火墙（或放行端口）
```bash
# 停止防火墙
systemctl stop firewalld

# 禁用防火墙开机启动
systemctl disable firewalld

# 验证
systemctl status firewalld
```
*看到 `inactive (dead)` 表示成功*

---

## 第三步：配置 MySQL 数据库

### 3.1 获取 MySQL 初始密码
```bash
grep 'temporary password' /var/log/mysqld.log
```

**输出示例：**
```
2024-03-29T05:00:00.000000Z 6 [Note] [MY-010454] [Server] A temporary password is generated for root@localhost: Ab9#xyz&defGHI
```

**复制** `:` 后面的密码（如 `Ab9#xyz&defGHI`），记录下来。

### 3.2 登录 MySQL
```bash
mysql -u root -p
```
粘贴你刚才复制的初始密码，回车。

### 3.3 修改 root 密码
在 MySQL 命令行中执行（注意分号 `;`）：

```sql
-- 修改 root 密码（改成你自己的强密码）
ALTER USER 'root'@'localhost' IDENTIFIED BY 'YourStrongPassword123!';
```

**密码要求：**
- 至少8位
- 包含大小写字母、数字、特殊字符
- 例如：`MyDb@2024Pw`

### 3.4 创建应用数据库
```sql
-- 创建数据库
CREATE DATABASE job_assistant CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3.5 创建应用用户
```sql
-- 创建应用用户（修改密码）
CREATE USER 'job_user'@'localhost' IDENTIFIED BY 'JobUserPassword123!';

-- 授予权限
GRANT ALL PRIVILEGES ON job_assistant.* TO 'job_user'@'localhost';

-- 刷新权限
FLUSH PRIVILEGES;
```

### 3.6 退出 MySQL
```sql
EXIT;
```

### 3.7 导入数据库表结构

**在本地电脑执行：**
```bash
# 进入项目目录
cd /workspace/projects/workspace/personal-job-assistant

# 查看 SQL 文件是否存在
ls -la database/

# 上传 SQL 文件到服务器（修改IP）
scp database/job_assistant.sql root@你的ECS公网IP:/tmp/
```

**在服务器上执行：**
```bash
# 导入数据库
mysql -u job_user -p job_assistant < /tmp/job_assistant.sql

# 输入刚才设置的 job_user 密码
```

**验证导入：**
```bash
mysql -u job_user -p
# 输入密码

USE job_assistant;
SHOW TABLES;
```
*应该能看到数据库表列表*

```sql
EXIT;
```

---

## 第四步：部署后端服务

### 4.1 创建项目目录
```bash
# 创建目录
mkdir -p /opt/personal-job-assistant/backend
mkdir -p /opt/personal-job-assistant/frontend
mkdir -p /opt/personal-job-assistant/logs

# 验证
ls -la /opt/personal-job-assistant/
```

### 4.2 上传后端 JAR 包

**在本地电脑执行：**
```bash
# 进入后端目录
cd /workspace/projects/workspace/personal-job-assistant/backend

# 打包（跳过测试）
mvn clean package -DskipTests

# 等待打包完成，查看生成的文件
ls -lh target/*.jar

# 上传到服务器（修改IP）
scp target/personal-job-assistant-1.0.0.jar root@你的ECS公网IP:/opt/personal-job-assistant/backend/
```

*Windows 用户：使用 WinSCP 或 Xftp 工具上传*

### 4.3 创建后端配置文件
```bash
# 编辑配置文件
vim /opt/personal-job-assistant/backend/application-prod.yml
```

**按 `i` 进入插入模式，粘贴以下内容：**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/job_assistant?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: job_user
    password: JobUserPassword123!  # ← 改成你设置的密码
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true

jwt:
  secret: YourJWTSecretKeyHereAtLeast32CharactersLong  # ← 改成32位随机字符串
  expiration: 86400000
```

**保存：**
1. 按 `Esc`
2. 输入 `:wq`
3. 按回车

### 4.4 创建 Systemd 服务
```bash
# 编辑服务配置
vim /etc/systemd/system/job-assistant.service
```

**按 `i` 进入插入模式，粘贴以下内容：**

```ini
[Unit]
Description=Personal Job Assistant
After=network.target mysql.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/personal-job-assistant
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod -Xms512m -Xmx1024m /opt/personal-job-assistant/backend/personal-job-assistant-1.0.0.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**保存：**
1. 按 `Esc`
2. 输入 `:wq`
3. 按回车

### 4.5 启动后端服务
```bash
# 重新加载 systemd 配置
systemctl daemon-reload

# 设置开机自启
systemctl enable job-assistant

# 启动服务
systemctl start job-assistant

# 查看状态
systemctl status job-assistant
```

**成功标志：** 看到 `active (running)` 表示启动成功

### 4.6 查看后端日志（可选）
```bash
# 查看实时日志
journalctl -u job-assistant -f

# 按 Ctrl+C 退出日志查看
```

---

## 第五步：部署前端

### 5.1 本地构建前端

**在本地电脑执行：**
```bash
# 进入前端目录
cd /workspace/projects/workspace/personal-job-assistant/frontend

# 安装依赖
npm install

# 设置 API 地址（修改为你的 ECS 公网IP）
echo "VITE_API_BASE_URL=http://你的ECS公网IP:8080/api" > .env.production

# 构建生产包
npm run build
```
*等待构建完成（约2-3分钟）*

### 5.2 上传前端文件

**在本地执行：**
```bash
# 进入项目目录
cd /workspace/projects/workspace/personal-job-assistant/frontend

# 上传构建后的文件到服务器
scp -r dist/* root@你的ECS公网IP:/opt/personal-job-assistant/frontend/
```

*Windows 用户：使用 WinSCP 上传 `dist` 文件夹内所有文件*

### 5.3 验证前端文件
```bash
# 在服务器上检查
ls -la /opt/personal-job-assistant/frontend/
```
*应该能看到 `index.html` 等文件*

---

## 第六步：配置 Nginx

### 6.1 创建 Nginx 配置
```bash
# 编辑配置文件
vim /etc/nginx/conf.d/job-assistant.conf
```

**按 `i` 进入插入模式，粘贴以下内容：**

```nginx
server {
    listen 80;
    server_name _;  # 使用 _ 表示匹配所有域名和IP

    # 前端静态文件
    location / {
        root /opt/personal-job-assistant/frontend;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 后端API代理
    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 错误页面
    error_page 500 502 503 504 /50x.html;
    location = /50x.html {
        root /usr/share/nginx/html;
    }
}
```

**保存：**
1. 按 `Esc`
2. 输入 `:wq`
3. 按回车

### 6.2 测试 Nginx 配置
```bash
# 测试配置语法
nginx -t
```

**成功标志：** 看到 `syntax is ok` 和 `test is successful`

### 6.3 重载 Nginx
```bash
# 重载配置
nginx -s reload

# 或者重启服务
systemctl restart nginx
```

---

## 第七步：验证部署

### 7.1 测试网站访问

1. 打开浏览器
2. 访问：`http://你的ECS公网IP`
3. **应该能看到登录页面**

### 7.2 测试注册功能

1. 点击「注册」按钮
2. 填写：
   - 用户名：`test`
   - 邮箱：`test@test.com`
   - 密码：`Test@123`
3. 点击「注册」

**成功标志：** 提示注册成功并跳转到登录页

### 7.3 测试登录功能

1. 输入刚才注册的账号密码
2. 点击「登录」

**成功标志：** 跳转到仪表盘页面

---

## 第八步：检查服务状态

### 8.1 检查各服务
```bash
# 检查后端服务
systemctl status job-assistant

# 检查 Nginx
systemctl status nginx

# 检查 MySQL
systemctl status mysqld
```

**所有服务都应该显示 `active (running)`**

### 8.2 查看端口监听
```bash
# 检查端口占用
netstat -tlnp | grep -E ':(80|8080|3306)'
```

**应该看到：**
```
tcp        0      0 0.0.0.0:80              0.0.0.0:*               LISTEN      xxx/nginx
tcp        0      0 0.0.0.0:8080            0.0.0.0:*               LISTEN      xxx/java
tcp        0      0 0.0.0.0:3306            0.0.0.0:*               LISTEN      xxx/mysqld
```

---

## 第九步：日常管理

### 重启服务
```bash
# 重启后端
systemctl restart job-assistant

# 重启 Nginx
systemctl restart nginx

# 重启 MySQL
systemctl restart mysqld
```

### 查看日志
```bash
# 后端日志
journalctl -u job-assistant -f

# Nginx 访问日志
tail -f /var/log/nginx/access.log

# Nginx 错误日志
tail -f /var/log/nginx/error.log
```

### 查看系统资源
```bash
# CPU 和内存
top -c

# 磁盘使用
df -h

# 内存使用
free -h
```

---

## 常见问题排查

### 问题1：网站打不开

**检查：**
```bash
# 1. Nginx 是否运行
systemctl status nginx

# 2. 安全组是否放行80端口
# 去阿里云控制台 → ECS → 安全组 → 配置规则
# 确保有：端口80，授权对象 0.0.0.0/0

# 3. 检查前端文件是否存在
ls /opt/personal-job-assistant/frontend/index.html

# 4. 查看 Nginx 错误日志
tail /var/log/nginx/error.log
```

### 问题2：注册/登录失败

**检查：**
```bash
# 1. 后端是否运行
systemctl status job-assistant

# 2. 测试后端接口
curl http://localhost:8080/api/auth/login -X POST \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test"}'

# 3. 查看后端日志
journalctl -u job-assistant -n 50

# 4. 检查数据库连接
mysql -u job_user -p job_assistant -e "SHOW TABLES;"
```

### 问题3：数据库连接失败

**检查：**
```bash
# 1. MySQL 是否运行
systemctl status mysqld

# 2. 测试数据库连接
mysql -u job_user -p -h localhost

# 3. 检查配置文件中的密码是否正确
cat /opt/personal-job-assistant/backend/application-prod.yml

# 4. 查看后端日志中的详细错误
journalctl -u job-assistant -n 100
```

### 问题4：端口被占用

**检查：**
```bash
# 查看端口占用
netstat -tlnp | grep 8080

# 如果有其他进程占用，杀死它
kill -9 <进程ID>
```

---

## 部署完成检查清单

- [ ] 服务器连接成功
- [ ] MySQL 安装完成并启动
- [ ] 数据库 `job_assistant` 创建成功
- [ ] 数据库用户 `job_user` 创建成功
- [ ] 数据库表导入成功
- [ ] 后端 JAR 包上传成功
- [ ] 后端配置文件创建成功
- [ ] 后端服务启动成功（active running）
- [ ] 前端文件上传成功
- [ ] Nginx 配置文件创建成功
- [ ] Nginx 启动成功
- [ ] 网站可以访问（浏览器打开看到登录页）
- [ ] 注册功能测试成功
- [ ] 登录功能测试成功

**全部打钩后，部署完成！✅**

---

## 下一步（可选）

### 配置域名（推荐）
1. 购买域名（阿里云万网）
2. 域名解析指向 ECS 公网IP
3. 修改 Nginx 配置中的 `server_name` 为你的域名
4. 访问 `http://yourdomain.com`

### 配置 HTTPS（推荐）
1. 申请 SSL 证书（阿里云免费 DV 证书）
2. 下载证书到服务器 `/etc/nginx/ssl/`
3. 修改 Nginx 配置添加 443 端口
4. 访问 `https://yourdomain.com`

### 配置自动备份
```bash
# 创建备份脚本
vim /opt/personal-job-assistant/backup.sh

# 添加定时任务
echo "0 2 * * * root /opt/personal-job-assistant/backup.sh" >> /etc/crontab
```

---

**遇到问题随时问我！**
