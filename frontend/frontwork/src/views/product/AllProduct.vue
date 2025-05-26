<template>
  <div class="product-home">
    <!-- 广告轮播模块 -->
    <div class="ad-container">
      <el-carousel :interval="5000" height="400px" arrow="always">
        <el-carousel-item v-for="ad in ads" :key="ad.id">
          <router-link
              :to="ad.link"
              class="ad-link"
              @click.prevent="handleAdClick(ad)"
          >
            <img
                :src="ad.image"
                :alt="ad.alt"
                class="ad-image"
                @error="handleAdImageError"
            />
          </router-link>
        </el-carousel-item>
      </el-carousel>
    </div>

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
                        :max="5"
                        :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                    />
                    <span class="rating-text">{{ product.rate?.toFixed(1) || '暂无' }}</span>
                  </div>
                </div>
                <el-tag v-if="product.frozen==1" type="danger" effect="dark" class="stock-tag">
                  商品已冻结，停止售卖
                </el-tag>
                <el-tag v-else-if="product.amount <= 0" type="danger" effect="dark" class="stock-tag">
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
import { useRouter } from 'vue-router'
import { getAllProducts } from '../../api/products'
import { fetchAllAdvertisements } from '../../api/advertisements'
import { ElMessage } from 'element-plus'

interface Product {
  id: number
  productName: string
  price: number
  rate?: number
  cover: string
  amount: number
  frozen:number
}

// 商品数据
const products = ref<Product[]>([])
const loading = ref(true)
const error = ref<string | null>(null)
const router = useRouter()

// 广告数据
const ads = ref<Array<{
  id: number
  image: string
  link: string
  alt: string
}>>([])

// 获取商品数据
const fetchProducts = async () => {
  try {
    loading.value = true
    error.value = null
    const res = await getAllProducts()

    if (res.code === '200' && Array.isArray(res.data)) {
      products.value = res.data.map(item => ({
        id: item.id,
        productName: item.productName,
        price: item.price,
        rate: item.rate ?? 0,
        cover: item.cover,
        amount: item.amount,
        frozen: item.frozen,
      }))
    } else {
      throw new Error('接口返回异常')
    }
  } catch (err: any) {
    error.value = err.message || '加载商品数据失败'
    ElMessage.error(error.value)
  } finally {
    loading.value = false
  }
}

// 获取广告数据
const fetchAds = async () => {
  try {
    const res = await fetchAllAdvertisements()
    if (res.code === '200' && Array.isArray(res.data)) {
      ads.value = res.data.map(ad => ({
        id: ad.advertisementId || Date.now(),
        image: ad.imageUrl || require('@/assets/default-ad.jpg'),
        link: ad.productId ? `/productdetail/${ad.productId}` : '#',
        alt: ad.title || '广告图片'
      }))
    }
  } catch (err) {
    console.error('广告加载失败:', err)
    ads.value = [{
      id: 1,
      image: require('@/assets/default-ad.jpg'),
      link: '#',
      alt: '默认广告'
    }]
  }
}

// 广告点击处理
const handleAdClick = (ad: { link: string }) => {
  if (ad.link.startsWith('/productdetail')) {
    router.push(ad.link)
  } else {
    ElMessage.warning('该广告暂未关联商品')
  }
}

// 商品图片错误处理
const handleImageError = (e: Event) => {
  const img = e.target as HTMLImageElement
  img.src = 'https://picsum.photos/280/280?grayscale'
}

// 广告图片错误处理
const handleAdImageError = (e: Event) => {
  const img = e.target as HTMLImageElement
  img.src = require('@/assets/default-ad.jpg')
}

// 初始化加载
onMounted(() => {
  fetchProducts()
  fetchAds()
})
</script>


<style scoped>
.product-home {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 80vh;
}

/* 广告模块样式 */
.ad-container {
  margin: 20px 0 40px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.ad-link {
  display: block;
  width: 100%;
  height: 100%;
  position: relative;
  transition: transform 0.3s ease;
}

.ad-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.ad-link:hover .ad-image {
  transform: scale(1.03);
}

/* 商品列表样式 */
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

/* 轮播组件深度样式 */
:deep(.el-carousel__indicators) {
  bottom: 20px;
}

:deep(.el-carousel__button) {
  width: 40px;
  height: 4px;
  border-radius: 2px;
  background-color: rgba(255, 255, 255, 0.6);
}

:deep(.el-carousel__indicator.is-active .el-carousel__button) {
  background-color: #409EFF;
}
</style>