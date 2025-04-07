<template>
  <div class="product-modify-container">
    <el-card class="modify-card">
      <template #header>
        <div class="card-header">
          <span>修改商品信息 (ID: {{ id }})</span>
        </div>
      </template>

      <el-form :model="form" label-width="120px">
        <el-form-item label="商品名称">
          <el-input v-model="form.title" placeholder="请输入商品名称"></el-input>
        </el-form-item>

        <el-form-item label="商品价格">
          <el-input-number
              v-model="form.price"
              :min="0"
              :precision="2"
              controls-position="right"
          ></el-input-number>
        </el-form-item>

        <el-form-item label="商品评分">
          <el-rate
              v-model="form.rate"
              :max="10"
              allow-half
              show-score
          />
        </el-form-item>

        <el-form-item label="库存数量">
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
          <el-form-item :label="'规格 ' + (index + 1)">
            <el-input v-model="spec.item" placeholder="规格名称" style="width: 200px"></el-input>
            <el-input v-model="spec.value" placeholder="规格值" style="width: 300px; margin-left: 10px"></el-input>
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

const route = useRoute()
const router = useRouter()
const id = route.params.id

const form = ref({
  title: '',
  price: 0,
  rate: 0,
  amount: 0,
  description: '',
  detail: '',
  specifications: [
    { id: '', item: '', value: '', productId: id }
  ]
})

const addSpec = () => {
  form.value.specifications.push({ id: '', item: '', value: '', productId: id })
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
    const payload = {
      id: id,
      title: form.value.title,
      price: form.value.price,
      rate: form.value.rate,
      amount: form.value.amount,
      description: form.value.description,
      detail: form.value.detail,
      specifications: form.value.specifications.map(spec => ({
        ...spec,
        productId: id
      }))
    }
    console.log('更新商品:', payload)
    // 实际项目中替换为API调用:
    // await api.updateProduct(payload)
    ElMessage.success('商品更新成功')
  } catch (error) {
    ElMessage.error('更新商品失败')
    console.error(error)
  }
}

const deleteProduct = () => {
  ElMessageBox.confirm('确定要删除这个商品吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      console.log('删除商品:', id)
      // 实际项目中替换为API调用:
      // await api.deleteProduct(id)
      ElMessage.success('商品删除成功')
      router.push('/allproduct')
    } catch (error) {
      ElMessage.error('删除商品失败')
      console.error(error)
    }
  })
}

const goBack = () => {
  router.push('/allproduct')
}

onMounted(async () => {
  if (id) {
    try {
      // 模拟API调用获取商品详情
      // const response = await api.getProductById(id)
      // form.value = response.data
      console.log(`加载商品ID: ${id}`)
      // 模拟数据
      form.value = {
        title: '示例商品',
        price: 99.99,
        rate: 4.5,
        amount: 100,
        description: '这是一个示例商品描述',
        detail: '这是商品的详细说明信息',
        specifications: [
          { id: '1', item: '颜色', value: '红色', productId: id },
          { id: '2', item: '尺寸', value: 'XL', productId: id }
        ]
      }
      ElMessage.success(`已加载商品ID: ${id}`)
    } catch (error) {
      ElMessage.error('加载商品信息失败')
      console.error(error)
    }
  } else {
    ElMessage.warning('未指定商品ID')
    router.push('/allproduct')
  }
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