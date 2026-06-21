<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

interface Category {
  id: number
  name: string
  icon: string
  questionCount: number
}

const router = useRouter()
const userInfo = ref<any>(null)
const categories = ref<Category[]>([])
const stats = ref({
  totalAnswered: 0,
  correctRate: 0,
  continueDays: 0
})

const fetchCategories = async () => {
  try {
    const response = await axios.get('/api/question/categories')
    if (response.data.code === 200) {
      categories.value = response.data.data
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}

const startQuiz = (categoryId?: number) => {
  if (categoryId) {
    router.push(`/quiz/${categoryId}`)
  } else {
    router.push('/quiz')
  }
}

onMounted(() => {
  const stored = localStorage.getItem('userInfo')
  if (stored) {
    userInfo.value = JSON.parse(stored)
  }
  fetchCategories()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 头部 -->
    <header class="bg-primary-600 text-white px-4 py-6">
      <div class="max-w-4xl mx-auto flex justify-between items-center">
        <div>
          <h1 class="text-xl font-bold">QuizMaster</h1>
          <p class="text-primary-200 text-sm mt-1">欢迎回来，{{ userInfo?.username || '用户' }}</p>
        </div>
        <button
          @click="handleLogout"
          class="px-4 py-2 bg-primary-700 rounded-lg text-sm hover:bg-primary-800 transition"
        >
          退出
        </button>
      </div>
    </header>

    <!-- 统计卡片 -->
    <div class="max-w-4xl mx-auto px-4 -mt-4">
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
            <div class="text-2xl font-bold text-orange-500">{{ stats.continueDays }}</div>
            <div class="text-xs text-gray-500 mt-1">连续天数</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 快速开始 -->
    <div class="max-w-4xl mx-auto px-4 mt-6">
      <h2 class="text-lg font-bold text-gray-800 mb-4">快速开始</h2>
      <div
        @click="startQuiz()"
        class="bg-gradient-to-r from-primary-500 to-primary-700 rounded-2xl p-6 text-white cursor-pointer hover:shadow-lg transition"
      >
        <div class="flex items-center justify-between">
          <div>
            <h3 class="text-xl font-bold">随机刷题</h3>
            <p class="text-primary-100 text-sm mt-1">随机抽取10道题目</p>
          </div>
          <div class="w-12 h-12 bg-white/20 rounded-full flex items-center justify-center">
            <span class="text-2xl">🎲</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 分类列表 -->
    <div class="max-w-4xl mx-auto px-4 mt-6">
      <h2 class="text-lg font-bold text-gray-800 mb-4">分类刷题</h2>
      <div class="grid grid-cols-2 sm:grid-cols-3 gap-4">
        <div
          v-for="cat in categories"
          :key="cat.id"
          @click="startQuiz(cat.id)"
          class="bg-white rounded-xl p-4 shadow-sm cursor-pointer hover:shadow-md hover:-translate-y-1 transition"
        >
          <div class="text-3xl mb-2">{{ cat.icon }}</div>
          <h3 class="font-bold text-gray-800">{{ cat.name }}</h3>
          <p class="text-xs text-gray-500 mt-1">{{ cat.questionCount }} 道题目</p>
        </div>
      </div>
    </div>

    <!-- 底部导航 -->
    <nav class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-4 py-2">
      <div class="max-w-4xl mx-auto flex justify-around">
        <router-link to="/home" class="flex flex-col items-center text-primary-600">
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
        <router-link to="/profile" class="flex flex-col items-center text-gray-400">
          <span class="text-xl">👤</span>
          <span class="text-xs mt-1">我的</span>
        </router-link>
      </div>
    </nav>
  </div>
</template>
