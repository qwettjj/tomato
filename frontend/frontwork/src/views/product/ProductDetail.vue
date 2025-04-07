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
                :alt="product.title"
                fit="contain"
                style="width: 100%; max-height: 500px;"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="product-info">
            <h1 class="title">{{ product.title }}</h1>
            <div class="author">作者: {{ product.author }}</div>
            <div class="price-section">
              <span class="price">¥{{ product.price }}</span>
              <span class="rate">
                <el-rate
                    v-model="product.rate"
                    disabled
                    show-score
                    :max="10"
                    score-template="{value} 分"
                />
              </span>
            </div>
            <div class="description">
              <h3>商品描述</h3>
              <p>{{ product.description }}</p>
            </div>
            <div class="actions">
              <el-button type="primary" size="large" @click="addToCart">
                加入购物车
              </el-button>
              <el-button type="success" size="large" @click="buyNow">
                立即购买
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-divider />

      <div class="product-content">
        <h3>商品详情</h3>
        <div class="content" v-html="product.detail"></div>
      </div>
    </div>

    <div v-else class="not-found">
      <el-empty description="未找到该商品信息" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

// 定义商品类型
interface Book {
  id: number
  title: string
  author: string
  price: number
  rate: number
  description: string
  cover: string
  detail: string
}

const route = useRoute()
const productId = ref(Number(route.params.id))
const product = ref<Book | null>(null)
const loading = ref(true)

// 模拟API获取商品详情
const fetchProductDetail = async (id: number) => {
  try {
    // 这里应该是实际API调用，暂时用模拟数据
    loading.value = true

    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 500))

    // 模拟数据 - 实际项目中应该替换为API调用
    const mockBooks: Book[] = [
      {
        id: 1,
        title: 'JavaScript高级程序设计',
        author: '尼古拉斯·泽卡斯',
        price: 99.80,
        rate: 9.5,
        description: '前端开发必读经典',
        cover: 'https://via.placeholder.com/280x360?text=JavaScript高级程序设计',
        detail: '<p>本书全面介绍了JavaScript语言的核心概念，以及浏览器支持的API</p><p>适合有一定编程基础的读者学习</p>'
      },
      // 其他书籍数据...
    ]

    const foundProduct = mockBooks.find(book => book.id === id)
    if (foundProduct) {
      product.value = foundProduct
    }
  } catch (error) {
    ElMessage.error('获取商品详情失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const addToCart = () => {
  ElMessage.success('已加入购物车')
  // 实际项目中这里应该调用添加购物车的API
}

const buyNow = () => {
  ElMessage.success('跳转到结算页面')
  // 实际项目中这里应该跳转到结算页面
}

onMounted(() => {
  fetchProductDetail(productId.value)
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
  margin-bottom: 10px;
  color: #333;
}

.author {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

.price-section {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.price {
  font-size: 28px;
  color: #f56c6c;
  font-weight: bold;
  margin-right: 30px;
}

.description {
  margin: 30px 0;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.description h3 {
  margin-bottom: 10px;
}

.actions {
  margin-top: 40px;
}

.actions .el-button {
  margin-right: 20px;
}

.product-content {
  margin-top: 40px;
}

.product-content h3 {
  font-size: 20px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.content {
  line-height: 1.8;
}

.loading {
  padding: 50px;
}

.not-found {
  margin-top: 100px;
  text-align: center;
}
</style>