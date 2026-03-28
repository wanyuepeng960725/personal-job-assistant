# 阿里云部署 - 执行清单

> 本文档告诉你**每一步具体要做什么**，按顺序执行即可。

---

## 阶段一：购买阿里云资源（30分钟）

### 第1步：购买 ECS 服务器

**做什么：** 登录阿里云，购买一台云服务器

**具体操作：**
1. 访问 https://ecs.console.aliyun.com
2. 点击「创建实例」
3. 按以下配置选择：

| 配置项 | 选择 |
|--------|------|
| 地域 | 华东1（杭州）或 华北2（北京） |
| 实例规格 | **2核4G** (ecs.t6-c1m2.large) |
| 操作系统 | **CentOS 7.9** (64位) |
| 系统盘 | **40GB** SSD |
| 带宽 | **按量付费** 或 **1-5Mbps** |
| 购买时长 | 1个月/1年 |

4. 设置登录密码（**保存好密码**）
5. 点击「确认订单」并支付
6. 记录**公网IP地址**（例如：47.100.123.45）

---

### 第2步：配置安全组（放行端口）

**做什么：** 让外部可以访问你的网站

**具体操作：**
1. 在 ECS 控制台找到「安全组」
2. 点击「配置规则」
3. 添加以下「入方向」规则：

| 端口 | 用途 | 授权对象 |
|------|------|----------|
| 22 | SSH远程连接 | 0.0.0.0/0 |
| 80 | HTTP网站访问 | 0.0.0.0/0 |
| 443 | HTTPS网站访问 | 0.0.0.0/0 |
| 8080 | 后端API（可选） | 0.0.0.0/0 |

**操作路径：** 安全组 → 配置规则 → 手动添加 → 按上表填写 → 保存

---

### 第3步：购买域名（可选）

**做什么：** 让网站可以通过域名访问（如 www.yourname.com）

**具体操作：**
1. 访问 https://wanwang.aliyun.com
2. 搜索你想要的域名（如 yourname.com）
3. 点击「加入清单」→「立即结算」
4. 完成实名认证（**必须**）
5. 在「域名解析」中添加记录：
   - 记录类型：A
   - 主机记录：@
   - 记录值：你的ECS公网IP

---

## 阶段二：连接服务器并安装环境（20分钟）

### 第4步：连接服务器

**做什么：** 用电脑连接到购买的云服务器

**Windows 用户：**
1. 下载 PuTTY：https://www.putty.org/
2. 打开 PuTTY，填写：
   - Host Name: `root@你的ECS_IP`
   - Port: `22`
3. 点击 Open，输入密码登录

**Mac/Linux 用户：**
打开终端，执行：
```bash
ssh root@你的ECS_IP
# 例如：ssh root@47.100.123.45
```
输入密码登录

---

### 第5步：安装必要软件

**做什么：** 在服务器上安装 Java、MySQL、Nginx

**登录服务器后，依次执行以下命令：**

```bash
# 1. 更新系统
yum update -y

# 2. 安装基本工具
yum install -y wget vim git

# 3. 安装 Nginx
yum install -y epel-release
yum install -y nginx

# 4. 安装 Java 17
yum install -y java-17-openjdk

# 5. 安装 MySQL 8.0
wget https://dev.mysql.com/get/mysql80-community-release-el7-11.noarch.rpm
rpm -Uvh mysql80-community-release-el7-11.noarch.rpm
yum install -y mysql-community-server

# 6. 启动 MySQL
systemctl start mysqld
systemctl enable mysqld

# 7. 启动 Nginx
systemctl start nginx
systemctl enable nginx

# 8. 关闭防火墙（或放行端口）
systemctl stop firewalld
systemctl disable firewalld
```

**等待全部执行完成...**（约5-10分钟）

---

### 第6步：配置数据库

**做什么：** 创建数据库和用户

