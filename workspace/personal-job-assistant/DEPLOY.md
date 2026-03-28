# 阿里云部署指南 - 个人求职助手

本文档详细介绍如何将个人求职助手项目部署到阿里云服务器。

## 部署架构

```
用户请求 → 域名 → Nginx (前端静态文件 / 反向代理到后端)
                     ↓
            ┌────────┴────────┐
            ↓                 ↓
      前端 (Vue.js)      后端 (Spring Boot)
      端口: 80/443        端口: 8080
                               ↓
                         MySQL 数据库
                         端口: 3306
```

## 方案选择

| 方案 | 适用场景 | 月费用估算 |
|------|----------|-----------|
| **方案A: ECS单机** | 个人项目、测试环境 | ￥50-100 |
| **方案B: ECS+RDS** | 生产环境、数据重要 | ￥150-300 |
| **方案C: 容器化** | 需要弹性扩展 | ￥100-200 |

本文以 **方案A (ECS单机部署)** 为例，其他方案思路类似。

---

## 一、准备工作

### 1.1 购买阿里云 ECS 服务器

1. 登录阿里云控制台: https://ecs.console.aliyun.com
2. 创建实例，推荐配置：
   - **地域**: 选择离用户最近的（如华北2/华东1）
   - **实例规格**: 2核4G（ecs.t6-c1m2.large）
   - **操作系统**: CentOS 7.9 / Ubuntu 22.04 LTS
   - **系统盘**: 40GB SSD
   - **带宽**: 1-5Mbps（按量付费或固定带宽）
   - **安全组**: 开放端口 22(SSH), 80(HTTP), 443(HTTPS), 8080(后端)

3. 设置 root 密码或配置密钥对
4. 记录公网 IP 地址

### 1.2 购买域名（可选）

1. 阿里云域名注册: https://wanwang.aliyun.com
2. 实名认证（必须）
3. 解析域名到 ECS IP

---

## 二、服务器环境配置

### 2.1 连接服务器

```bash
# 使用 SSH 连接
ssh root@<你的ECS公网IP>

# 示例
ssh root@47.100.123.45
```

### 2.2 安装基础软件

**CentOS 7.x:**
```bash
# 更新系统
yum update -y

# 安装必要工具
yum install -y wget vim git

# 安装 Nginx
yum install -y epel-release
yum install -y nginx

# 安装 Java 17
yum install -y java-17-openjdk java-17-openjdk-devel

# 安装 MySQL 8.0
wget https://dev.mysql.com/get/mysql80-community-release-el7-11.noarch.rpm
rpm -Uvh mysql80-community-release-el7-11.noarch.rpm
yum install -y mysql-community-server

# 安装 Node.js 18 (用于构建前端)
curl -fsSL https://rpm.nodesource.com/setup_18.x | bash -
yum install -y nodejs
```

**Ubuntu 22.04:**
```bash
# 更新系统
apt update && apt upgrade -y

# 安装必要工具
apt install -y wget vim git

# 安装 Nginx
apt install -y nginx

# 安装 Java 17
apt install -y openjdk-17-jdk

# 安装 MySQL 8.0
apt install -y mysql-server-8.0

# 安装 Node.js 18
curl -fsSL https://deb.nodesource.com/setup_18.x | bash -
apt install -y nodejs
```

### 2.3 配置防火墙

```bash
# CentOS
systemctl stop firewalld
systemctl disable firewalld

# 或放行端口
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --permanent --add-port=443/tcp
firewall-cmd --permanent --add-port=8080/tcp
firewall-cmd --reload
```

---

## 三、数据库部署

### 3.1 启动 MySQL

```bash
# 启动服务
systemctl start mysqld
systemctl enable mysqld

# 查看初始密码（CentOS）
grep 'temporary password' /var/log/mysqld.log

# Ubuntu 默认无密码，直接登录修改
mysql -u root -p
```

### 3.2 配置 MySQL

