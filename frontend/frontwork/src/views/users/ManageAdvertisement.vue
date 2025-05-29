<template>
  <div class="manage-advertisement-container">
    <el-card class="header-card">
      <div class="header">
        <h2>广告管理</h2>
        <el-button type="primary" @click="refreshData">刷新数据</el-button>
      </div>
    </el-card>

    <el-card class="content-card">
      <el-tabs v-model="activeTab" stretch>
        <el-tab-pane label="商品列表" name="products">
          <div v-if="loading" class="loading">
            <el-skeleton :rows="6" animated />
          </div>
          <div v-else class="product-list">
            <div v-for="product in products" :key="product.id" class="product-card">
              <div class="product-image">
                <el-image
                    :src="product.cover || 'https://dummyimage.com/300x200/cccccc/000000&text=No+Image'"
                    fit="cover"
                    class="image"
                    :alt="product.productName"
                />
              </div>
              <div class="product-info">
                <h3 class="product-name">{{ product.productName }}</h3>
                <div class="product-price">¥{{ product.price.toFixed(2) }}</div>
                <div class="product-description">{{ product.description }}</div>
                <div class="product-actions">
                  <el-button
                      type="primary"
                      @click="openAdvertisementDialog(product)"
                      :loading="settingAdId === product.id"
                  >
                    设置为广告
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="广告列表" name="advertisements">
          <div v-if="loading" class="loading">
            <el-skeleton :rows="6" animated />
          </div>
          <div v-else class="product-list">
            <div v-for="ad in advertisements" :key="ad.advertisementId" class="product-card">
              <div class="product-image">
                <el-image
                    :src="ad.imageUrl || 'https://dummyimage.com/300x200/cccccc/000000&text=No+Image'"
                    fit="cover"
                    class="image"
                    :alt="ad.title"
                />
              </div>
              <div class="product-info">
                <h3 class="product-name">{{ ad.title }}</h3>
                <div class="product-price">广告ID: {{ ad.advertisementId }}</div>
                <div class="product-description">{{ ad.content }}</div>
                <div class="product-actions">
                  <el-button
                      type="danger"
                      @click="removeAdvertisement(ad.advertisementId)"
                      :loading="removingAdId === ad.advertisementId"
                  >
                    撤销广告
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 广告内容输入对话框 -->
    <el-dialog
        v-model="dialogVisible"
        title="设置广告词"
        width="30%"
        :before-close="handleClose"
    >
      <el-input
          v-model="adContent"
          type="textarea"
          :rows="4"
          placeholder="请输入广告词"
          maxlength="200"
          show-word-limit
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSetAdvertisement" :loading="confirmLoading">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllProducts } from '../../api/products'
import {
  fetchAllAdvertisements,
  createAdvertisement,
  deleteAdvertisement
} from '../../api/advertisements'
import type { ProductVO, AdvertisementVO } from '../../api/types'

// 数据状态
const loading = ref(true)
const products = ref<ProductVO[]>([])
const advertisements = ref<AdvertisementVO[]>([])
const activeTab = ref('products')
const settingAdId = ref<number | null>(null)
const removingAdId = ref<number | null>(null)

// 对话框相关状态
const dialogVisible = ref(false)
const adContent = ref('')
const currentProduct = ref<ProductVO | null>(null)
const confirmLoading = ref(false)

// 获取所有商品
const fetchProducts = async () => {
  try {
    loading.value = true
    const res = await getAllProducts()
    products.value = Array.isArray(res) ? res : res.data
  } catch (error) {
    ElMessage.error('获取商品列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 获取所有广告
const fetchAdvertisements = async () => {
  try {
    loading.value = true
    const res = await fetchAllAdvertisements()
    advertisements.value = Array.isArray(res) ? res : res.data
  } catch (error) {
    ElMessage.error('获取广告列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 打开广告设置对话框
const openAdvertisementDialog = (product: ProductVO) => {
  currentProduct.value = product
  adContent.value = product.description || '' // 默认使用商品描述作为广告内容
  dialogVisible.value = true
}

// 确认设置广告
const confirmSetAdvertisement = async () => {
  if (!currentProduct.value || !currentProduct.value.id) {
    ElMessage.error('商品信息无效')
    return
  }

  if (!adContent.value.trim()) {
    ElMessage.warning('请输入广告词')
    return
  }

  try {
    confirmLoading.value = true
    const newAd: AdvertisementVO = {
      title: currentProduct.value.productName,
      content: adContent.value.trim(),
      imageUrl: currentProduct.value.cover,
      productId: String(currentProduct.value.id)
    }

    await createAdvertisement(newAd)
    ElMessage.success('设置广告成功')
    dialogVisible.value = false
    await fetchAdvertisements() // 刷新广告列表
  } catch (error) {
    ElMessage.error('设置广告失败')
    console.error(error)
  } finally {
    confirmLoading.value = false
  }
}

// 关闭对话框前的处理
const handleClose = (done: () => void) => {
  // 可以添加确认关闭的逻辑
  done()
}

// 撤销广告
const removeAdvertisement = async (adId?: number) => {
  if (!adId) return

  try {
    removingAdId.value = adId
    await deleteAdvertisement(adId)
    ElMessage.success('撤销广告成功')
    await fetchAdvertisements() // 刷新广告列表
  } catch (error) {
    ElMessage.error('撤销广告失败')
    console.error(error)
  } finally {
    removingAdId.value = null
  }
}

// 刷新数据
const refreshData = async () => {
  loading.value = true
  await Promise.all([fetchProducts(), fetchAdvertisements()])
}

// 初始化数据
onMounted(() => {
  refreshData()
})
</script>

<style scoped>
.manage-advertisement-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 50px;
}

.header-card {
  margin-bottom: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.content-card {
  min-height: 500px;
}

.loading {
  padding: 20px;
}

.product-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.product-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.product-image {
  height: 200px;
  width: 100%;
}

.image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 15px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #303133;
}

.product-price {
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 10px;
}

.product-description {
  color: #606266;
  font-size: 14px;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  flex-grow: 1;
}

.product-actions {
  display: flex;
  justify-content: center;
  margin-top: auto;
}

@media (max-width: 1200px) {
  .product-list {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .product-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .product-list {
    grid-template-columns: 1fr;
  }
}
</style>