**执行以下命令获取 MySQL 初始密码：**
```bash
grep 'temporary password' /var/log/mysqld.log
```
复制显示的密码（类似：`Ab9#xyz&def`）

**执行以下命令登录 MySQL：**
```bash
mysql -u root -p
# 输入上面复制的密码
```

**在 MySQL 命令行中执行以下 SQL：**
```sql
-- 1. 修改 root 密码（把 YourPassword 换成你的强密码）
ALTER USER 'root'@'localhost' IDENTIFIED BY 'YourPassword123!';

-- 2. 创建数据库
CREATE DATABASE job_assistant CHARACTER SET utf8mb4;

-- 3. 创建应用用户
CREATE USER 'job_user'@'localhost' IDENTIFIED BY 'JobPassword123!';
GRANT ALL ON job_assistant.* TO 'job_user'@'localhost';
FLUSH PRIVILEGES;

-- 4. 退出
EXIT;
```

**记录以下信息：**
- 数据库名：`job_assistant`
- 用户名：`job_user`
- 密码：`JobPassword123!`

---

## 阶段三：部署后端服务（15分钟）

### 第7步：创建项目目录

**做什么：** 在服务器上创建存放代码的文件夹

**执行：**
```bash
mkdir -p /opt/personal-job-assistant/backend
mkdir -p /opt/personal-job-assistant/frontend
mkdir -p /opt/personal-job-assistant/logs
```

---

### 第8步：上传后端代码

**做什么：** 把本地打包好的 JAR 文件传到服务器

**在本地电脑（Mac/Linux）执行：**
```bash
cd /workspace/projects/workspace/personal-job-assistant/backend
mvn clean package -DskipTests

# 上传到服务器（把 IP 换成你的）
scp target/personal-job-assistant-1.0.0.jar root@你的ECS_IP:/opt/personal-job-assistant/backend/
```

**Windows 用户：**
使用 WinSCP 或 Xftp 工具上传 JAR 文件到 `/opt/personal-job-assistant/backend/`

---

### 第9步：创建后端配置文件

**做什么：** 配置数据库连接信息

**在服务器上执行：**
```bash
vim /opt/personal-job-assistant/backend/application-prod.yml
```

**粘贴以下内容（修改密码为你设置的）：**
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/job_assistant?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: job_user
    password: JobPassword123!  # ← 改成你设置的密码
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true

jwt:
  secret: YourJWTSecretKeyHereAtLeast32CharactersLong  # ← 改成随机字符串
  expiration: 86400000
```

**保存：** 按 `Esc` → 输入 `:wq` → 回车

---

### 第10步：创建启动脚本

**做什么：** 让后端服务可以方便地启动和停止

**执行：**
```bash
vim /etc/systemd/system/job-assistant.service
```

**粘贴以下内容：**
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

[Install]
WantedBy=multi-user.target
```

**保存并启动服务：**
```bash
# 重新加载配置
systemctl daemon-reload

# 设置开机自启
systemctl enable job-assistant

# 启动服务
systemctl start job-assistant

# 查看状态
systemctl status job-assistant
```

**看到 `active (running)` 表示启动成功！**

---

## 阶段四：部署前端（10分钟）

### 第11步：本地构建前端

**做什么：** 把前端代码打包成可部署的文件

**在本地电脑执行：**
```bash
cd /workspace/projects/workspace/personal-job-assistant/frontend

# 安装依赖
npm install

# 设置 API 地址（把 IP 换成你的 ECS 公网IP）
echo "VITE_API_BASE_URL=http://你的ECS_IP:8080/api" > .env.production

# 构建
npm run build
```

**等待构建完成...**（约2-3分钟）

---

### 第12步：上传前端文件

**做什么：** 把打包好的前端文件传到服务器

**在本地执行：**
```bash
cd /workspace/projects/workspace/personal-job-assistant/frontend

# 上传到服务器（把 IP 换成你的）
scp -r dist/* root@你的ECS_IP:/opt/personal-job-assistant/frontend/
```

