<template>
  <div class="product-modify-container">
    <el-card class="modify-card">
      <template #header>
        <div class="card-header">
          <span>修改商品信息 (ID: {{ id }})</span>
        </div>
      </template>

      <el-form :model="form" label-width="120px" v-loading="loading">
        <el-form-item label="商品名称" prop="productName">
          <el-input
              v-model="form.productName"
              placeholder="请输入商品名称"
          ></el-input>
        </el-form-item>

        <el-form-item label="商品价格" prop="price">
          <el-input-number
              v-model="form.price"
              :min="0"
              :precision="2"
              controls-position="right"
          ></el-input-number>
        </el-form-item>

        <el-form-item label="库存数量" prop="amount">
          <el-input-number
              v-model="form.amount"
              :min="0"
              controls-position="right"
          ></el-input-number>
        </el-form-item>

        <el-form-item label="商品描述">
          <el-input
              v-model="form.description"
              type="textarea"
              :rows="3"
              placeholder="请输入商品描述"
          ></el-input>
        </el-form-item>

        <el-form-item label="商品详情">
          <el-input
              v-model="form.detail"
              type="textarea"
              :rows="5"
              placeholder="请输入商品详细说明"
          ></el-input>
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
            ></el-input>
            <el-input
                v-model="spec.value"
                placeholder="规格值"
                style="width: 300px; margin-left: 10px"
            ></el-input>
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
  updateProduct,
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
  specifications: []
})

const loadProduct = async () => {
  try {
    loading.value = true
    const product = await getProduct(id)
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
    loading.value = true
    await updateProduct({
      ...form.value,
      id: id,
      specifications: form.value.specifications
    })
    ElMessage.success('商品更新成功')
    loadProduct() // 刷新数据
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
      router.push('/home/allproduct')
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const goBack = () => {
  router.push('/home/allproduct')
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
</style>