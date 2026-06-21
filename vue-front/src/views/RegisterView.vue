<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const errorMsg = ref('')

const handleRegister = async () => {
  if (!username.value || !password.value) {
    errorMsg.value = '请输入用户名和密码'
    return
  }

  if (password.value !== confirmPassword.value) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }

  if (password.value.length < 6) {
    errorMsg.value = '密码长度不能少于6位'
    return
  }

  loading.value = true
  errorMsg.value = ''

  try {
    const response = await axios.post('/api/user/register', {
      username: username.value,
      password: password.value
    })

    if (response.data.code === 200) {
      router.push('/login')
    } else {
      errorMsg.value = response.data.message || '注册失败'
    }
  } catch (error: any) {
    errorMsg.value = error.response?.data?.message || '网络错误，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-primary-600 to-primary-800 flex items-center justify-center p-4">
    <div class="w-full max-w-md">
      <!-- Logo -->
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-20 h-20 bg-white rounded-2xl shadow-lg mb-4">
          <span class="text-4xl">📝</span>
        </div>
        <h1 class="text-3xl font-bold text-white">QuizMaster</h1>
        <p class="text-primary-200 mt-2">单选题刷题系统</p>
      </div>

      <!-- 注册表单 -->
      <div class="bg-white rounded-2xl shadow-xl p-8">
        <h2 class="text-2xl font-bold text-gray-800 mb-6 text-center">注册账号</h2>

        <div v-if="errorMsg" class="bg-red-50 text-red-600 px-4 py-3 rounded-lg mb-4 text-sm">
          {{ errorMsg }}
        </div>

        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
            <input
              v-model="username"
              type="text"
              placeholder="请输入用户名"
              class="w-full px-4 py-3 border border-gray-200 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent outline-none transition"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">密码</label>
            <input
              v-model="password"
              type="password"
              placeholder="请输入密码（至少6位）"
              class="w-full px-4 py-3 border border-gray-200 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent outline-none transition"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">确认密码</label>
            <input
              v-model="confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              class="w-full px-4 py-3 border border-gray-200 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent outline-none transition"
              @keyup.enter="handleRegister"
            />
          </div>

          <button
            @click="handleRegister"
            :disabled="loading"
            class="w-full py-3 bg-primary-600 text-white font-medium rounded-lg hover:bg-primary-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </div>

        <div class="mt-6 text-center">
          <span class="text-gray-500 text-sm">已有账号？</span>
          <router-link to="/login" class="text-primary-600 font-medium text-sm hover:underline ml-1">
            立即登录
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>