```sql
-- 修改 root 密码（使用强密码）
ALTER USER 'root'@'localhost' IDENTIFIED BY 'YourStrongPassword123!';

-- 创建应用数据库
CREATE DATABASE IF NOT EXISTS job_assistant CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建应用用户（不要用 root 连接应用）
CREATE USER 'job_user'@'localhost' IDENTIFIED BY 'JobUserPassword123!';
GRANT ALL PRIVILEGES ON job_assistant.* TO 'job_user'@'localhost';
FLUSH PRIVILEGES;

-- 退出
EXIT;
```

### 3.3 导入数据库表结构

```bash
# 上传 SQL 文件到服务器
# 本地执行：
scp database/job_assistant.sql root@<ECS_IP>:/tmp/

# 服务器上导入
mysql -u root -p job_assistant < /tmp/job_assistant.sql
```

### 3.4 修改 MySQL 配置（可选）

编辑 `/etc/my.cnf` (CentOS) 或 `/etc/mysql/mysql.conf.d/mysqld.cnf` (Ubuntu)：

```ini
[mysqld]
# 字符集
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci

# 时区
default-time-zone='+08:00'

# 性能优化（根据服务器配置调整）
innodb_buffer_pool_size = 512M
max_connections = 100
```

重启 MySQL：
```bash
systemctl restart mysqld
```

---

## 四、后端部署

### 4.1 创建应用目录

```bash
mkdir -p /opt/personal-job-assistant
mkdir -p /opt/personal-job-assistant/backend
mkdir -p /opt/personal-job-assistant/logs
```

### 4.2 上传后端代码

**方式1: 使用 Git**
```bash
cd /opt/personal-job-assistant

# 克隆代码（如果仓库公开）
git clone https://github.com/wanyuepeng960725/personal-job-assistant.git temp
cp -r temp/backend/* backend/
rm -rf temp
```

**方式2: 本地打包上传**
```bash
# 本地打包（跳过测试）
cd backend
mvn clean package -DskipTests

# 上传 jar 包
scp target/personal-job-assistant-1.0.0.jar root@<ECS_IP>:/opt/personal-job-assistant/backend/

# 上传配置文件
scp src/main/resources/application-prod.yml root@<ECS_IP>:/opt/personal-job-assistant/backend/
```

### 4.3 配置生产环境

创建 `/opt/personal-job-assistant/backend/application-prod.yml`：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/job_assistant?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: job_user
    password: JobUserPassword123!
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

mybatis:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

jwt:
  secret: YourJWTSecretKeyHereAtLeast32CharactersLong
  expiration: 86400000

cors:
  allowed-origins: http://localhost,http://<你的域名>
```

### 4.4 创建启动脚本

创建 `/opt/personal-job-assistant/start-backend.sh`：

```bash
#!/bin/bash

APP_NAME="personal-job-assistant"
JAR_PATH="/opt/personal-job-assistant/backend/personal-job-assistant-1.0.0.jar"
LOG_FILE="/opt/personal-job-assistant/logs/backend.log"
PID_FILE="/opt/personal-job-assistant/backend.pid"

# 检查是否已在运行
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null 2>&1; then
        echo "应用已在运行 (PID: $PID)"
        exit 1
    fi
fi

# 启动应用
echo "启动 $APP_NAME ..."
nohup java -jar \
    -Dspring.profiles.active=prod \
    -Dfile.encoding=UTF-8 \
    -Xms512m \
    -Xmx1024m \
    "$JAR_PATH" > "$LOG_FILE" 2>&1 &

# 保存 PID
echo $! > "$PID_FILE"
echo "启动成功，PID: $(cat $PID_FILE)"
echo "日志: $LOG_FILE"
```

创建 `/opt/personal-job-assistant/stop-backend.sh`：

```bash
#!/bin/bash

PID_FILE="/opt/personal-job-assistant/backend.pid"

if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    echo "停止应用 (PID: $PID) ..."
    kill $PID
    rm -f "$PID_FILE"
    echo "已停止"
else
    echo "应用未运行"
