<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  total: number
  current: number
  size: number
}>()

const emit = defineEmits<{
  (e: 'page-change', page: number): void
  (e: 'size-change', size: number): void
}>()

const totalPages = computed(() => Math.max(1, Math.ceil(props.total / props.size)))

const startRow = computed(() => {
  if (props.total === 0) return 0
  return (props.current - 1) * props.size + 1
})

const endRow = computed(() => {
  return Math.min(props.current * props.size, props.total)
})

const pageNumbers = computed(() => {
  const pages: (number | string)[] = []
  const total = totalPages.value
  const current = props.current
  const neighborCount = 2

  if (total <= 7) {
    // 总页数少时全部显示
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    // 始终显示第一页
    pages.push(1)

    // 计算中间页码范围
    let start = Math.max(2, current - neighborCount)
    let end = Math.min(total - 1, current + neighborCount)

    // 调整范围，确保至少显示 neighborCount*2+1 个按钮
    if (current - neighborCount <= 2) {
      end = Math.min(total - 1, 1 + neighborCount * 2 + 1)
    }
    if (current + neighborCount >= total - 1) {
      start = Math.max(2, total - neighborCount * 2 - 1)
    }

    // 第一页后的省略号
    if (start > 2) {
      pages.push('...')
    }

    // 中间页码
    for (let i = start; i <= end; i++) {
      pages.push(i)
    }

    // 最后一页前的省略号
    if (end < total - 1) {
      pages.push('...')
    }

    // 始终显示最后一页
    pages.push(total)
  }

  return pages
})

const canPrev = computed(() => props.current > 1)
const canNext = computed(() => props.current < totalPages.value)

const goToPage = (page: number) => {
  if (page < 1 || page > totalPages.value || page === props.current) return
  emit('page-change', page)
}

const goToPrev = () => {
  if (canPrev.value) emit('page-change', props.current - 1)
}

const goToNext = () => {
  if (canNext.value) emit('page-change', props.current + 1)
}

const goToFirst = () => {
  if (props.current !== 1) emit('page-change', 1)
}

const goToLast = () => {
  if (props.current !== totalPages.value) emit('page-change', totalPages.value)
}

const pageSizes = [10, 20, 50, 100]

const onSizeChange = (e: Event) => {
  const target = e.target as HTMLSelectElement
  emit('size-change', Number(target.value))
}
</script>

<template>
  <div class="flex flex-col sm:flex-row items-center justify-between gap-3 mt-4 px-2" v-if="total > 0">
    <!-- 信息展示 -->
    <div class="text-sm text-gray-500">
      共 <span class="font-medium text-gray-700">{{ total }}</span> 条记录，
      第 <span class="font-medium text-gray-700">{{ startRow }}</span>-<span class="font-medium text-gray-700">{{ endRow }}</span> 条
    </div>

    <div class="flex items-center gap-1">
      <!-- 每页条数选择器 -->
      <div class="flex items-center gap-1 mr-3">
        <span class="text-sm text-gray-500">每页</span>
        <select
          :value="size"
          @change="onSizeChange"
          class="text-sm border border-gray-300 rounded px-2 py-1 focus:outline-none focus:border-blue-500 bg-white"
        >
          <option v-for="s in pageSizes" :key="s" :value="s">{{ s }}</option>
        </select>
        <span class="text-sm text-gray-500">条</span>
      </div>

      <!-- 分页按钮 -->
      <div class="flex items-center gap-1">
        <!-- 首页 -->
        <button
          @click="goToFirst"
          :disabled="!canPrev"
          class="px-2 py-1 text-sm rounded border border-gray-300 hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed bg-white text-gray-600"
          title="首页"
        >
          ⟪
        </button>

        <!-- 上一页 -->
        <button
          @click="goToPrev"
          :disabled="!canPrev"
          class="px-3 py-1 text-sm rounded border border-gray-300 hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed bg-white text-gray-600"
        >
          上一页
        </button>

        <!-- 页码 -->
        <template v-for="(page, index) in pageNumbers" :key="index">
          <span v-if="page === '...'" class="px-2 py-1 text-sm text-gray-400">...</span>
          <button
            v-else
            @click="goToPage(page as number)"
            :class="current === page ? 'bg-blue-600 text-white border-blue-600' : 'bg-white text-gray-600 border-gray-300 hover:bg-gray-100'"
            class="px-3 py-1 text-sm rounded border min-w-9 text-center"
          >
            {{ page }}
          </button>
        </template>

        <!-- 下一页 -->
        <button
          @click="goToNext"
          :disabled="!canNext"
          class="px-3 py-1 text-sm rounded border border-gray-300 hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed bg-white text-gray-600"
        >
          下一页
        </button>

        <!-- 末页 -->
        <button
          @click="goToLast"
          :disabled="!canNext"
          class="px-2 py-1 text-sm rounded border border-gray-300 hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed bg-white text-gray-600"
          title="末页"
        >
          ⟫
        </button>
      </div>
    </div>
  </div>

  <!-- 无数据时 -->
  <div v-else class="flex justify-center mt-4">
    <span class="text-sm text-gray-400">暂无数据</span>
  </div>
</template>
