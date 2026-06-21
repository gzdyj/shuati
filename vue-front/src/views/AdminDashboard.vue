<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const userInfo = ref<any>(null)
const stats = ref({
  totalUsers: 0,
  totalQuestions: 0,
  totalAnswers: 0,
  overallCorrectRate: 0,
  activeUsersToday: 0
})
const loading = ref(false)

const fetchStats = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/admin/statistics/overview', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      stats.value = response.data.data
    }
  } catch (error) {
    console.error('获取统计数据失败', error)
  } finally {
    loading.value = false
  }
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  localStorage.removeItem('userRole')
  router.push('/login')
}

onMounted(() => {
  const stored = localStorage.getItem('userInfo')
  if (stored) {
    userInfo.value = JSON.parse(stored)
  }
  fetchStats()
})
</script>

<template>
  <div class="min-h-screen bg-gray-100">
    <!-- 顶部导航 -->
    <header class="bg-gradient-to-r from-blue-600 to-blue-800 text-white px-6 py-4 shadow-lg">
      <div class="flex items-center justify-between">
        <div class="flex items-center">
          <span class="text-2xl mr-3">🛡️</span>
          <h1 class="text-xl font-bold">QuizMaster 管理后台</h1>
        </div>
        <div class="flex items-center">
          <span class="text-sm mr-4">{{ userInfo?.username || '管理员' }}</span>
          <button @click="handleLogout" class="text-sm bg-white/20 px-3 py-1 rounded hover:bg-white/30">
            退出
          </button>
        </div>
      </div>
    </header>

    <!-- 统计卡片 -->
    <div class="px-6 py-6">
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div class="bg-white rounded-xl shadow-sm p-4">
          <div class="flex items-center">
            <span class="text-3xl mr-3">👥</span>
            <div>
              <div class="text-2xl font-bold text-blue-600">{{ stats.totalUsers }}</div>
              <div class="text-sm text-gray-500">用户总数</div>
            </div>
          </div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-4">
          <div class="flex items-center">
            <span class="text-3xl mr-3">📝</span>
            <div>
              <div class="text-2xl font-bold text-green-600">{{ stats.totalQuestions }}</div>
              <div class="text-sm text-gray-500">题目总数</div>
            </div>
          </div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-4">
          <div class="flex items-center">
            <span class="text-3xl mr-3">📊</span>
            <div>
              <div class="text-2xl font-bold text-purple-600">{{ stats.totalAnswers }}</div>
              <div class="text-sm text-gray-500">答题总数</div>
            </div>
          </div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-4">
          <div class="flex items-center">
            <span class="text-3xl mr-3">✅</span>
            <div>
              <div class="text-2xl font-bold text-orange-600">{{ stats.overallCorrectRate.toFixed(1) }}%</div>
              <div class="text-sm text-gray-500">平均正确率</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 功能入口 -->
    <div class="px-6">
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <router-link to="/admin/users" class="bg-white rounded-xl shadow-sm p-6 hover:shadow-md transition">
          <div class="text-center">
            <span class="text-4xl">👥</span>
            <div class="mt-2 font-medium text-gray-800">用户管理</div>
            <div class="text-sm text-gray-500 mt-1">管理用户账号</div>
          </div>
        </router-link>
        <router-link to="/admin/questions" class="bg-white rounded-xl shadow-sm p-6 hover:shadow-md transition">
          <div class="text-center">
            <span class="text-4xl">📝</span>
            <div class="mt-2 font-medium text-gray-800">题目管理</div>
            <div class="text-sm text-gray-500 mt-1">增删改查题目</div>
          </div>
        </router-link>
        <router-link to="/admin/categories" class="bg-white rounded-xl shadow-sm p-6 hover:shadow-md transition">
          <div class="text-center">
            <span class="text-4xl">📂</span>
            <div class="mt-2 font-medium text-gray-800">分类管理</div>
            <div class="text-sm text-gray-500 mt-1">管理题目分类</div>
          </div>
        </router-link>
        <router-link to="/import" class="bg-white rounded-xl shadow-sm p-6 hover:shadow-md transition">
          <div class="text-center">
            <span class="text-4xl">📥</span>
            <div class="mt-2 font-medium text-gray-800">题库导入</div>
            <div class="text-sm text-gray-500 mt-1">批量导入题目</div>
          </div>
        </router-link>
      </div>
    </div>

    <!-- 返回前台入口 -->
    <div class="px-6 mt-6">
      <router-link to="/home" class="block bg-gray-200 rounded-xl p-4 text-center hover:bg-gray-300 transition">
        <span class="text-gray-600">← 返回前台首页</span>
      </router-link>
    </div>
  </div>
</template>