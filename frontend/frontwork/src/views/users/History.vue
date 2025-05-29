<template>
  <div class="history-container">
    <div class="header">
      <h1><el-icon><Clock /></el-icon> 我的购买记录</h1>
      <p>查看您的所有历史订单</p>
    </div>

    <div class="content-wrapper">
      <!-- 筛选区域 -->
      <div class="filter-section">
        <div class="filter-card">
          <h3><el-icon><Filter /></el-icon> 筛选条件</h3>

          <div class="filter-group">
            <h4>时间范围</h4>
            <el-radio-group v-model="timeFilter">
              <el-radio-button label="all">全部</el-radio-button>
              <el-radio-button label="week">近一周</el-radio-button>
              <el-radio-button label="month">近一月</el-radio-button>
              <el-radio-button label="quarter">近三月</el-radio-button>
            </el-radio-group>
          </div>

          <div class="filter-group">
            <h4>价格范围</h4>
            <div class="price-range">
              <el-input-number v-model="minPrice" :min="0" :max="10000" placeholder="最低价" />
              <span class="to-text">至</span>
              <el-input-number v-model="maxPrice" :min="0" :max="10000" placeholder="最高价" />
            </div>
          </div>

          <el-button type="primary" class="filter-btn" @click="applyFilters">
            <el-icon><Search /></el-icon> 应用筛选
          </el-button>
          <el-button @click="resetFilters">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </div>

        <div class="stats-card">
          <div class="stat-item">
            <div class="stat-value">{{ totalRecords }}</div>
            <div class="stat-label">总记录数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">¥{{ totalAmount.toFixed(2) }}</div>
            <div class="stat-label">总消费额</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ totalItems }}</div>
            <div class="stat-label">总商品数</div>
          </div>
        </div>
      </div>

      <!-- 历史记录区域 -->
      <div class="history-section">
        <div class="action-bar">
          <el-checkbox v-model="selectAll" :indeterminate="isIndeterminate" @change="handleSelectAll">
            全选
          </el-checkbox>

          <el-button type="danger" :disabled="selectedRecords.length === 0" @click="batchDelete">
            <el-icon><Delete /></el-icon> 删除选中
          </el-button>

          <div class="spacer"></div>

          <el-input
              v-model="searchText"
              placeholder="搜索商品名称"
              class="search-input"
              clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="5" animated />
        </div>

        <!-- 历史记录列表 -->
        <div v-else class="history-list">
          <div v-if="filteredRecords.length === 0" class="empty-history">
            <el-empty description="暂无购买记录" />
            <el-button type="primary" @click="goShopping">去逛逛商品</el-button>
          </div>

          <div v-else>
            <div v-for="record in paginatedRecords" :key="record.historyId" class="history-item">
              <div class="item-header">
                <div class="order-info">
                  <el-checkbox v-model="record.selected" />
                  <span class="order-id">订单号: {{ record.orderId }}</span>
                </div>
                <div class="order-time">
                  <el-icon><Calendar /></el-icon> {{ formatDate(record.createTime) }}
                </div>
              </div>

              <div class="item-content">
                <div class="product-info">
                  <el-image
                      :src="record.product?.cover || 'https://dummyimage.com/100x150/f0f0f0/ccc&text=No+Image'"
                      class="product-image"
                      fit="cover"
                  >
                    <template #error>
                      <div class="image-error">
                        <el-icon><Picture /></el-icon>
                      </div>
                    </template>
                  </el-image>

                  <div class="product-details">
                    <h4 class="product-name">{{ record.product?.productName || '商品信息加载中...' }}</h4>

                    <!-- 评分区域 -->
                    <div class="rating-section">
                      <div class="rating-display">
                        <el-rate
                            v-model="record.product.rate"
                            :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                            disabled
                        />
                        <span v-if="record.product.rate" class="rating-text">
                          {{ record.product.rate.toFixed(1) }}
                        </span>
                        <span v-else class="rating-text">暂无评分</span>
                      </div>

                      <div v-if="!record.hasRated" class="rating-action">
                        <span class="rating-label">我的评分:</span>
                        <el-rate
                            v-model="record.userRating"
                            :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                            @change="handleRate(record)"
                        />
                        <el-button
                            v-if="record.userRating > 0"
                            type="primary"
                            size="small"
                            @click="submitRating(record)"
                            :loading="record.isRating"
                        >
                          提交评分
                        </el-button>
                      </div>
                      <div v-else class="rated-text">
                        <el-icon color="#67C23A"><SuccessFilled /></el-icon>
                        <span>已评分</span>
                      </div>
                    </div>

                    <div class="product-meta">
                      <span class="product-price">¥{{ record.product?.price?.toFixed(2) || '0.00' }}</span>
                      <span class="product-quantity">×{{ record.quantity }}</span>
                    </div>
                    <div class="specifications">
                      <span v-for="(spec, index) in record.product?.specifications || []" :key="index" class="spec-item">
                        {{ spec.item }}: {{ spec.value }}
                      </span>
                    </div>
                  </div>
                </div>

                <div class="order-details">
                  <div class="order-amount">
                    总价: <span class="highlight">¥{{ calculateTotal(record).toFixed(2) }}</span>
                  </div>

                  <div class="order-actions">
                    <el-button type="primary" size="small" @click="viewProduct(record.productId)">
                      <el-icon><View /></el-icon> 查看商品
                    </el-button>
                    <el-button type="danger" size="small" @click="deleteRecord(record.historyId)">
                      <el-icon><Delete /></el-icon> 删除
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页控件 -->
        <div class="pagination">
          <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="filteredRecords.length"
              :page-sizes="[5, 10, 20]"
              layout="total, sizes, prev, pager, next, jumper"
              background
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Calendar,
  Clock,
  Delete,
  DeleteFilled,
  Filter,
  Picture,
  Refresh,
  Search,
  SuccessFilled,
  View
} from '@element-plus/icons-vue'
import { batchDeleteHistory, getUserHistory, deleteHistory } from '../../api/histories'
import { getProduct, rateProduct } from '../../api/products'
import type { HistoryVO } from '../../api/histories'
import type { ProductVO } from '../../api/products'

