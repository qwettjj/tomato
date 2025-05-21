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
                <span class="price">¥{{ formatPrice(product.price) }}</span>
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

    <!-- 🔥 加入购物车弹窗 -->
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
  padding: 24px;
  background-color: #fff;
}

.breadcrumb {
  margin-bottom: 24px;
}

.loading {
  padding: 40px;
  text-align: center;
}

.product-detail {
  padding: 16px 0;
}

.product-image {
  text-align: center;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
  padding: 20px;
}

.product-info {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 16px;
}

.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 12px;
}

.meta-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.price-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}

.rating {
  margin-top: 8px;
}

.specifications {
  margin-top: 20px;
}

.specifications h3 {
  font-size: 18px;
  margin-bottom: 8px;
}

.spec-item {
  margin-bottom: 6px;
}

.spec-label {
  font-weight: 500;
  margin-right: 4px;
}

.description {
  margin-top: 20px;
}

.description h3 {
  font-size: 18px;
  margin-bottom: 8px;
}

.description p {
  color: #666;
}

.actions {
  margin-top: 24px;
  display: flex;
  gap: 16px;
}

.product-content {
  margin-top: 40px;
}

.product-content h3 {
  font-size: 20px;
  margin-bottom: 12px;
}

.content {
  border-top: 1px solid #eee;
  padding-top: 12px;
  color: #333;
  line-height: 1.8;
}

.not-found {
  text-align: center;
  padding: 60px 0;
}

</style>
