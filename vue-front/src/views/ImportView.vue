<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const file = ref<File | null>(null)
const uploading = ref(false)
const result = ref<any>(null)
const error = ref('')
const dragover = ref(false)

const handleFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement
  if (target.files && target.files[0]) {
    file.value = target.files[0]
    error.value = ''
  }
}

const handleDrop = (e: DragEvent) => {
  e.preventDefault()
  dragover.value = false
  if (e.dataTransfer?.files && e.dataTransfer.files[0]) {
    const selectedFile = e.dataTransfer.files[0]
    if (selectedFile.name.endsWith('.xlsx') || selectedFile.name.endsWith('.xls')) {
      file.value = selectedFile
      error.value = ''
    } else {
      error.value = '请上传.xlsx格式的Excel文件'
    }
  }
}

const handleDragover = (e: DragEvent) => {
  e.preventDefault()
  dragover.value = true
}

const handleDragleave = () => {
  dragover.value = false
}

const uploadFile = async () => {
  if (!file.value) {
    error.value = '请选择要上传的文件'
    return
  }

  uploading.value = true
  error.value = ''
  result.value = null

  try {
    const formData = new FormData()
    formData.append('file', file.value)

    const response = await axios.post('/api/import/questions', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (response.data.code === 200) {
      result.value = response.data.data
    } else {
      error.value = response.data.message || '导入失败'
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || '上传失败，请重试'
  } finally {
    uploading.value = false
  }
}

const downloadTemplate = () => {
  const headers = ['序号', 'title', 'optionA', 'optionB', 'optionC', 'optionD', 'answer', 'analysis']
  const exampleData = [
    [1, '示例题目', '选项A内容', '选项B内容', '选项C内容', '选项D内容', 'A', '这是解析内容']
  ]
  
  let csvContent = headers.join(',') + '\n'
  exampleData.forEach(row => {
    csvContent += row.join(',') + '\n'
  })
  
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = '题库导入模板.csv'
  link.click()
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 pb-20">
    <!-- 头部 -->
    <header class="bg-primary-600 text-white px-4 py-4">
      <div class="max-w-4xl mx-auto flex items-center">
        <button @click="router.back()" class="text-white mr-4">
          <span class="text-xl">←</span>
        </button>
        <h1 class="text-lg font-bold">题库导入</h1>
      </div>
    </header>

    <!-- 上传区域 -->
    <div class="max-w-4xl mx-auto px-4 mt-6">
      <!-- 上传说明 -->
      <div class="bg-white rounded-2xl shadow-sm p-6 mb-4">
        <h2 class="text-lg font-bold text-gray-800 mb-4">导入说明</h2>
        <div class="text-gray-600 text-sm space-y-2">
          <p>1. 请上传.xlsx格式的Excel文件</p>
          <p>2. Excel文件应包含以下列（按顺序）：</p>
          <div class="bg-gray-50 rounded-lg p-3 mt-2 text-xs font-mono">
            <div>序号 | title | optionA | optionB | optionC | optionD | answer | analysis</div>
          </div>
          <p>3. <strong>title</strong>：题目内容</p>
          <p>4. <strong>optionA-D</strong>：选项A到D的内容</p>
          <p>5. <strong>answer</strong>：正确答案（A/B/C/D）</p>
          <p>6. <strong>analysis</strong>：答案解析（可选）</p>
        </div>
        <button
          @click="downloadTemplate"
          class="mt-4 text-primary-600 text-sm font-medium hover:underline"
        >
          下载导入模板
        </button>
      </div>

      <!-- 文件选择 -->
      <div
        @drop="handleDrop"
        @dragover="handleDragover"
        @dragleave="handleDragleave"
        class="bg-white rounded-2xl shadow-sm p-8 text-center cursor-pointer transition-all"
        :class="{ 'border-2 border-primary-500 bg-primary-50': dragover }"
        @click="($refs.fileInput as HTMLInputElement).click()"
      >
        <input
          ref="fileInput"
          type="file"
          accept=".xlsx,.xls"
          class="hidden"
          @change="handleFileChange"
        />
        
        <div v-if="!file" class="text-gray-400">
          <div class="text-5xl mb-4">📁</div>
          <p class="text-lg">点击或拖拽上传文件</p>
          <p class="text-sm mt-2">支持.xlsx格式</p>
        </div>
        
        <div v-else class="text-gray-700">
          <div class="text-5xl mb-4">✓</div>
          <p class="text-lg font-medium">{{ file.name }}</p>
          <p class="text-sm mt-2 text-gray-500">
            大小: {{ (file.size / 1024).toFixed(2) }} KB
          </p>
        </div>
      </div>

      <!-- 错误提示 -->
      <div v-if="error" class="mt-4 p-4 bg-red-50 border border-red-200 rounded-xl text-red-600 text-sm">
        {{ error }}
      </div>

      <!-- 上传按钮 -->
      <button
        @click="uploadFile"
        :disabled="!file || uploading"
        class="w-full mt-4 py-4 bg-primary-600 text-white font-bold rounded-xl disabled:opacity-50 disabled:cursor-not-allowed hover:bg-primary-700 transition"
      >
        {{ uploading ? '导入中...' : '开始导入' }}
      </button>

      <!-- 导入结果 -->
      <div v-if="result" class="mt-6 bg-white rounded-2xl shadow-sm p-6">
        <h3 class="text-lg font-bold text-gray-800 mb-4">导入结果</h3>
        <div class="space-y-3">
          <div class="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
            <span class="text-gray-600">总行数</span>
            <span class="font-bold text-gray-800">{{ result.totalRows }}</span>
          </div>
          <div class="flex justify-between items-center p-3 bg-green-50 rounded-lg">
            <span class="text-green-600">成功导入</span>
            <span class="font-bold text-green-700">{{ result.successCount }}</span>
          </div>
          <div class="flex justify-between items-center p-3 bg-red-50 rounded-lg">
            <span class="text-red-600">失败</span>
            <span class="font-bold text-red-700">{{ result.failCount }}</span>
          </div>
          
          <!-- 错误详情 -->
          <div v-if="result.errorMessages && result.errorMessages.length > 0" class="mt-4">
            <h4 class="text-sm font-medium text-red-600 mb-2">错误详情：</h4>
            <div class="bg-red-50 rounded-lg p-3 max-h-48 overflow-y-auto">
              <p v-for="(err, idx) in result.errorMessages" :key="idx" class="text-xs text-red-600 mb-1">
                {{ err }}
              </p>
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