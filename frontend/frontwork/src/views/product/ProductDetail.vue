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
      <!-- 图片和基本信息区域 -->
      <div class="main-content">
        <!-- 图片容器 -->
        <div class="image-wrapper">
          <el-image
              :src="product.cover"
              :alt="product.productName"
              fit="contain"
              class="product-image"
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

        <!-- 商品信息 -->
        <div class="info-wrapper">
          <h1 class="title">{{ product.productName }}</h1>
          <div class="meta-info">
            <div class="price-section">
              <span class="price">¥{{ formatPrice(product.price) }}</span>
              <el-tag :type="product.amount > 0 ? 'success' : 'danger'" effect="dark">
                {{ product.amount > 0 ? `库存 ${product.amount} 件` : '已售罄' }}
              </el-tag>
            </div>
            <el-rate
                v-model="product.rate"
                disabled
                :max="5"
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
      </div>

      <!-- 商品详情区域 -->
      <div class="detail-content">
        <el-divider />
        <h3>商品详情</h3>
        <div class="content" v-html="product.detail || '暂无商品详情'"></div>
      </div>
    </div>

    <div v-else class="not-found">
      <el-empty description="未找到该商品信息" />
      <el-button type="primary" @click="$router.push('/home/allproduct')">返回商品列表</el-button>
    </div>

    <el-dialog
        v-model="showAddDialog"
        title="选择购买数量"
        width="30%"
        :close-on-click-modal="false"
    >
      <div style="margin: 20px 0;">
        <el-input-number
            v-model="selectedQuantity"
            :min="1"
            :max="product?.amount || 1"
            label="购买数量"
        />
      </div>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmAddToCart">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { getProduct } from '../../api/products'
import type { ProductVO } from '../../api/products'
import { addCartItem } from "../../api/cartItem";
import { createOrderDirectly} from "../../api/orders";


// 路由相关
const route = useRoute()
const router = useRouter()
const productId = Number(route.params.id)

// 商品和状态
const product = ref<ProductVO | null>(null)
const loading = ref(true)
const isBuyingNow = ref(false)

// 加入购物车弹窗逻辑
const showAddDialog = ref(false)
const selectedQuantity = ref(1)

// 获取商品详情
const fetchProductDetail = async () => {
  try {
    loading.value = true
    const res = await getProduct(productId)
    const data = res.data
    product.value = {
      ...data,
      price: data.price ?? 0,
      specifications: data.specifications || []
    }
  } catch (error) {
    ElMessage.error('获取商品详情失败')
    router.push('/home/allproduct')
  } finally {
    loading.value = false
  }
}

// 图片错误处理
const handleImageError = (e: Event) => {
  const img = e.target as HTMLImageElement
  img.src = 'https://dummyimage.com/400x500/cccccc/000000&text=图片加载失败'
}

// 打开选择数量弹窗
const addToCart = () => {
  selectedQuantity.value = 1
  isBuyingNow.value = false
  showAddDialog.value = true
}

// 确认加入购物车
const confirmAddToCart = async () => {
  try {
    if (!product.value) return
    const quantity = selectedQuantity.value

    if (isBuyingNow.value) {
      // ✅ 立即购买跳转页面，并携带数量
      const createRes = await createOrderDirectly({
        amount: selectedQuantity.value * product.value.price,
        paymentMethod: 'alipay',
        productId : product.value.id,
        quantity : selectedQuantity.value
      })
      console.log(createRes.data)
      router.push({
        path: '/order',
        query: {
          orderId: createRes.data,
          isDirect : true,
          amount : selectedQuantity.value * product.value.price,
          productId: product.value.id,
          quantity : selectedQuantity.value
        }
      })
    } else {
      // ✅ 加入购物车
      await addCartItem({
        productId: product.value.id,
        quantity: quantity
      })
      ElMessage.success('已加入购物车')
    }

    showAddDialog.value = false
  } catch (error) {
    ElMessage.error(isBuyingNow.value ? '跳转失败' : '加入购物车失败')
  }
}

// 立即购买逻辑
const buyNow = () => {
  selectedQuantity.value = 1
  isBuyingNow.value = true
  showAddDialog.value = true
  // router.push({
  //   path: '/order',
  //   query: { productId: product.value?.id }
  // })
}

// 页面挂载后获取数据
onMounted(() => {
  if (isNaN(productId)) {
    ElMessage.error('无效的商品ID')
    router.push('/home/allproduct')
    return
  }
  fetchProductDetail()
})

// 格式化价格
const formatPrice = (price: number | undefined) =>
    typeof price === 'number' ? price.toFixed(2) : '暂无'
</script>

<style scoped>
.product-detail-container {
  padding: 20px;
  max-width: 1800px;
  margin: 0 50px;
}

.breadcrumb {
  margin-bottom: 20px;
  text-align: left;
}

.loading {
  padding: 40px 0;
  text-align: left;
}

.product-detail {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.main-content {
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
  text-align: left;
}

.image-wrapper {
  flex: 1;
  min-width: 300px;
  max-width: 700px;
  height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent; 
  border-radius: 8px;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
  text-align: left;
}

.info-wrapper {
  flex: 1;
  min-width: 300px;
  max-width: 600px;
  text-align: left;
}

.title {
  font-size: 24px;
  margin-bottom: 15px;
  text-align: left;
}

.price-section {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
  text-align: left;
}

.price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}

.specifications,
.description {
  margin-top: 20px;
  text-align: left;
}

.spec-item {
  margin: 8px 0;
  text-align: left;
}

.actions {
  margin-top: 30px;
  display: flex;
  gap: 15px;
  text-align: left;
}

.detail-content {
  width: 100%;
  margin-top: 20px;
  text-align: left;
}

.not-found {
  text-align: center;
  padding: 40px 0;
}

@media (max-width: 768px) {
  .image-wrapper {
    height: auto;
    aspect-ratio: 1/1;
    background: transparent; /* 移动端也改为透明背景 */
  }

  .main-content {
    flex-direction: column;
  }
}
</style>