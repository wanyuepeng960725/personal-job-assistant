<template>
  <div class="dashboard">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>个人求职助手</h1>
          <div class="user-info">
            <span>欢迎，{{ username }}</span>
            <el-button type="danger" plain @click="handleLogout">退出</el-button>
          </div>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px">
          <el-menu
            :default-active="activeMenu"
            router
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b"
          >
            <el-menu-item index="/dashboard">
              <el-icon><House /></el-icon>
              <span>仪表盘</span>
            </el-menu-item>
            <el-menu-item index="/interview">
              <el-icon><Document /></el-icon>
              <span>求职进度</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House, Document } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const username = ref('用户')

const activeMenu = computed(() => route.path)

const loadUserInfo = async () => {
  try {
    const { getUserInfoAPI } = await import('@/api/index')
    const response = await getUserInfoAPI()
    username.value = response.data.username
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
}

const handleLogout = () => {
  localStorage.removeItem('token')
  ElMessage.success('已退出登录')
  router.push('/login')
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.dashboard {
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-header {
  background-color: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.header-content h1 {
  margin: 0;
  font-size: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.el-aside {
  background-color: #545c64;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