fi
```

设置权限：
```bash
chmod +x /opt/personal-job-assistant/*.sh
```

### 4.5 使用 Systemd 管理服务（推荐）

创建 `/etc/systemd/system/job-assistant.service`：

```ini
[Unit]
Description=Personal Job Assistant Backend
After=syslog.target network.target mysql.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/personal-job-assistant
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod -Xms512m -Xmx1024m /opt/personal-job-assistant/backend/personal-job-assistant-1.0.0.jar
ExecStop=/bin/kill -15 $MAINPID
Restart=always
RestartSec=10
StandardOutput=append:/opt/personal-job-assistant/logs/backend.log
StandardError=append:/opt/personal-job-assistant/logs/backend-error.log

[Install]
WantedBy=multi-user.target
```

启动服务：
```bash
systemctl daemon-reload
systemctl enable job-assistant
systemctl start job-assistant

# 查看状态
systemctl status job-assistant

# 查看日志
journalctl -u job-assistant -f
```

---

## 五、前端部署

### 5.1 本地构建前端

```bash
cd frontend

# 安装依赖
npm install

# 修改 API 地址
# 编辑 .env.production 文件：
echo "VITE_API_BASE_URL=http://<你的ECS_IP>:8080/api" > .env.production

# 构建生产包
npm run build
```

### 5.2 上传前端文件

```bash
# 创建前端目录
mkdir -p /opt/personal-job-assistant/frontend

# 上传构建后的文件
scp -r dist/* root@<ECS_IP>:/opt/personal-job-assistant/frontend/
```

---

## 六、Nginx 配置

### 6.1 配置 Nginx

编辑 `/etc/nginx/nginx.conf` 或在 `/etc/nginx/conf.d/` 创建新配置：

```nginx
server {
    listen 80;
    server_name _;  # 使用 _ 匹配所有域名，或填写你的域名

    # 前端静态文件
    location / {
        root /opt/personal-job-assistant/frontend;
        index index.html index.htm;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 反向代理
    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 超时设置
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }

    # 错误页面
    error_page 500 502 503 504 /50x.html;
    location = /50x.html {
        root /usr/share/nginx/html;
    }
}
```

### 6.2 测试并启动 Nginx

```bash
# 测试配置
nginx -t

# 启动 Nginx
systemctl start nginx
systemctl enable nginx

# 重载配置
nginx -s reload
```

---

## 七、HTTPS 配置（可选但推荐）

### 7.1 申请 SSL 证书

**方式1: 阿里云免费证书**
1. 登录阿里云 SSL 控制台
2. 申请免费 DV 证书
3. 域名验证后下载证书（Nginx 格式）

**方式2: Let's Encrypt 免费证书**
```bash
# 安装 certbot
yum install -y certbot python3-certbot-nginx

# 申请证书
certbot --nginx -d yourdomain.com -d www.yourdomain.com

# 自动续期
echo "0 0,12 * * * root certbot renew --quiet" >> /etc/crontab
```

### 7.2 配置 HTTPS

```nginx
server {
    listen 80;
    server_name yourdomain.com;
    return 301 https://$server_name$request_uri;  # HTTP 重定向到 HTTPS
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com;

    # SSL 证书
    ssl_certificate /path/to/your/cert.pem;
    ssl_certificate_key /path/to/your/key.pem;

    # SSL 优化
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers 'ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256';
    ssl_prefer_server_ciphers on;

    # 前端静态文件
    location / {
        root /opt/personal-job-assistant/frontend;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API
    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

## 八、验证部署

### 8.1 检查各服务状态

```bash
# MySQL
systemctl status mysqld
mysql -u job_user -p -e "SELECT 1"

# 后端
systemctl status job-assistant
curl http://localhost:8080/api/auth/login -X POST

# Nginx
systemctl status nginx
curl -I http://localhost
```

### 8.2 浏览器访问

```
http://<你的ECS公网IP>
或
http://yourdomain.com
```

---

## 九、运维管理

### 9.1 日常命令

```bash
# 查看后端日志
tail -f /opt/personal-job-assistant/logs/backend.log

# 重启后端
systemctl restart job-assistant

# 查看系统资源
htop
free -h
df -h

# 查看 Nginx 访问日志
tail -f /var/log/nginx/access.log

# 查看 Nginx 错误日志
tail -f /var/log/nginx/error.log
```

### 9.2 自动备份脚本

创建 `/opt/personal-job-assistant/backup.sh`：

```bash
#!/bin/bash

BACKUP_DIR="/opt/backups"
DB_NAME="job_assistant"
DB_USER="root"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# 备份数据库
mysqldump -u $DB_USER -p'YourStrongPassword123!' $DB_NAME > $BACKUP_DIR/db_backup_$DATE.sql

# 压缩备份
gzip $BACKUP_DIR/db_backup_$DATE.sql

# 删除7天前的备份
find $BACKUP_DIR -name "db_backup_*.sql.gz" -mtime +7 -delete

echo "备份完成: $BACKUP_DIR/db_backup_$DATE.sql.gz"
```

添加到定时任务：
```bash
chmod +x /opt/personal-job-assistant/backup.sh

# 每天凌晨2点备份
echo "0 2 * * * root /opt/personal-job-assistant/backup.sh >> /var/log/backup.log 2>&1" >> /etc/crontab
```

### 9.3 监控告警（可选）

安装阿里云云监控插件：
```bash
wget http://update2.aegis.aliyun.com/download/quartz_install.sh
chmod +x quartz_install.sh
./quartz_install.sh
```

或在阿里云控制台配置：
- 云监控 → 告警服务 → 创建告警规则
- 监控 ECS 的 CPU、内存、磁盘使用率
- 配置告警通知（短信/邮件）

---

## 十、故障排查

### 10.1 后端无法启动

```bash
# 查看详细错误
java -jar /opt/personal-job-assistant/backend/personal-job-assistant-1.0.0.jar

# 检查端口占用
netstat -tlnp | grep 8080

# 检查数据库连接
mysql -u job_user -p -e "USE job_assistant; SHOW TABLES;"
```

### 10.2 前端访问空白

```bash
# 检查 Nginx 配置
nginx -t

# 检查前端文件
ls -la /opt/personal-job-assistant/frontend/

# 查看 Nginx 错误日志
tail -f /var/log/nginx/error.log
```

### 10.3 API 请求失败

```bash
# 测试后端接口
curl http://localhost:8080/api/auth/login \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test"}'

# 检查后端日志
tail -f /opt/personal-job-assistant/logs/backend.log
```

---

## 十一、性能优化

### 11.1 JVM 优化

根据服务器内存调整：
```bash
# 2核4G 服务器推荐
-Xms1024m -Xmx2048m

# 4核8G 服务器推荐
-Xms2048m -Xmx4096m
```

### 11.2 MySQL 优化

编辑 `/etc/my.cnf`：
```ini
[mysqld]
# 缓冲池大小（建议设为物理内存的50-70%）
innodb_buffer_pool_size = 2G

# 日志文件大小
innodb_log_file_size = 256M
innodb_log_files_in_group = 2

# 连接数
max_connections = 200

# 查询缓存（MySQL 8.0 已移除，仅 5.7 可用）
query_cache_type = 1
query_cache_size = 64M
```

### 11.3 Nginx 优化

```nginx
# /etc/nginx/nginx.conf
worker_processes auto;
worker_connections 1024;

gzip on;
gzip_types text/plain text/css application/json application/javascript;

# 静态文件缓存
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

---

## 十二、安全加固

### 12.1 服务器安全

```bash
# 修改 SSH 端口（建议）
vim /etc/ssh/sshd_config
# Port 2222  # 改为非22端口
# PermitRootLogin no  # 禁止root登录
# PasswordAuthentication no  # 使用密钥登录

systemctl restart sshd

# 安装 fail2ban 防暴力破解
yum install -y fail2ban
systemctl enable fail2ban
systemctl start fail2ban
```

### 12.2 应用安全

1. **修改默认密码**: 数据库、JWT Secret 使用强密码
2. **定期更新**: 及时更新系统和依赖包
3. **访问控制**: 安全组只开放必要端口
4. **日志审计**: 定期检查异常访问日志

---

## 总结

完成以上步骤后，你的个人求职助手就成功部署到阿里云了！

**访问地址**: http://yourdomain.com 或 http://ECS_IP

**管理入口**: 
- 后端 API: http://yourdomain.com/api
- 数据库: mysql -u job_user -p

如有问题，检查各服务的日志文件定位问题。

---

**文档版本**: v1.0  
**最后更新**: 2026-03-29
