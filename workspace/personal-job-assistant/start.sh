#!/bin/bash

echo "🚀 个人求职助手 - 一键启动脚本"
echo "=================================="

# 检查MySQL是否运行
echo ""
echo "📊 检查MySQL..."
if ! pgrep -x "mysqld" > /dev/null; then
    echo "⚠️  MySQL未运行，请先启动MySQL"
    echo "提示: sudo systemctl start mysql"
    exit 1
fi
echo "✅ MySQL正在运行"

# 检查数据库是否存在
echo ""
echo "📦 检查数据库..."
if ! mysql -u root -p'your_password' -e "USE job_assistant;" 2>/dev/null; then
    echo "⚠️  数据库不存在，正在创建..."
    mysql -u root -p'your_password' < /workspace/projects/workspace/personal-job-assistant/backend/src/main/resources/sql/schema.sql
    echo "✅ 数据库创建成功"
else
    echo "✅ 数据库已存在"
fi

# 启动后端
echo ""
echo "🔧 启动后端..."
cd /workspace/projects/workspace/personal-job-assistant/backend

# 检查后端进程是否已运行
if pgrep -f "PersonalJobAssistantApplication" > /dev/null; then
    echo "⚠️  后端已在运行，先关闭..."
    pkill -f "PersonalJobAssistantApplication"
    sleep 2
fi

# 启动后端（后台运行）
nohup mvn spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
echo "✅ 后端启动中... (PID: $BACKEND_PID)"

# 等待后端启动
echo "⏳ 等待后端启动..."
for i in {1..30}; do
    if curl -s http://localhost:8080/api > /dev/null 2>&1; then
        echo "✅ 后端启动成功！"
        break
    fi
    echo "等待中... ($i/30)"
    sleep 2
done

# 启动前端
echo ""
echo "🎨 启动前端..."
cd /workspace/projects/workspace/personal-job-assistant/frontend

# 检查前端进程是否已运行
if pgrep -f "vite" > /dev/null; then
    echo "⚠️  前端已在运行，先关闭..."
    pkill -f "vite"
    sleep 2
fi

# 启动前端（后台运行）
nohup npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
echo "✅ 前端启动中... (PID: $FRONTEND_PID)"

# 等待前端启动
echo "⏳ 等待前端启动..."
for i in {1..30}; do
    if curl -s http://localhost:5173 > /dev/null 2>&1; then
        echo "✅ 前端启动成功！"
        break
    fi
    echo "等待中... ($i/30)"
    sleep 2
done

# 显示启动信息
echo ""
echo "=================================="
echo "✅ 服务已启动！"
echo "=================================="
echo "📍 前端地址: http://localhost:5173"
echo "📍 后端地址: http://localhost:8080/api"
echo ""
echo "📝 查看后端日志: tail -f /workspace/projects/workspace/personal-job-assistant/backend.log"
echo "📝 查看前端日志: tail -f /workspace/projects/workspace/personal-job-assistant/frontend.log"
echo ""
echo "🛑 停止服务: pkill -f 'PersonalJobAssistantApplication|vite'"
echo "=================================="
