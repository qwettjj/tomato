<template>
  <div class="product-home">
    <h1>全部商品</h1>
    <el-skeleton :loading="loading" animated :count="4" :throttle="500">
      <template #template>
        <el-row :gutter="20">
          <el-col v-for="i in 4" :key="i" :span="6" class="product-col">
            <el-skeleton-item variant="image" style="width: 280px; height: 280px" />
            <el-skeleton-item variant="text" style="width: 60%" />
            <el-skeleton-item variant="text" style="width: 40%" />
          </el-col>
        </el-row>
      </template>

      <el-row :gutter="20">
        <el-col
            v-for="product in products"
            :key="product.id"
            :span="6"
            class="product-col"
        >
          <router-link :to="`/productdetail/${product.id}`" class="product-link">
            <el-card class="product-card">
              <div class="image-container">
                <img
                    :src="product.cover"
                    :alt="product.productName"
                    class="product-image"
                    @error="handleImageError"
                >
              </div>
              <div class="product-info">
                <h3 class="product-title">{{ product.productName }}</h3>
                <div class="product-meta">
                  <span class="product-price">¥{{ product.price.toFixed(2) }}</span>
                  <div class="product-rating">
                    <el-rate
                        v-model="product.rate"
                        disabled
                        :max="10"
                        :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                    />
                    <span class="rating-text">{{ product.rate?.toFixed(1) || '暂无' }}</span>
                  </div>
                </div>
                <el-tag v-if="product.amount <= 0" type="danger" effect="dark" class="stock-tag">
                  已售罄
                </el-tag>
                <el-tag v-else class="stock-tag">
                  库存 {{ product.amount }} 件
                </el-tag>
              </div>
            </el-card>
          </router-link>
        </el-col>
      </el-row>
    </el-skeleton>

    <div v-if="error" class="error-message">
      <el-alert
          title="数据加载失败"
          type="error"
          :description="error"
          show-icon
          closable
      />
      <el-button type="primary" @click="fetchProducts">重新加载</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAllProducts } from '../../api/products'
import { ElMessage } from 'element-plus'

interface Product {
  id: number
  productName: string
  price: number
  rate?: number
  cover: string
  amount: number
}

const products = ref<Product[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

// 获取商品数据
const fetchProducts = async () => {
  try {
    loading.value = true
    error.value = null
    const data = await getAllProducts()
    console.log(data)
    products.value = data.map(item => ({
      ...item,
      rate: item.rate || 0  // 处理未评分情况
    }))
  } catch (err) {
    error.value = err.message || '加载商品数据失败'
    ElMessage.error(error.value)
  } finally {
    loading.value = false
  }
}

// 图片加载失败处理
const handleImageError = (e: Event) => {
  const img = e.target as HTMLImageElement
  img.src = 'https://via.placeholder.com/280x280?text=Image+Not+Available'
}

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.product-home {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 80vh;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  color: #303133;
}

.product-col {
  margin-bottom: 20px;
  padding: 0 10px;
}

.product-link {
  text-decoration: none;
}

.product-card {
  width: 280px;
  height: 420px;
  transition: transform 0.3s ease;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.image-container {
  width: 100%;
  height: 280px;
  overflow: hidden;
  position: relative;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.product-info {
  padding: 15px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.product-title {
  margin: 0;
  font-size: 16px;
  color: #303133;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-meta {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 5px;
}

.rating-text {
  font-size: 14px;
  color: #606266;
}

.stock-tag {
  margin-top: auto;
  align-self: flex-start;
}

.error-message {
  text-align: center;
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
}

.el-skeleton {
  width: 100%;
}

.el-skeleton__item {
  margin: 10px;
}
</style>