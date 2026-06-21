<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

interface WrongQuestion {
  id: number
  content: string
  yourAnswer: string
  correctAnswer: string
  category: string
}

const wrongQuestions = ref<WrongQuestion[]>([])
const loading = ref(false)

const fetchWrongQuestions = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/question/wrong', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      wrongQuestions.value = response.data.data
    }
  } catch (error) {
    console.error('获取错题本失败', error)
    wrongQuestions.value = [
      {
        id: 1,
        content: '以下哪个是JavaScript的数据类型？',
        yourAnswer: 'Integer',
        correctAnswer: 'Number',
        category: '技术'
      }
    ]
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchWrongQuestions()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50 pb-20">
    <!-- 头部 -->
    <header class="bg-primary-600 text-white px-4 py-6">
      <div class="max-w-4xl mx-auto">
        <h1 class="text-xl font-bold">错题本</h1>
        <p class="text-primary-200 text-sm mt-1">共 {{ wrongQuestions.length }} 道错题</p>
      </div>
    </header>

    <!-- 内容 -->
    <div class="max-w-4xl mx-auto px-4 mt-4">
      <div v-if="loading" class="text-center py-10 text-gray-500">加载中...</div>

      <div v-else-if="wrongQuestions.length === 0" class="text-center py-10">
        <div class="text-6xl mb-4">🎉</div>
        <div class="text-gray-500">太棒了！暂无错题</div>
      </div>

      <div v-else class="space-y-4">
        <div
          v-for="q in wrongQuestions"
          :key="q.id"
          class="bg-white rounded-xl p-4 shadow-sm"
        >
          <div class="flex items-start">
            <span class="text-red-500 text-xl mr-3">✕</span>
            <div class="flex-1">
              <div class="text-gray-800 font-medium">{{ q.content }}</div>
              <div class="mt-3 text-sm">
                <div class="text-red-500">你的答案：{{ q.yourAnswer }}</div>
                <div class="text-green-500 mt-1">正确答案：{{ q.correctAnswer }}</div>
              </div>
              <div class="mt-2">
                <span class="text-xs px-2 py-1 bg-gray-100 text-gray-500 rounded">{{ q.category }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
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
        <router-link to="/wrong" class="flex flex-col items-center text-primary-600">
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