const router = useRouter()

// 筛选条件
const timeFilter = ref('all')
const minPrice = ref<number | null>(null)
const maxPrice = ref<number | null>(null)
const searchText = ref('')
const loading = ref(true)

// 分页设置
const currentPage = ref(1)
const pageSize = ref(10)

// 选择控制
const selectAll = ref(false)
const isIndeterminate = ref(false)

// 历史记录数据
interface HistoryRecord extends HistoryVO {
  product?: ProductVO;
  selected: boolean;
  userRating: number;
  hasRated: boolean;
  isRating: boolean;

}

const historyRecords = ref<HistoryRecord[]>([])

// 计算属性
const filteredRecords = computed(() => {
  return historyRecords.value.filter(record => {
    // 时间筛选
    if (timeFilter.value !== 'all' && record.createTime) {
      const now = new Date()
      const recordDate = new Date(record.createTime)
      const timeDiff = now.getTime() - recordDate.getTime()
      const daysDiff = timeDiff / (1000 * 3600 * 24)

      if (timeFilter.value === 'week' && daysDiff > 7) return false
      if (timeFilter.value === 'month' && daysDiff > 30) return false
      if (timeFilter.value === 'quarter' && daysDiff > 90) return false
    }

    // 价格筛选
    const totalPrice = calculateTotal(record)
    if (minPrice.value !== null && totalPrice < minPrice.value) return false
    if (maxPrice.value !== null && totalPrice > maxPrice.value) return false

    // 搜索筛选
    if (searchText.value &&
        !record.product?.productName?.toLowerCase().includes(searchText.value.toLowerCase())) {
      return false
    }

    return true
  })
})

const paginatedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredRecords.value.slice(start, end)
})

