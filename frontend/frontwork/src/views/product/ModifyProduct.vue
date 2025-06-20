<template>
  <div class="product-modify-container">
    <el-card class="modify-card">
      <template #header>
        <div class="card-header">
          <span>修改商品信息 (ID: {{ id }})</span>
        </div>
      </template>

      <!-- ✅ 显示原始商品信息 -->
      <el-alert
          v-if="originalForm"
          title="商品原始信息"
          type="info"
          show-icon
          class="mb-4 left-align-alert"
      >
        <p>原名称：{{ originalForm.productName }}</p>
        <p>原价格：¥{{ originalForm.price }}</p>
        <p>原库存：{{ originalForm.amount }}</p>
        <p>原评分：{{ originalForm.rate || '无' }}</p>
        <p>原状态：{{ originalForm.frozen ? '已冻结' : '未冻结' }}</p>
        <p>原描述：{{ originalForm.description }}</p>
        <p>原详情：{{ originalForm.detail }}</p>
      </el-alert>

      <el-form :model="form" label-width="120px" v-loading="loading">
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入商品名称" />
        </el-form-item>

        <el-form-item label="商品价格" prop="price">
          <el-input-number
              v-model="form.price"
              :min="0"
              :precision="2"
              controls-position="right"
          />
        </el-form-item>

        <el-form-item label="库存数量" prop="amount">
          <el-input-number
              v-model="form.amount"
              :min="0"
              controls-position="right"
          />
        </el-form-item>

        <el-form-item label="商品评分" prop="rate">
          <el-rate
              v-model="form.rate"
              :max="5"
              show-score
              :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
          />
        </el-form-item>

        <el-form-item
            label="冻结状态"
            v-if="originalForm"
        >
        <el-switch
            v-model="form.frozen"
            :active-value="1"
            :inactive-value="0"
            active-text="已冻结"
            inactive-text="未冻结"
        />
        </el-form-item>

        <el-form-item label="商品描述">
          <el-input
              v-model="form.description"
              type="textarea"
              :rows="3"
              placeholder="请输入商品描述"
          />
        </el-form-item>

        <el-form-item label="商品详情">
          <el-input
              v-model="form.detail"
              type="textarea"
              :rows="5"
              placeholder="请输入商品详细说明"
          />
        </el-form-item>

        <el-divider />
        <h3>商品规格</h3>
        <div v-for="(spec, index) in form.specifications" :key="index" class="spec-item">
          <el-form-item
              :label="'规格 ' + (index + 1)"
              :prop="'specifications.' + index + '.item'"
          >
            <el-input
                v-model="spec.item"
                placeholder="规格名称"
                style="width: 200px"
            />
            <el-input
                v-model="spec.value"
                placeholder="规格值"
                style="width: 300px; margin-left: 10px"
            />
            <el-button
                type="danger"
                circle
                @click="removeSpec(index)"
                style="margin-left: 10px"
            >
              <el-icon><Delete /></el-icon>
            </el-button>
          </el-form-item>
        </div>

        <el-form-item>
          <el-button type="primary" @click="addSpec">添加规格</el-button>
        </el-form-item>

        <el-divider />
        <el-form-item>
          <el-button type="primary" @click="updateProduct">更新商品</el-button>
          <el-button type="danger" @click="deleteProduct">删除商品</el-button>
          <el-button @click="resetToOriginal">重置为原始信息</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import {
  getProduct,
  updateProduct as updateProductApi,
  deleteProduct as deleteProductApi,
  type ProductVO,
  type Specification
} from '../../api/products'

const route = useRoute()
const router = useRouter()
const id = Number(route.params.id)
const loading = ref(false)

const form = ref<ProductVO>({
  productName: '',
  price: 0,
  amount: 0,
  description: '',
  cover: '',
  detail: '',
  specifications: [],
  rate:3,
  frozen: 0,
})

// ✅ 保存原始数据
const originalForm = ref<ProductVO | null>(null)

const loadProduct = async () => {
  try {
    loading.value = true
    const res = await getProduct(id)
    const product = res.data

    originalForm.value = JSON.parse(JSON.stringify(product))
    form.value.rate = product.rate
    form.value.frozen = product.frozen
    form.value = {
      ...product,
      specifications: product.specifications || []
    }
  } catch (error) {
    ElMessage.error('加载商品信息失败')
    router.push('/home/allproduct')
  } finally {
    loading.value = false
  }
}

const addSpec = () => {
  form.value.specifications.push({ item: '', value: '' })
}

const removeSpec = (index: number) => {
  if (form.value.specifications.length > 1) {
    form.value.specifications.splice(index, 1)
  } else {
    ElMessage.warning('至少需要保留一个规格')
  }
}

const updateProduct = async () => {
  try {
    console.log("we are in update")
    loading.value = true
    await updateProductApi({
      ...form.value,
      id: id,
      specifications: form.value.specifications
    })
    ElMessage.success('商品更新成功')
    await loadProduct()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '更新失败')
  } finally {
    loading.value = false
  }
}

const deleteProduct = () => {
  ElMessageBox.confirm('确定要永久删除该商品吗？', '警告', {
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteProductApi(id)
      ElMessage.success('商品已删除')
      await router.push({name : 'AllProduct'})
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// ✅ 重置为原始信息
const resetToOriginal = () => {
  if (originalForm.value) {
    form.value = JSON.parse(JSON.stringify(originalForm.value))
    ElMessage.success('已重置为初始数据')
  }
}

const goBack = () => {
  router.push({ name: 'Dashboard' })
}

onMounted(() => {
  if (isNaN(id)) {
    ElMessage.error('无效的商品ID')
    router.push('/home/allproduct')
    return
  }
  loadProduct()
})
</script>

<style scoped>
.product-modify-container {
  padding: 20px;
}

.modify-card {
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.spec-item {
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.mb-4 {
  margin-bottom: 20px;
}
:deep(.left-align-alert) {
  text-align: left;
}
</style>
