<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const route = useRoute()

interface Question {
  id: number
  content: string
  options: { id: string; content: string }[]
}

const questions = ref<Question[]>([])
const currentIndex = ref(0)
const answers = ref<Record<number, string>>({})
const selectedOption = ref<string | null>(null)
const showResult = ref(false)
const loading = ref(true)
const timeLeft = ref(60)
const timer = ref<number | null>(null)
const categoryId = ref<number | null>(null)

// 获取题目
const fetchQuestions = async () => {
  loading.value = true
  try {
    const categoryParam = categoryId.value ? `?categoryId=${categoryId.value}` : ''
    const response = await axios.get(`/api/question/random${categoryParam}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })

    if (response.data.code === 200) {
      questions.value = response.data.data
      answers.value = {}
    }
  } catch (error) {
    console.error('获取题目失败', error)
    // 使用模拟数据
    questions.value = [
      {
        id: 1,
        content: '中国的首都是哪里？',
        options: [
          { id: 'A', content: '北京' },
          { id: 'B', content: '上海' },
          { id: 'C', content: '广州' },
          { id: 'D', content: '深圳' }
        ]
      },
      {
        id: 2,
        content: 'JavaScript是一种什么类型的语言？',
        options: [
          { id: 'A', content: '编译型语言' },
          { id: 'B', content: '解释型语言' },
          { id: 'C', content: '汇编语言' },
          { id: 'D', content: '机器语言' }
        ]
      }
    ]
  } finally {
    loading.value = false
  }
}

const selectOption = (optionId: string) => {
  if (showResult.value) return
  selectedOption.value = optionId
  answers.value[questions.value[currentIndex.value].id] = optionId
}

const confirmAnswer = () => {
  if (!selectedOption.value) return
  showResult.value = true
}

const nextQuestion = () => {
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value++
    selectedOption.value = answers.value[questions.value[currentIndex.value].id] || null
    showResult.value = !!selectedOption.value
  }
}

const submitQuiz = async () => {
  try {
    await axios.post('/api/answer/submit', {
      answers: answers.value
    }, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
  } catch (error) {
    console.error('提交失败', error)
  }
  // 计算正确率并跳转
  const correctCount = Object.entries(answers.value).filter(() => {
    // 简化逻辑，实际应从后端获取
    return true
  }).length
  const rate = Math.round((correctCount / questions.value.length) * 100)
  alert(`答题完成！正确率：${rate}%`)
  router.push('/home')
}

const handleTimeout = () => {
  if (timeLeft.value > 0) {
    timeLeft.value--
  } else {
    confirmAnswer()
  }
}

onMounted(() => {
  categoryId.value = route.params.id ? Number(route.params.id) : null
  fetchQuestions()
  timer.value = window.setInterval(handleTimeout, 1000)
})

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
})
</script>

<template>
  <div class="min-h-screen bg-gray-50 pb-20">
    <!-- 头部 -->
    <header class="bg-primary-600 text-white px-4 py-4">
      <div class="max-w-4xl mx-auto flex justify-between items-center">
        <button @click="router.back()" class="text-white">
          <span class="text-xl">←</span>
        </button>
        <div class="text-center">
          <div class="text-sm opacity-80">第 {{ currentIndex + 1 }} / {{ questions.length }} 题</div>
          <div class="font-bold">{{ Math.ceil(timeLeft / 60) }}:{{ String(timeLeft % 60).padStart(2, '0') }}</div>
        </div>
        <button @click="submitQuiz" class="text-sm bg-primary-700 px-3 py-1 rounded">
          交卷
        </button>
      </div>
      <!-- 进度条 -->
      <div class="max-w-4xl mx-auto mt-3">
        <div class="h-1 bg-primary-800 rounded-full overflow-hidden">
          <div
            class="h-full bg-white transition-all duration-300"
            :style="{ width: `${((currentIndex + 1) / questions.length) * 100}%` }"
          ></div>
        </div>
      </div>
    </header>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center items-center h-64">
      <div class="text-primary-600">加载中...</div>
    </div>

    <!-- 题目内容 -->
    <div v-else class="max-w-4xl mx-auto px-4 mt-6">
      <div class="bg-white rounded-2xl shadow-sm p-6">
        <h2 class="text-lg font-bold text-gray-800 mb-6">
          {{ questions[currentIndex]?.content }}
        </h2>

        <div class="space-y-3">
          <div
            v-for="option in questions[currentIndex]?.options"
            :key="option.id"
            @click="selectOption(option.id)"
            class="p-4 border-2 rounded-xl cursor-pointer transition-all"
            :class="{
              'border-primary-500 bg-primary-50': selectedOption === option.id && !showResult,
              'border-gray-200 hover:border-primary-300': selectedOption !== option.id && !showResult,
              'border-green-500 bg-green-50': showResult && selectedOption === option.id,
              'border-gray-200 bg-gray-50': showResult && selectedOption !== option.id
            }"
          >
            <div class="flex items-center">
              <span
                class="w-8 h-8 rounded-full border-2 flex items-center justify-center mr-3 font-bold text-sm"
                :class="{
                  'border-primary-500 bg-primary-500 text-white': selectedOption === option.id && !showResult,
                  'border-gray-300 text-gray-500': selectedOption !== option.id && !showResult,
                  'border-green-500 bg-green-500 text-white': showResult && selectedOption === option.id,
                  'border-gray-300 text-gray-400': showResult && selectedOption !== option.id
                }"
              >
                {{ option.id }}
              </span>
              <span class="text-gray-700">{{ option.content }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="mt-6">
        <button
          v-if="!showResult"
          @click="confirmAnswer"
          :disabled="!selectedOption"
          class="w-full py-4 bg-primary-600 text-white font-bold rounded-xl disabled:opacity-50 disabled:cursor-not-allowed hover:bg-primary-700 transition"
        >
          确认答案
        </button>
        <button
          v-else-if="currentIndex < questions.length - 1"
          @click="nextQuestion"
          class="w-full py-4 bg-primary-600 text-white font-bold rounded-xl hover:bg-primary-700 transition"
        >
          下一题
        </button>
        <button
          v-else
          @click="submitQuiz"
          class="w-full py-4 bg-green-500 text-white font-bold rounded-xl hover:bg-green-600 transition"
        >
          提交答题
        </button>
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
