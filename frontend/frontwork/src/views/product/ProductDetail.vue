<template>
  <div class="product-detail-container">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/home/allproduct' }">所有商品</el-breadcrumb-item>
      <el-breadcrumb-item>商品详情</el-breadcrumb-item>
    </el-breadcrumb>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="6" animated />
    </div>

    <div v-else-if="product" class="product-detail">
      <el-row :gutter="40">
        <el-col :span="12">
          <div class="product-image">
            <el-image
                :src="product.cover"
                :alt="product.productName"
                fit="contain"
                style="width: 100%; max-height: 500px;"
                @error="handleImageError"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>图片加载失败</span>
                </div>
              </template>
            </el-image>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="product-info">
            <h1 class="title">{{ product.productName }}</h1>
            <div class="meta-info">
              <div class="price-section">
                <span class="price">¥{{ product.price.toFixed(2) }}</span>
                <el-tag :type="product.amount > 0 ? 'success' : 'danger'" effect="dark">
                  {{ product.amount > 0 ? `库存 ${product.amount} 件` : '已售罄' }}
                </el-tag>
              </div>
              <el-rate
                  v-model="product.rate"
                  disabled
                  :max="10"
                  allow-half
                  class="rating"
                  :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
              />
            </div>

            <div class="specifications">
              <h3>商品规格</h3>
              <div v-if="product.specifications.length === 0" class="no-spec">
                暂无规格信息
              </div>
              <div
                  v-for="(spec, index) in product.specifications"
                  :key="index"
                  class="spec-item"
              >
                <span class="spec-label">{{ spec.item }}：</span>
                <span class="spec-value">{{ spec.value }}</span>
              </div>
            </div>

            <div class="description">
              <h3>商品描述</h3>
              <p>{{ product.description || '暂无商品描述' }}</p>
            </div>

            <div class="actions">
              <el-button
                  type="primary"
                  size="large"
                  @click="addToCart"
                  :disabled="product.amount <= 0"
              >
                加入购物车
              </el-button>
              <el-button
                  type="success"
                  size="large"
                  @click="buyNow"
                  :disabled="product.amount <= 0"
              >
                立即购买
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-divider />

      <div class="product-content">
        <h3>商品详情</h3>
        <div class="content" v-html="product.detail || '暂无商品详情'"></div>
      </div>
    </div>

    <div v-else class="not-found">
      <el-empty description="未找到该商品信息" />
      <el-button type="primary" @click="$router.push('/home/allproduct')">返回商品列表</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { getProduct } from '../../api/products'
import type { ProductVO } from '../../api/products'

const route = useRoute()
const router = useRouter()
const productId = Number(route.params.id)
const product = ref<ProductVO | null>(null)
const loading = ref(true)

const fetchProductDetail = async () => {
  try {
    loading.value = true
    const data = await getProduct(productId)
    product.value = {
      ...data,
      // 确保specifications字段存在
      specifications: data.specifications || []
    }
  } catch (error) {
    ElMessage.error('获取商品详情失败')
    router.push('/home/allproduct')
  } finally {
    loading.value = false
  }
}

const handleImageError = (e: Event) => {
  const img = e.target as HTMLImageElement
  img.src = 'https://via.placeholder.com/400x500?text=Image+Not+Available'
}

const addToCart = () => {
  ElMessage.success('已加入购物车')
  // 实际应调用购物车API
}

const buyNow = () => {
  // 实际应跳转到订单确认页
  router.push({
    path: '/order/confirm',
    query: { productId: product.value?.id }
  })
}

onMounted(() => {
  if (isNaN(productId)) {
    ElMessage.error('无效的商品ID')
    router.push('/home/allproduct')
    return
  }
  fetchProductDetail()
})
</script>

<style scoped>
.product-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.breadcrumb {
  margin-bottom: 20px;
}

.product-detail {
  margin-top: 20px;
}

.title {
  font-size: 24px;
  margin-bottom: 15px;
  color: #303133;
}

.meta-info {
  margin-bottom: 30px;
}

.price-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 15px;
}

.price {
  font-size: 28px;
  color: #f56c6c;
  font-weight: bold;
}

.rating {
  margin: 10px 0;
}

.specifications {
  margin: 30px 0;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.spec-item {
  display: flex;
  margin: 10px 0;
  padding: 8px 12px;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.spec-label {
  color: #606266;
  min-width: 80px;
}

.spec-value {
  color: #303133;
  font-weight: 500;
}

.no-spec {
  color: #909399;
  text-align: center;
  padding: 10px;
}

.description {
  margin: 30px 0;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.description h3 {
  margin-bottom: 10px;
}

.actions {
  margin-top: 40px;
  display: flex;
  gap: 20px;
}

.product-content {
  margin-top: 40px;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
}

.product-content h3 {
  font-size: 20px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.content {
  line-height: 1.8;
  color: #606266;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.loading {
  padding: 50px;
}

.not-found {
  margin-top: 100px;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 20px;
}
</style>