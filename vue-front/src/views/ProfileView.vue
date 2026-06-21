<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const userInfo = ref<any>(null)
const stats = ref({
  totalAnswered: 0,
  correctRate: 0,
  rank: 0
})
const loading = ref(false)
const isAdmin = ref(false)

const fetchProfile = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/user/profile', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      userInfo.value = response.data.data.user
      stats.value = response.data.data.stats
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
    const stored = localStorage.getItem('userInfo')
    if (stored) {
      userInfo.value = JSON.parse(stored)
    }
    stats.value = { totalAnswered: 156, correctRate: 78, rank: 23 }
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
  fetchProfile()
  // 检查是否为管理员
  isAdmin.value = localStorage.getItem('userRole') === 'ADMIN'
})
</script>

<template>
  <div class="min-h-screen bg-gray-50 pb-20">
    <!-- 头部 -->
    <header class="bg-gradient-to-br from-primary-600 to-primary-800 text-white px-4 py-8">
      <div class="max-w-4xl mx-auto text-center">
        <div class="w-24 h-24 bg-white/20 rounded-full mx-auto flex items-center justify-center mb-4">
          <span class="text-5xl">👤</span>
        </div>
        <h2 class="text-2xl font-bold">{{ userInfo?.username || '用户' }}</h2>
        <p class="text-primary-200 text-sm mt-1">ID: {{ userInfo?.id || '-' }}</p>
      </div>
    </header>

    <!-- 统计卡片 -->
    <div class="max-w-4xl mx-auto px-4 -mt-6">
      <div class="bg-white rounded-2xl shadow-lg p-6">
        <div class="grid grid-cols-3 gap-4">
          <div class="text-center">
            <div class="text-2xl font-bold text-primary-600">{{ stats.totalAnswered }}</div>
            <div class="text-xs text-gray-500 mt-1">答题总数</div>
          </div>
          <div class="text-center">
            <div class="text-2xl font-bold text-green-500">{{ stats.correctRate }}%</div>
            <div class="text-xs text-gray-500 mt-1">正确率</div>
          </div>
          <div class="text-center">
            <div class="text-2xl font-bold text-orange-500">第{{ stats.rank }}名</div>
            <div class="text-xs text-gray-500 mt-1">排行榜</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 功能列表 -->
    <div class="max-w-4xl mx-auto px-4 mt-6">
      <div class="bg-white rounded-xl shadow-sm">
        <div class="divide-y divide-gray-100">
          <!-- 管理后台入口（仅管理员可见） -->
          <router-link
            v-if="isAdmin"
            to="/admin"
            class="flex items-center justify-between p-4 hover:bg-gray-50 transition bg-blue-50"
          >
            <div class="flex items-center">
              <span class="text-xl mr-3">🛡️</span>
              <span class="text-blue-600 font-medium">管理后台</span>
            </div>
            <span class="text-gray-400">→</span>
          </router-link>
          <router-link
            to="/history"
            class="flex items-center justify-between p-4 hover:bg-gray-50 transition"
          >
            <div class="flex items-center">
              <span class="text-xl mr-3">📊</span>
              <span class="text-gray-800">答题记录</span>
            </div>
            <span class="text-gray-400">→</span>
          </router-link>
          <router-link
            to="/wrong"
            class="flex items-center justify-between p-4 hover:bg-gray-50 transition"
          >
            <div class="flex items-center">
              <span class="text-xl mr-3">📚</span>
              <span class="text-gray-800">错题本</span>
            </div>
            <span class="text-gray-400">→</span>
          </router-link>
          <router-link
            to="/import"
            class="flex items-center justify-between p-4 hover:bg-gray-50 transition"
          >
            <div class="flex items-center">
              <span class="text-xl mr-3">📥</span>
              <span class="text-gray-800">导入题库</span>
            </div>
            <span class="text-gray-400">→</span>
          </router-link>
          <div class="flex items-center justify-between p-4 hover:bg-gray-50 transition cursor-pointer">
            <div class="flex items-center">
              <span class="text-xl mr-3">⚙️</span>
              <span class="text-gray-800">设置</span>
            </div>
            <span class="text-gray-400">→</span>
          </div>
          <div class="flex items-center justify-between p-4 hover:bg-gray-50 transition cursor-pointer">
            <div class="flex items-center">
              <span class="text-xl mr-3">❓</span>
              <span class="text-gray-800">帮助与反馈</span>
            </div>
            <span class="text-gray-400">→</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 退出按钮 -->
    <div class="max-w-4xl mx-auto px-4 mt-6">
      <button
        @click="handleLogout"
        class="w-full py-3 bg-red-50 text-red-600 font-medium rounded-xl hover:bg-red-100 transition"
      >
        退出登录
      </button>
    </div>

    <!-- 版本信息 -->
    <div class="text-center text-gray-400 text-xs mt-8">
      QuizMaster v1.0.0
    </div>

    <!-- 底部导航 -->
    <nav class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-4 py-2">
      <div class="max-w-4xl mx-auto flex justify-around">
        <router-link to="/home" class="flex flex-col items-center text-gray-400">
          <span class="text-xl">🏠</span>
          <span class="text-xs mt-1">首页</span>
        </router-link>
        <router-link to="/history" class="flex flex-col items-center text-gray-400">
          <span class="text-xl">📊</span>
          <span class="text-xs mt-1">记录</span>
        </router-link>
        <router-link to="/wrong" class="flex flex-col items-center text-gray-400">
          <span class="text-xl">📚</span>
          <span class="text-xs mt-1">错题本</span>
        </router-link>
        <router-link to="/profile" class="flex flex-col items-center text-primary-600">
          <span class="text-xl">👤</span>
          <span class="text-xs mt-1">我的</span>
        </router-link>
      </div>
    </nav>
  </div>
</template>