**Windows 用户：** 用 WinSCP 上传 `dist` 文件夹内所有文件到 `/opt/personal-job-assistant/frontend/`

---

## 阶段五：配置 Nginx（10分钟）

### 第13步：配置网站

**做什么：** 让 Nginx 可以访问前端和后端

**在服务器上执行：**
```bash
vim /etc/nginx/conf.d/job-assistant.conf
```

**粘贴以下内容：**
```nginx
server {
    listen 80;
    server_name _;  # 使用 _ 表示匹配所有域名

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
    }
}
```

**保存并重载 Nginx：**
```bash
# 测试配置
nginx -t

# 重载配置
nginx -s reload
```

**显示 `syntax is ok` 和 `test is successful` 表示成功！**

---

## 阶段六：验证部署（5分钟）

### 第14步：浏览器访问

**做什么：** 测试网站是否正常

**操作：**
1. 打开浏览器
2. 访问：`http://你的ECS公网IP`
3. 应该能看到登录页面

**测试注册功能：**
1. 点击「注册」
2. 填写用户名、邮箱、密码
3. 点击注册
4. 如果能成功注册并跳转到登录页，说明部署成功！

---

### 第15步：检查各服务状态

**在服务器上执行以下命令检查：**

```bash
# 检查后端服务
systemctl status job-assistant

# 检查 Nginx
systemctl status nginx

# 检查 MySQL
systemctl status mysqld

# 查看后端日志（如果出错）
tail -f /opt/personal-job-assistant/logs/backend.log
```

---

## 部署完成！✅

### 你的应用信息

| 项目 | 地址 |
|------|------|
| 网站访问 | `http://你的ECS公网IP` |
| 后端 API | `http://你的ECS公网IP:8080/api` |

### 日常管理命令

```bash
# 重启后端
systemctl restart job-assistant

# 查看后端日志
tail -f /opt/personal-job-assistant/logs/backend.log

# 重启 Nginx
nginx -s reload

# 查看 Nginx 错误日志
tail -f /var/log/nginx/error.log
```

---

## 常见问题

### 1. 浏览器打不开网站

**检查：**
```bash
# 1. Nginx 是否运行
systemctl status nginx

# 2. 前端文件是否存在
ls /opt/personal-job-assistant/frontend/

# 3. 安全组是否放行80端口
# 去阿里云控制台检查安全组规则
```

### 2. 注册/登录失败

**检查：**
```bash
# 1. 后端是否运行
systemctl status job-assistant

# 2. 数据库连接是否正常
curl http://localhost:8080/api/auth/login -X POST

# 3. 查看后端错误日志
tail -f /opt/personal-job-assistant/logs/backend.log
```

### 3. 忘记数据库密码

**重置：**
```bash
# 1. 停止 MySQL
systemctl stop mysqld

# 2. 跳过授权启动
mysqld_safe --skip-grant-tables &

# 3. 无密码登录修改
mysql -u root
ALTER USER 'root'@'localhost' IDENTIFIED BY '新密码';
FLUSH PRIVILEGES;
EXIT;

# 4. 重启 MySQL
systemctl restart mysqld
```

---

## 下一步（可选）

### 配置 HTTPS（推荐）

**做什么：** 让网站使用 https:// 访问，更安全

**步骤：**
1. 在阿里云 SSL 控制台申请免费证书
2. 下载证书文件到服务器 `/etc/nginx/ssl/`
3. 修改 Nginx 配置添加 443 端口和 SSL 证书
4. 访问 `https://你的域名`

### 配置域名

**做什么：** 用域名代替 IP 地址访问

**步骤：**
1. 购买域名（阿里云万网）
2. 域名解析到 ECS 公网IP
3. 修改 Nginx 配置中的 `server_name`
4. 等待 DNS 生效（10分钟-24小时）

---

**祝部署顺利！有问题查看详细文档：** `DEPLOY.md`
