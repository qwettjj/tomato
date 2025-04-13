<template>
  <div class="product-creation-container">
    <div class="product-creation">
      <h1>{{ currentStep === 1 ? '创建商品' : '添加商品规格' }}</h1>
      <el-form
          :model="form"
          :rules="rules"
          ref="productForm"
          label-width="120px"
          @submit.prevent="submitForm"
          v-if="currentStep === 1"
      >
        <!-- 商品名称 -->
        <el-form-item label="商品名称" prop="productName">
          <el-input
              v-model="form.productName"
              placeholder="请输入商品名称"
              maxlength="50"
              show-word-limit
          ></el-input>
        </el-form-item>

        <!-- 商品价格 -->
        <el-form-item label="商品价格" prop="price">
          <el-input-number
              v-model="form.price"
              :min="0"
              :step="0.01"
              :precision="2"
              controls-position="right"
              placeholder="请输入商品价格"
          ></el-input-number>
        </el-form-item>

        <!-- 商品库存 -->
        <el-form-item label="商品库存" prop="amount">
          <el-input-number
              v-model="form.amount"
              :min="1"
              :step="1"
              controls-position="right"
              placeholder="请输入库存数量"
          ></el-input-number>
        </el-form-item>

        <!-- 商品封面 -->
        <el-form-item label="商品封面" prop="cover">
          <el-upload
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :on-change="handleImageChange"
              :on-remove="handleImageRemove"
              :limit="1"
          >
            <div class="upload-area">
              <el-icon><Plus /></el-icon>
              <div class="upload-text">点击上传图片</div>
            </div>
            <template #file="{ file }">
              <img class="el-upload-list__item-thumbnail" :src="file.url"  alt=""/>
            </template>
          </el-upload>
        </el-form-item>

        <!-- 商品描述 -->
        <el-form-item label="商品描述" prop="description">
          <el-input
              v-model="form.description"
              type="textarea"
              :rows="3"
              placeholder="请输入商品描述"
              maxlength="255"
              show-word-limit
          ></el-input>
        </el-form-item>

        <!-- 商品详情 -->
        <el-form-item label="商品详情" prop="detail">
          <el-input
              v-model="form.detail"
              type="textarea"
              :rows="5"
              placeholder="请输入商品详细说明"
              maxlength="500"
              show-word-limit
          ></el-input>
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button
              type="primary"
              @click="nextStep"
              :disabled="!formValid"
              :loading="loading"
          >下一步</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 规格信息表单 -->
      <div v-if="currentStep === 2">
        <el-form
            :model="specificationForm"
            :rules="specRules"
            ref="specForm"
            label-width="120px"
        >
          <div v-for="(spec, index) in specificationForm.specs" :key="index" class="spec-item">
            <el-form-item
                :label="'规格 ' + (index + 1)"
                :prop="'specs.' + index + '.item'"
                :rules="specRules.item"
            >
              <el-input
                  v-model="spec.item"
                  placeholder="规格名称"
                  style="width: 200px; margin-right: 10px"
              ></el-input>
              <el-input
                  v-model="spec.value"
                  placeholder="规格内容"
                  style="width: 300px; margin-right: 10px"
              ></el-input>
              <el-button
                  type="danger"
                  circle
                  @click="removeSpec(index)"
                  :disabled="specificationForm.specs.length <= 1"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </el-form-item>
          </div>

          <el-form-item>
            <el-button type="primary" @click="addSpec">添加规格</el-button>
            <el-button @click="prevStep">上一步</el-button>
            <el-button
                type="success"
                @click="submitForm"
                :disabled="specificationForm.specs.length === 0"
                :loading="loading"
            >创建商品</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { addProduct } from '../../api/products'
import { uploadImage } from '../../api/tool'
import type { ProductVO, Specification } from '../../api/products'

// 当前步骤
const currentStep = ref(1)
const loading = ref(false)

// 表单数据
const form = ref<Omit<ProductVO, 'id' | 'specifications'>>({
  productName: '',
  price: 0,
  amount: 1,
  description: '',
  cover: '',
  detail: ''
})

// 规格表单数据
const specificationForm = ref<{ specs: Specification[] }>({
  specs: [{ item: '', value: '' }]
})

// 表单验证规则
const rules = {
  productName: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { max: 50, message: '长度不能超过50个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格不能小于0', trigger: 'blur' }
  ],
  amount: [
    { required: true, message: '请输入商品数量', trigger: 'blur' },
    { type: 'number', min: 1, message: '数量不能小于1', trigger: 'blur' }
  ],
  cover: [
    { required: true, message: '请上传商品封面', trigger: 'change' }
  ],
  description: [
    { max: 255, message: '长度不能超过255个字符', trigger: 'blur' }
  ],
  detail: [
    { max: 500, message: '长度不能超过500个字符', trigger: 'blur' }
  ]
}

// 规格验证规则
const specRules = {
  item: [
    { required: true, message: '请输入规格名称', trigger: 'blur' },
    { max: 50, message: '长度不能超过50个字符', trigger: 'blur' }
  ],
  value: [
    { required: true, message: '请输入规格内容', trigger: 'blur' },
    { max: 255, message: '长度不能超过255个字符', trigger: 'blur' }
  ]
}

// 表单有效性
const formValid = computed(() => {
  return (
      form.value.productName &&
      form.value.price > 0 &&
      form.value.amount > 0 &&
      form.value.cover
  )
})

// 图片上传处理
const handleImageChange = async (file: any) => {
  try {
    const loading = ElLoading.service({
      lock: true,
      text: '上传图片中...'
    })

    const formData = new FormData()
    formData.append('image', file.raw)
    const { data } = await uploadImage(formData)

    form.value.cover = data.url
    file.url = data.url
  } catch (error) {
    ElMessage.error('图片上传失败')
  } finally {
    loading.close()
  }
}

// 图片移除处理
const handleImageRemove = () => {
  form.value.cover = ''
}

// 规格操作
const addSpec = () => {
  specificationForm.value.specs.push({ item: '', value: '' })
}

const removeSpec = (index: number) => {
  if (specificationForm.value.specs.length > 1) {
    specificationForm.value.specs.splice(index, 1)
  }
}

// 步骤切换
const nextStep = () => {
  if (!formValid.value) {
    ElMessage.warning('请完成所有必填项')
    return
  }
  currentStep.value = 2
}

const prevStep = () => {
  currentStep.value = 1
}

// 提交表单
const submitForm = async () => {
  loading.value = true
  try {
    const productData: ProductVO = {
      ...form.value,
      specifications: specificationForm.value.specs
    }

    await addProduct(productData)

    ElMessage.success('商品创建成功')
    resetForm()
    currentStep.value = 1
    window.location.href = '/home/allproduct'
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '创建商品失败')
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  form.value = {
    productName: '',
    price: 0,
    amount: 1,
    description: '',
    cover: '',
    detail: ''
  }
  specificationForm.value = {
    specs: [{ item: '', value: '' }]
  }
}
</script>

<!-- 保持原有样式不变 -->