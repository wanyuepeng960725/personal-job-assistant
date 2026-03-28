# 个人求职助手 - 部署完成报告

> 部署日期：2026年3月29日
> 状态：✅ 完全成功

---

## 📊 部署概览

### 服务器信息

| 项目 | 信息 |
|------|------|
| **IP 地址** | 8.146.238.66 |
| **域名** | wanyuepeng.com |
| **SSH 登录** | `ssh root@8.146.238.66` |
| **SSH 密码** | %LUshuxiang981106 |

### 服务配置

| 服务 | 状态 | 端口 | 说明 |
|------|------|------|------|
| **Nginx** | 🟢 运行中 | 80, 443 | HTTP → HTTPS 自动跳转 |
| **后端 API** | 🟢 运行中 | 8080 | Spring Boot 3.2 + Java 21 |
| **MySQL** | 🟢 运行中 | 3306 | MySQL 8.0 |

### SSL 证书

- **类型**: Let's Encrypt 免费证书
- **域名**: wanyuepeng.com + www.wanyuepeng.com
- **有效期**: 2026-06-26（89天）
- **自动续期**: 已配置（每天 0:00 和 12:00）
- **证书路径**: `/etc/letsencrypt/live/wanyuepeng.com/`

---

## 🌐 访问地址

| 协议 | 地址 | 说明 |
|------|------|------|
| **HTTPS** | https://wanyuepeng.com | 主域名 |
| **HTTPS** | https://www.wanyuepond.com | www 子域名 |
| **HTTP** | http://wanyuepeng.com | 自动跳转 HTTPS |
| **后端 API** | https://wanyuepeng.com/api | 后端接口 |

---

## 📋 部署清单

| # | 任务 | 状态 | 说明 |
|---|------|------|------|
| 1 | 购买阿里云 ECS 服务器 | ✅ | 2核4G CentOS 7.9 |
| 2 | 安装 Java 21 | ✅ | 从本地打包的 JAR 包要求 |
| 3 | 安装 MySQL 8.0 | ✅ | 包含数据库和表 |
| 4 | 安装 Nginx | ✅ | 反向代理和静态文件 |
| 5 | 创建数据库 `job_assistant` | ✅ | 已导入表结构 |
| 6 | 部署后端服务 | ✅ | Java 21 + Spring Boot 3.2 |
| 7 | 部署前端 Vue.js 应用 | ✅ | Vite + Element Plus |
| 8 | 配置域名解析 | ✅ | A 记录指向 8.146.238.66 |
| 9 | 申请 Let's Encrypt SSL 证书 | ✅ | 免费证书 |
| 10 | 配置 HTTPS | ✅ | HTTP → HTTPS |
| 11 | 配置证书自动续期 | ✅ | 每天 0:00 和 12:00 |
| 12 | 修复数据库连接 | ✅ | 添加 allowPublicKeyRetrieval=true |

---

## 🔐 测试账号

| 用户名 | 密码 | 邮箱 |
|--------|------|------|
| wanyuepeng | Wan@123456 | wyp960725@163.com |

*此账号已在部署过程中创建，用于测试*

---

## 🛠️ 日常管理命令

### 连接服务器

```bash
ssh root@8.146.238.66
# 密码: %LUshuxiang981106
```

### 服务管理

```bash
# 查看所有服务状态
systemctl status job-assistant
systemctl status nginx
systemctl status mysqld

# 重启服务
systemctl restart job-assistant
systemctl restart nginx
systemctl restart mysqld

# 查看后端日志
journalctl -u job-assistant -f

# 查看 Nginx 日志
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```

### 数据库管理

```bash
# 连接数据库
mysql -u job_user -pJobPassword123! job_assistant

# 常用命令
mysql -u job_user -pJobPassword123! -e "SELECT * FROM job_assistant.user;"
mysql -u job_user -pJobPassword123! -e "SELECT * FROM job_assistant.interview;"
```

### SSL 证书管理

```bash
# 查看证书信息
certbot certificates

# 手动续期证书
certbot renew

# 查看续期日志
tail -f /var/log/certbot-renew.log

# 查看详细日志
tail -f /var/log/letsencrypt/letsencrypt.log
```

### 备份数据库

```bash
# 创建备份脚本
cat > /opt/personal-job-assistant/backup.sh << 'EOF'
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/opt/backups"
mkdir -p $BACKUP_DIR

# 备份数据库
mysqldump -u job_user -pJobPassword123! job_assistant > $BACKUP_DIR/job_assistant_$DATE.sql

# 压缩备份
gzip $BACKUP_DIR/job_assistant_$DATE.sql

# 删除 7 天前的备份
find $BACKUP_DIR -name "job_assistant_*.sql.gz" -mtime +7 -delete

echo "备份完成: $BACKUP_DIR/job_assistant_$DATE.sql.gz"
EOF

chmod +x /opt/personal-job-assistant/backup.sh

# 手动执行备份
/opt/personal-job-assistant/backup.sh

# 添加定时任务（每天凌晨 2 点）
echo "0 2 * * * root /opt/personal-job-assistant/backup.sh >> /var/log/backup.log 2>&1" >> /etc/crontab
```

