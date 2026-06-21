<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

const questions = ref<any[]>([])
const categories = ref<any[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const keyword = ref('')
const selectedCategory = ref<number | null>(null)
const showEditModal = ref(false)
const showAddModal = ref(false)
const editingQuestion = ref<any>(null)
const newQuestion = ref({
  content: '',
  correctAnswer: 'A',
  categoryId: null,
  explanation: '',
  options: [
    { optionId: 'A', content: '' },
    { optionId: 'B', content: '' },
    { optionId: 'C', content: '' },
    { optionId: 'D', content: '' }
  ]
})
const selectedIds = ref<number[]>([])

const fetchQuestions = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/admin/questions', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        keyword: keyword.value,
        categoryId: selectedCategory.value
      },
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      questions.value = response.data.data.records
      total.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取题目列表失败', error)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const response = await axios.get('/api/admin/categories', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      categories.value = response.data.data
    }
  } catch (error) {
    console.error('获取分类列表失败', error)
  }
}

const searchQuestions = () => {
  currentPage.value = 1
  fetchQuestions()
}

const changePage = (page: number) => {
  currentPage.value = page
  fetchQuestions()
}

const openEditModal = (question: any) => {
  editingQuestion.value = { ...question }
  showEditModal.value = true
}

const updateQuestion = async () => {
  if (!editingQuestion.value) return
  try {
    const response = await axios.put(`/api/admin/questions/${editingQuestion.value.id}`, editingQuestion.value, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      showEditModal.value = false
      fetchQuestions()
    }
  } catch (error) {
    console.error('更新题目失败', error)
  }
}

const openAddModal = () => {
  newQuestion.value = {
    content: '',
    correctAnswer: 'A',
    categoryId: null,
    explanation: '',
    options: [
      { optionId: 'A', content: '' },
      { optionId: 'B', content: '' },
      { optionId: 'C', content: '' },
      { optionId: 'D', content: '' }
    ]
  }
  showAddModal.value = true
}

const addQuestion = async () => {
  try {
    const response = await axios.post('/api/admin/questions', newQuestion.value, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      showAddModal.value = false
      fetchQuestions()
    }
  } catch (error) {
    console.error('添加题目失败', error)
  }
}