const selectedRecords = computed(() => {
  return historyRecords.value.filter(record => record.selected)
})

const totalRecords = computed(() => filteredRecords.value.length)
const totalAmount = computed(() => {
  return filteredRecords.value.reduce((sum, record) => {
    return sum + calculateTotal(record)
  }, 0)
})
const totalItems = computed(() => {
  return filteredRecords.value.reduce((sum, record) => sum + record.quantity, 0)
})

// 方法
const formatDate = (dateString?: string) => {
  if (!dateString) return '未知时间'
  const date = new Date(dateString)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

const calculateTotal = (record: HistoryRecord) => {
  if (!record.product?.price) return 0
  return record.product.price * record.quantity
}

const handleSelectAll = (val: boolean) => {
  historyRecords.value.forEach(record => {
    record.selected = val
  })
}

const applyFilters = () => {
  currentPage.value = 1
  ElMessage.success('筛选条件已应用')
}

const resetFilters = () => {
  timeFilter.value = 'all'
  minPrice.value = null
  maxPrice.value = null
  searchText.value = ''
  currentPage.value = 1
}

const viewProduct = (productId?: number) => {
  if (!productId) return
  router.push({ path: `/productdetail/${productId}` })
}

const deleteRecord = async (id: number) => {
  if (!id) {
    ElMessage.error('无效的历史记录ID');
    return;
  }

  try {
    await ElMessageBox.confirm('确定删除这条购买记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteHistory(id)
    historyRecords.value = historyRecords.value.filter(record => record.historyId !== id)
    ElMessage.success('记录已删除')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除记录失败:', error)
      ElMessage.error(error.message || '删除失败，请稍后重试')
    }
  }
}

const batchDelete = async () => {
  if (selectedRecords.value.length === 0) return

  try {
    await ElMessageBox.confirm(`确定删除选中的记录吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const selectedIds = selectedRecords.value.map(r => r.historyId) as number[]
    await batchDeleteHistory(selectedIds)
    await fetchHistoryData()
    ElMessage.success(`已全部删除`)
    selectAll.value = false
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}



const goShopping = () => {
  router.push('/allproduct')
}

const handleRate = (record: HistoryRecord) => {
  if (record.userRating > 0) {
    submitRating(record)
  }
}

const submitRating = async (record: HistoryRecord) => {
  try {
    record.isRating = true
    await rateProduct(record.productId, record.userRating)

    if (record.product) {
      record.product.rate = record.userRating
    }
    record.hasRated = true
    ElMessage.success('评分提交成功！')
  } catch (error) {
    console.error('评分失败:', error)
    ElMessage.error('评分提交失败，请稍后重试')
  } finally {
    record.isRating = false
  }
}

const fetchHistoryData = async () => {
  try {
    loading.value = true
    const historyResponse = await getUserHistory()
    console.log("historyResponse:"+historyResponse)
    // 假设返回的是数组格式

    const historyData = Array.isArray(historyResponse) ? historyResponse : historyResponse.data
    console.log("historyData:"+historyData)
    console.log("historyResponse:"+historyResponse)

    const recordsWithProducts = await Promise.all(historyData.map(async (record: HistoryVO) => {
      if (!record.historyId) {
        console.warn('发现没有 historyId 的记录:', record)
        record.historyId = Date.now()
      }
      try {
        const productResponse = await getProduct(record.productId)
        const product = productResponse.data || productResponse

        return {
          ...record,
          product,
          selected: false,
          userRating: product.rate || 0,
          hasRated: false,
          isRating: false
        }
        historyRecords.value = recordsWithProducts
      } catch (error) {
        console.error(`无法获取商品 ${record.productId} 的信息`, error)
        return {
          ...record,
          product: undefined,
          selected: false,
          userRating: 0,
          hasRated: false,
          isRating: false
        }
      }
    })
    )

    historyRecords.value = recordsWithProducts
  } catch (error) {
    console.error('获取历史记录失败:', error)
    ElMessage.error('获取历史记录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}


onMounted(() => {
  fetchHistoryData()
})
</script>

<style scoped>
.history-container {
  max-width: 1400px;
  margin: 20px auto;
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.header h1 {
  font-size: 2.2rem;
  color: #2c3e50;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.header p {
  font-size: 1.1rem;
  color: #7f8c8d;
}

.content-wrapper {
  display: flex;
  gap: 20px;
}

.filter-section {
  flex: 0 0 280px;
}

.history-section {
  flex: 1;
}

.filter-card, .stats-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  padding: 20px;
  margin-bottom: 20px;
}

.filter-card h3, .stats-card h3 {
  font-size: 1.2rem;
  color: #34495e;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-group {
  margin-bottom: 25px;
}

.filter-group h4 {
  font-size: 1rem;
  color: #7f8c8d;
  margin-bottom: 12px;
}

.price-range {
  display: flex;
  align-items: center;
  gap: 10px;
}

.to-text {
  color: #95a5a6;
}

.filter-btn {
  width: 100%;
  margin-top: 10px;
  margin-bottom: 10px;
}

.stats-card {
  display: flex;
  justify-content: space-around;
  text-align: center;
}

.stat-item {
  padding: 10px;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: bold;
  color: #3498db;
}

.stat-label {
  font-size: 0.9rem;
  color: #7f8c8d;
}

.action-bar {
  background: #fff;
  border-radius: 12px;
  padding: 15px 20px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.spacer {
  flex: 1;
}

.search-input {
  width: 250px;
}

.history-list {
  min-height: 500px;
}

.history-item {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}

.history-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #eee;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.order-id {
  font-weight: bold;
  color: #2c3e50;
}

.order-time {
  color: #7f8c8d;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.9rem;
}

.item-content {
  display: flex;
  padding: 20px;
}

.product-info {
  flex: 1;
  display: flex;
  gap: 20px;
}

.product-image {
  width: 100px;
  height: 140px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-error {
  color: #c0c4cc;
  font-size: 24px;
}

.product-details {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 1;
}

.product-name {
  font-size: 1.1rem;
  color: #2c3e50;
  margin: 0 0 10px 0;
}

.rating-section {
  margin-bottom: 12px;
}

.rating-display {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.rating-text {
  margin-left: 10px;
  font-size: 0.9rem;
  color: #F7BA2A;
  font-weight: bold;
}

.rating-action {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.rating-label {
  font-size: 0.9rem;
  color: #606266;
}

.rated-text {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #67C23A;
  font-size: 0.9rem;
  padding: 5px 10px;
  background-color: #f0f9eb;
  border-radius: 4px;
  width: fit-content;
}

.product-meta {
  display: flex;
  gap: 15px;
  margin-bottom: 10px;
}

.product-price {
  font-weight: bold;
  color: #e74c3c;
}

.product-quantity {
  color: #7f8c8d;
}

.specifications {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 5px;
}

.spec-item {
  background: #f0f2f5;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 0.85rem;
  color: #606266;
}

.order-details {
  width: 250px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-end;
}

.order-amount {
  font-size: 1.1rem;
}

.highlight {
  font-weight: bold;
  color: #e74c3c;
  font-size: 1.2rem;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.empty-history {
  text-align: center;
  padding: 60px 0;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.loading-container {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  min-height: 500px;
}

@media (max-width: 992px) {
  .content-wrapper {
    flex-direction: column;
  }

  .filter-section {
    flex: 1;
  }

  .item-content {
    flex-direction: column;
    gap: 20px;
  }

  .order-details {
    width: 100%;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
  }
}

@media (max-width: 768px) {
  .action-bar {
    flex-wrap: wrap;
  }

  .search-input {
    width: 100%;
    margin-top: 15px;
  }

  .item-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .order-info {
    width: 100%;
  }

  .rating-action {
    flex-wrap: wrap;
  }
}
</style>