---

## 📊 系统资源监控

```bash
# 查看 CPU 和内存使用
top -c

# 查看磁盘使用
df -h

# 查看内存使用
free -h

# 查看系统负载
uptime
```

---

## 🚨 故障排查

### 服务无法访问

```bash
# 1. 检查服务状态
systemctl status job-assistant
systemctl status nginx

# 2. 检查端口监听
netstat -tlnp | grep -E ":(80|443|8080|3306)"

# 3. 查看防火墙
systemctl status firewalld

# 4. 检查安全组（在阿里云控制台检查 80/443 端口是否放行）
```

### 后端 API 返回 500 错误

```bash
# 1. 查看后端日志
journalctl -u job-assistant -n 50

# 2. 检查数据库连接
mysql -u job_user -pJobPassword123! -e "SELECT 1;"

# 3. 检查数据库表
mysql -u job_user -pJobPassword123! -e "SHOW TABLES FROM job_assistant;"

# 4. 查看数据库连接数
mysql -u job_user -pJobPassword123! -e "SHOW PROCESSLIST;"
```

### HTTPS 访问失败

```bash
# 1. 检查 Nginx 配置
nginx -t

# 2. 查看 Nginx 错误日志
tail -f /var/log/nginx/error.log

# 3. 检查 SSL 证书
certbot certificates

# 4. 手动续期证书
certbot renew --force-renewal
```

---

## 📝 数据库配置

| 配置项 | 值 |
|--------|-----|
| 数据库名 | `job_assistant` |
| 用户名 | `job_user` |
| 密码 | `JobPassword123!` |
| 主机 | `localhost` |
| 端口 | `3306` |

### 数据表

| 表名 | 说明 |
|------|------|
| `user` | 用户表 |
| `interview` | 面试记录表 |

---

## 🎯 功能清单

### 已实现功能

- ✅ 用户注册
- ✅ 用户登录（JWT 认证）
- ✅ 面试记录管理
- ✅ 面试状态跟踪
- ✅ 用户信息管理
- ✅ 响应式设计
- ✅ HTTPS 安全访问
- ✅ 证书自动续期

### 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue.js 3 + Vite + Element Plus + Pinia |
| 后端 | Spring Boot 3.2 + Spring Security + MyBatis + JWT |
| 数据库 | MySQL 8.0 |
| Web 服务器 | Nginx |
| 运行环境 | Java 21 |
| 操作系统 | CentOS 7.9 |
| SSL | Let's Encrypt |

---

## 📈 性能优化建议

### JVM 参数

当前配置：
```
-Xms512m -Xmx1024m
```

**根据服务器配置调整：**
- 2核4G：`-Xms512m -Xmx1024m`（当前）
- 4核8G：`-Xms2048m -Xmx4096m`
- 8核16G：`-Xms4096m -Xmx8192m`

### Nginx 优化

已在配置中启用：
- Gzip 压缩
- 静态文件缓存（1年）

### MySQL 优化

建议在 `/etc/my.cnf` 中添加：
```ini
[mysqld]
innodb_buffer_pool_size = 512M
max_connections = 100
```

---

## 🔒 安全建议

### 已实施的安全措施

1. ✅ HTTPS 加密
2. ✅ SSL 证书自动续期
3. ✅ 密码加密存储（BCrypt）
4. ✅ JWT Token 认证
5. ✅ SQL 注入防护（MyBatis）
6. ✅ CORS 跨域保护

### 额外安全建议

1. 定期更新系统和软件
2. 使用强密码策略
3. 配置防火墙规则
4. 定期备份数据
5. 监控异常访问日志

---

## 📞 支持与联系

### 常见问题

**Q: 如何重启服务？**
A: `ssh root@8.146.238.66` → `systemctl restart job-assistant`

**Q: 如何查看日志？**
A: `journalctl -u job-assistant -f`

**Q: 证书即将过期怎么办？**
A: 自动续期已配置，无需手动操作。如需手动续期：`certbot renew`

**Q: 如何备份数据？**
A: 使用备份脚本：`/opt/personal-job-assistant/backup.sh`

---

## 📚 相关文档

- **项目代码**: https://github.com/wanyuepeng960725/personal-job-assistant
- **部署文档**: 见项目 README
- **API 文档**: 见项目 README

---

## 🎉 部署完成！

所有服务已成功部署并运行，网站可以正常访问：

**🌐 访问地址：https://wanyuepeng.com**

---

*报告生成时间：2026-03-29 02:43*
*部署完成时间：2026-03-29 02:41*
