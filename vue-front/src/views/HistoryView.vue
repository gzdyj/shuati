<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

interface Record {
  id: number
  date: string
  totalCount: number
  correctCount: number
  correctRate: number
}

const records = ref<Record[]>([])
const loading = ref(false)

const fetchHistory = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/answer/history', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      records.value = response.data.data
    }
  } catch (error) {
    console.error('获取历史记录失败', error)
    // 使用模拟数据
    records.value = [
      { id: 1, date: '2024-01-15', totalCount: 10, correctCount: 8, correctRate: 80 },
      { id: 2, date: '2024-01-14', totalCount: 10, correctCount: 6, correctRate: 60 },
      { id: 3, date: '2024-01-13', totalCount: 10, correctCount: 9, correctRate: 90 }
    ]
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchHistory()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50 pb-20">
    <!-- 头部 -->
    <header class="bg-primary-600 text-white px-4 py-6">
      <div class="max-w-4xl mx-auto">
        <h1 class="text-xl font-bold">答题记录</h1>
      </div>
    </header>

    <!-- 内容 -->
    <div class="max-w-4xl mx-auto px-4 mt-4">
      <div v-if="loading" class="text-center py-10 text-gray-500">加载中...</div>

      <div v-else-if="records.length === 0" class="text-center py-10 text-gray-500">
        暂无答题记录
      </div>

      <div v-else class="space-y-4">
        <div
          v-for="record in records"
          :key="record.id"
          class="bg-white rounded-xl p-4 shadow-sm"
        >
          <div class="flex justify-between items-center">
            <div>
              <div class="font-bold text-gray-800">{{ record.date }}</div>
              <div class="text-sm text-gray-500 mt-1">
                答题 {{ record.totalCount }} 道
              </div>
            </div>
            <div class="text-right">
              <div
                class="text-2xl font-bold"
                :class="record.correctRate >= 80 ? 'text-green-500' : record.correctRate >= 60 ? 'text-orange-500' : 'text-red-500'"
              >
                {{ record.correctRate }}%
              </div>
              <div class="text-xs text-gray-500">
                正确 {{ record.correctCount }} 道
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
        <router-link to="/history" class="flex flex-col items-center text-primary-600">
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
