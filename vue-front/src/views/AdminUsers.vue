<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

const users = ref<any[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const keyword = ref('')
const showRoleModal = ref(false)
const selectedUser = ref<any>(null)
const newRole = ref('')

const fetchUsers = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/admin/users', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        keyword: keyword.value
      },
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      users.value = response.data.data.records
      total.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取用户列表失败', error)
  } finally {
    loading.value = false
  }
}

const searchUsers = () => {
  currentPage.value = 1
  fetchUsers()
}

const changePage = (page: number) => {
  currentPage.value = page
  fetchUsers()
}

const openRoleModal = (user: any) => {
  selectedUser.value = user
  newRole.value = user.role
  showRoleModal.value = true
}

const updateRole = async () => {
  if (!selectedUser.value) return
  try {
    const response = await axios.put(`/api/admin/users/${selectedUser.value.id}`, {
      role: newRole.value
    }, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      showRoleModal.value = false
      fetchUsers()
    }
  } catch (error) {
    console.error('更新角色失败', error)
  }
}

const deleteUser = async (user: any) => {
  if (!confirm(`确定要删除用户 "${user.username}" 吗？此操作不可恢复！`)) return
  try {
    const response = await axios.delete(`/api/admin/users/${user.id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      fetchUsers()
    }
  } catch (error) {
    console.error('删除用户失败', error)
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<template>
  <div class="min-h-screen bg-gray-100">
    <!-- 顶部导航 -->
    <header class="bg-gradient-to-r from-blue-600 to-blue-800 text-white px-6 py-4 shadow-lg">
      <div class="flex items-center justify-between">
        <div class="flex items-center">
          <router-link to="/admin" class="text-xl mr-4 hover:opacity-80">←</router-link>
          <h1 class="text-xl font-bold">用户管理</h1>
        </div>
      </div>
    </header>

    <!-- 搜索栏 -->
    <div class="px-6 py-4">
      <div class="flex gap-2">
        <input
          v-model="keyword"
          type="text"
          placeholder="搜索用户名..."
          class="flex-1 px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:border-blue-500"
        />
        <button @click="searchUsers" class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
          搜索
        </button>
      </div>
    </div>

    <!-- 用户列表 -->
    <div class="px-6">
      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-4 py-3 text-left text-sm font-medium text-gray-600">ID</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-gray-600">用户名</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-gray-600">角色</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-gray-600">答题数</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-gray-600">正确率</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-gray-600">注册时间</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-gray-600">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="user in users" :key="user.id" class="hover:bg-gray-50">
              <td class="px-4 py-3 text-sm text-gray-800">{{ user.id }}</td>
              <td class="px-4 py-3 text-sm text-gray-800">{{ user.username }}</td>
              <td class="px-4 py-3 text-sm">
                <span :class="user.role === 'ADMIN' ? 'bg-red-100 text-red-600' : 'bg-blue-100 text-blue-600'" class="px-2 py-1 rounded text-xs">
                  {{ user.role }}
                </span>
              </td>
              <td class="px-4 py-3 text-sm text-gray-800">{{ user.totalAnswered }}</td>
              <td class="px-4 py-3 text-sm text-gray-800">{{ user.correctRate?.toFixed(1) }}%</td>
              <td class="px-4 py-3 text-sm text-gray-500">{{ user.createTime }}</td>
              <td class="px-4 py-3 text-sm">
                <button @click="openRoleModal(user)" class="text-blue-600 hover:text-blue-800 mr-2">修改角色</button>
                <button @click="deleteUser(user)" class="text-red-600 hover:text-red-800">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div class="flex justify-center mt-4 gap-2">
        <button
          v-for="page in Math.ceil(total / pageSize)"
          :key="page"
          @click="changePage(page)"
          :class="currentPage === page ? 'bg-blue-600 text-white' : 'bg-white text-gray-600'"
          class="px-3 py-1 rounded hover:opacity-80"
        >
          {{ page }}
        </button>
      </div>
    </div>

    <!-- 修改角色弹窗 -->
    <div v-if="showRoleModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-xl p-6 w-80">
        <h3 class="text-lg font-bold mb-4">修改用户角色</h3>
        <p class="text-sm text-gray-600 mb-4">用户: {{ selectedUser?.username }}</p>
        <select v-model="newRole" class="w-full px-4 py-2 rounded-lg border border-gray-300 mb-4">
          <option value="USER">普通用户</option>
          <option value="ADMIN">管理员</option>
        </select>
        <div class="flex gap-2">
          <button @click="showRoleModal = false" class="flex-1 py-2 bg-gray-200 rounded-lg">取消</button>
          <button @click="updateRole" class="flex-1 py-2 bg-blue-600 text-white rounded-lg">确认</button>
        </div>
      </div>
    </div>
  </div>
</template>