const deleteQuestion = async (id: number) => {
  if (!confirm('确定要删除此题目吗？')) return
  try {
    const response = await axios.delete(`/api/admin/questions/${id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      fetchQuestions()
    }
  } catch (error) {
    console.error('删除题目失败', error)
  }
}

const batchDelete = async () => {
  if (selectedIds.value.length === 0) {
    alert('请选择要删除的题目')
    return
  }
  if (!confirm(`确定要删除选中的 ${selectedIds.value.length} 个题目吗？`)) return
  try {
    const response = await axios.delete('/api/admin/questions/batch', {
      data: selectedIds.value,
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    if (response.data.code === 200) {
      selectedIds.value = []
      fetchQuestions()
    }
  } catch (error) {
    console.error('批量删除失败', error)
  }
}

const toggleSelect = (id: number) => {
  const index = selectedIds.value.indexOf(id)
  if (index > -1) {
    selectedIds.value.splice(index, 1)
  } else {
    selectedIds.value.push(id)
  }
}

onMounted(() => {
  fetchQuestions()
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
          <h1 class="text-xl font-bold">题目管理</h1>
        </div>
        <button @click="openAddModal" class="px-4 py-1 bg-white/20 rounded hover:bg-white/30">
          + 新增题目
        </button>
      </div>
    </header>

    <!-- 搜索栏 -->
    <div class="px-6 py-4">
      <div class="flex gap-2 flex-wrap">
        <input
          v-model="keyword"
          type="text"
          placeholder="搜索题目内容..."
          class="flex-1 min-w-200 px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:border-blue-500"
        />
        <select
          v-model="selectedCategory"
          class="px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:border-blue-500"
        >
          <option :value="null">全部分类</option>
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
        </select>
        <button @click="searchQuestions" class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
          搜索
        </button>
        <button v-if="selectedIds.length > 0" @click="batchDelete" class="px-6 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700">
          批量删除 ({{ selectedIds.length }})
        </button>
      </div>
    </div>

    <!-- 题目列表 -->
    <div class="px-6">
      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-2 py-3 text-center text-sm font-medium text-gray-600 w-10">
                <input type="checkbox" @change="selectedIds = questions.map(q => q.id)" />
              </th>
              <th class="px-4 py-3 text-left text-sm font-medium text-gray-600">ID</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-gray-600">题目内容</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-gray-600">分类</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-gray-600">正确答案</th>
              <th class="px-4 py-3 text-left text-sm font-medium text-gray-600">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="question in questions" :key="question.id" class="hover:bg-gray-50">
              <td class="px-2 py-3 text-center">
                <input
                  type="checkbox"
                  :checked="selectedIds.includes(question.id)"
                  @change="toggleSelect(question.id)"
                />
              </td>
              <td class="px-4 py-3 text-sm text-gray-800">{{ question.id }}</td>
              <td class="px-4 py-3 text-sm text-gray-800 max-w-300 truncate">{{ question.content }}</td>
              <td class="px-4 py-3 text-sm text-gray-600">{{ question.categoryName || '-' }}</td>
              <td class="px-4 py-3 text-sm">
                <span class="bg-green-100 text-green-600 px-2 py-1 rounded">{{ question.correctAnswer }}</span>
              </td>
              <td class="px-4 py-3 text-sm">
                <button @click="openEditModal(question)" class="text-blue-600 hover:text-blue-800 mr-2">编辑</button>
                <button @click="deleteQuestion(question.id)" class="text-red-600 hover:text-red-800">删除</button>
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

    <!-- 编辑弹窗 -->
    <div v-if="showEditModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 overflow-auto py-4">
      <div class="bg-white rounded-xl p-6 w-full max-w-2xl mx-4">
        <h3 class="text-lg font-bold mb-4">编辑题目</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm text-gray-600 mb-1">题目内容</label>
            <textarea v-model="editingQuestion.content" class="w-full px-4 py-2 rounded-lg border border-gray-300" rows="3"></textarea>
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">分类</label>
            <select v-model="editingQuestion.categoryId" class="w-full px-4 py-2 rounded-lg border border-gray-300">
              <option :value="null">无分类</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
            </select>
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">正确答案</label>
            <select v-model="editingQuestion.correctAnswer" class="w-full px-4 py-2 rounded-lg border border-gray-300">
              <option value="A">A</option>
              <option value="B">B</option>
              <option value="C">C</option>
              <option value="D">D</option>
            </select>
          </div>
          <div v-if="editingQuestion.options">
            <label class="block text-sm text-gray-600 mb-2">选项</label>
            <div v-for="(opt, index) in editingQuestion.options" :key="index" class="flex items-center gap-2 mb-2">
              <span class="w-8 text-center font-medium">{{ opt.optionId }}</span>
              <input v-model="opt.content" class="flex-1 px-4 py-2 rounded-lg border border-gray-300" />
            </div>
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">答案解析</label>
            <textarea v-model="editingQuestion.explanation" class="w-full px-4 py-2 rounded-lg border border-gray-300" rows="2"></textarea>
          </div>
        </div>
        <div class="flex gap-2 mt-6">
          <button @click="showEditModal = false" class="flex-1 py-2 bg-gray-200 rounded-lg">取消</button>
          <button @click="updateQuestion" class="flex-1 py-2 bg-blue-600 text-white rounded-lg">保存</button>
        </div>
      </div>
    </div>

    <!-- 新增弹窗 -->
    <div v-if="showAddModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 overflow-auto py-4">
      <div class="bg-white rounded-xl p-6 w-full max-w-2xl mx-4">
        <h3 class="text-lg font-bold mb-4">新增题目</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm text-gray-600 mb-1">题目内容 *</label>
            <textarea v-model="newQuestion.content" class="w-full px-4 py-2 rounded-lg border border-gray-300" rows="3"></textarea>
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">分类</label>
            <select v-model="newQuestion.categoryId" class="w-full px-4 py-2 rounded-lg border border-gray-300">
              <option :value="null">无分类</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
            </select>
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">正确答案 *</label>
            <select v-model="newQuestion.correctAnswer" class="w-full px-4 py-2 rounded-lg border border-gray-300">
              <option value="A">A</option>
              <option value="B">B</option>
              <option value="C">C</option>
              <option value="D">D</option>
            </select>
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-2">选项 *</label>
            <div v-for="(opt, index) in newQuestion.options" :key="index" class="flex items-center gap-2 mb-2">
              <span class="w-8 text-center font-medium">{{ opt.optionId }}</span>
              <input v-model="opt.content" class="flex-1 px-4 py-2 rounded-lg border border-gray-300" placeholder="选项内容" />
            </div>
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">答案解析</label>
            <textarea v-model="newQuestion.explanation" class="w-full px-4 py-2 rounded-lg border border-gray-300" rows="2"></textarea>
          </div>
        </div>
        <div class="flex gap-2 mt-6">
          <button @click="showAddModal = false" class="flex-1 py-2 bg-gray-200 rounded-lg">取消</button>
          <button @click="addQuestion" class="flex-1 py-2 bg-blue-600 text-white rounded-lg">添加</button>
        </div>
      </div>
    </div>
  </div>
</template>