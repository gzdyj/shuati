<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

const categories = ref<any[]>([])
const loading = ref(false)
const showEditModal = ref(false)
const showAddModal = ref(false)
const editingCategory = ref<any>(null)
const newCategory = ref({
  name: '',
  icon: '📁'
})

const icons = ['📁', '🏛️', '📜', '🌍', '⚖️', '💰', '🔬', '📚', '💻', '🎨', '🎵', '🏥']

const fetchCategories = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/admin/categories', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      categories.value = response.data.data
    }
  } catch (error) {
    console.error('获取分类列表失败', error)
  } finally {
    loading.value = false
  }
}

const openEditModal = (category: any) => {
  editingCategory.value = { ...category }
  showEditModal.value = true
}

const updateCategory = async () => {
  if (!editingCategory.value) return
  try {
    const response = await axios.put(`/api/admin/categories/${editingCategory.value.id}`, editingCategory.value, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      showEditModal.value = false
      fetchCategories()
    } else {
      alert(response.data.message)
    }
  } catch (error) {
    console.error('更新分类失败', error)
  }
}

const openAddModal = () => {
  newCategory.value = { name: '', icon: '📁' }
  showAddModal.value = true
}

const addCategory = async () => {
  if (!newCategory.value.name) {
    alert('请输入分类名称')
    return
  }
  try {
    const response = await axios.post('/api/admin/categories', newCategory.value, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      showAddModal.value = false
      fetchCategories()
    } else {
      alert(response.data.message)
    }
  } catch (error) {
    console.error('添加分类失败', error)
  }
}

const deleteCategory = async (category: any) => {
  if (!confirm(`确定要删除分类 "${category.name}" 吗？`)) return
  try {
    const response = await axios.delete(`/api/admin/categories/${category.id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      fetchCategories()
    } else {
      alert(response.data.message)
    }
  } catch (error) {
    console.error('删除分类失败', error)
  }
}

onMounted(() => {
  fetchCategories()
})
</script>

<template>
  <div class="min-h-screen bg-gray-100">
    <!-- 顶部导航 -->
    <header class="bg-gradient-to-r from-blue-600 to-blue-800 text-white px-6 py-4 shadow-lg">
      <div class="flex items-center justify-between">
        <div class="flex items-center">
          <router-link to="/admin" class="text-xl mr-4 hover:opacity-80">←</router-link>
          <h1 class="text-xl font-bold">分类管理</h1>
        </div>
        <button @click="openAddModal" class="px-4 py-1 bg-white/20 rounded hover:bg-white/30">
          + 新增分类
        </button>
      </div>
    </header>

    <!-- 分类列表 -->
    <div class="px-6 py-4">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div v-for="category in categories" :key="category.id" class="bg-white rounded-xl shadow-sm p-4">
          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <span class="text-3xl mr-3">{{ category.icon }}</span>
              <div>
                <h3 class="font-medium text-gray-800">{{ category.name }}</h3>
                <p class="text-sm text-gray-500">{{ category.questionCount }} 道题目</p>
              </div>
            </div>
            <div class="flex gap-2">
              <button @click="openEditModal(category)" class="text-blue-600 hover:text-blue-800 text-sm">编辑</button>
              <button @click="deleteCategory(category)" class="text-red-600 hover:text-red-800 text-sm">删除</button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="categories.length === 0" class="text-center text-gray-500 py-8">
        暂无分类数据
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <div v-if="showEditModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-xl p-6 w-80">
        <h3 class="text-lg font-bold mb-4">编辑分类</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm text-gray-600 mb-1">分类名称</label>
            <input v-model="editingCategory.name" class="w-full px-4 py-2 rounded-lg border border-gray-300" />
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">图标</label>
            <div class="grid grid-cols-6 gap-2">
              <button
                v-for="icon in icons"
                :key="icon"
                @click="editingCategory.icon = icon"
                :class="editingCategory.icon === icon ? 'bg-blue-100 border-blue-500' : 'bg-gray-100'"
                class="p-2 rounded-lg border text-xl hover:bg-blue-50"
              >
                {{ icon }}
              </button>
            </div>
          </div>
        </div>
        <div class="flex gap-2 mt-6">
          <button @click="showEditModal = false" class="flex-1 py-2 bg-gray-200 rounded-lg">取消</button>
          <button @click="updateCategory" class="flex-1 py-2 bg-blue-600 text-white rounded-lg">保存</button>
        </div>
      </div>
    </div>

    <!-- 新增弹窗 -->
    <div v-if="showAddModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-xl p-6 w-80">
        <h3 class="text-lg font-bold mb-4">新增分类</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm text-gray-600 mb-1">分类名称 *</label>
            <input v-model="newCategory.name" class="w-full px-4 py-2 rounded-lg border border-gray-300" placeholder="请输入分类名称" />
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">图标</label>
            <div class="grid grid-cols-6 gap-2">
              <button
                v-for="icon in icons"
                :key="icon"
                @click="newCategory.icon = icon"
                :class="newCategory.icon === icon ? 'bg-blue-100 border-blue-500' : 'bg-gray-100'"
                class="p-2 rounded-lg border text-xl hover:bg-blue-50"
              >
                {{ icon }}
              </button>
            </div>
          </div>
        </div>
        <div class="flex gap-2 mt-6">
          <button @click="showAddModal = false" class="flex-1 py-2 bg-gray-200 rounded-lg">取消</button>
          <button @click="addCategory" class="flex-1 py-2 bg-blue-600 text-white rounded-lg">添加</button>
        </div>
      </div>
    </div>
  </div>
</template>