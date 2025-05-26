<template>
  <div class="product-creation-container">
    <div class="product-creation">
      <h1>{{ currentStep === 1 ? '创建商品' : '添加商品规格' }}</h1>

      <!-- 第一步：基础信息 -->
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

        <!-- 商品评分 -->
        <el-form-item label="商品评分" prop="rate">
          <el-rate
              v-model="form.rate"
              :max="5"
              show-score
              :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
          />
        </el-form-item>

        <!-- 是否冻结 -->
        <el-form-item label="冻结状态">
          <el-switch
              v-model="form.frozen"
              active-value="1"
              inactive-value="0"
              active-text="已冻结"
              inactive-text="未冻结"
          />
        </el-form-item>

        <!-- 商品封面 -->
        <el-form-item label="商品封面">
          <el-upload
              action="#"
              list-type="picture-card"
              :auto-upload="true"
              :before-upload="beforeUpload"
              :on-remove="handleImageRemove"
              :http-request="customRequest"
              :limit="1"
          >
            <div class="upload-area">
              <img
                  v-if="previewUrl || form.cover"
                  :src="previewUrl || form.cover"
                  class="cover-preview"
              >
              <div v-else class="upload-placeholder">
                <el-icon><Plus /></el-icon>
                <div class="upload-text">点击上传封面</div>
              </div>
              <div v-if="previewUrl || form.cover" class="cover-mask">
                <el-icon><Plus /></el-icon>
                <span>更换封面</span>
              </div>
            </div>
            <template #tip>
              <div class="upload-tip">支持JPG/PNG格式，大小不超过2MB</div>
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

      <!-- 第二步：规格信息 -->
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
                :disabled="!specFormValid"
                :loading="loading"
            >创建商品</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed,watch} from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { addProduct } from '../../api/products'
import { uploadImage } from '../../api/tool'
import type { ProductVO, Specification } from '../../api/products'
import {router} from "../../router";


// 响应式数据
const currentStep = ref(1)
const loading = ref(false)
const previewUrl = ref('')
const productForm = ref()
const specForm = ref()

// 表单数据
const form = ref<Omit<ProductVO, 'id' | 'specifications'>>({
  productName: '',
  price: 1,
  amount: 1,
  description: '',
  cover: '',
  detail: '',
  rate: 3, // 默认3星
  frozen: 0, // 默认未冻结
})

const specificationForm = ref<{ specs: Specification[] }>({
  specs: [{ item: '', value: '' }]
})

// 验证规则
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
  ],
  rate: [
    { type: 'number', min: 1, max: 5, message: '评分必须在1-5之间', trigger: 'blur' }
  ]
}

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

const formValid = computed(() => {
  return (
      form.value.productName &&
      form.value.price > 0 &&
      form.value.amount > 0 &&
      form.value.cover
  )
})

const specFormValid = computed(() => {
  return specificationForm.value.specs.every(spec =>
      spec.item.trim() &&
      spec.value.trim() &&
      spec.item.length <= 50 &&
      spec.value.length <= 255
  )
})

// 图片处理
const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB!')
    return false
  }

  previewUrl.value = URL.createObjectURL(file)
  return true
}

const customRequest = async (options:any) => {
  const formData = new FormData()
  formData.append('file',options.file);
  console.log("we are in customRequst")
  uploadImage(formData).then(res => {
    console.log(res.data.data)
    form.value.cover = res.data.data;
  })
}
const handleImageRemove = () => {
  form.value.cover = ''
  previewUrl.value = ''
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

// 分步操作
const nextStep = async () => {
  try {
    await productForm.value.validate()
    if (formValid.value) {
      currentStep.value = 2
    }
  } catch (error) {
    ElMessage.warning('请完成所有必填项')
  }
}

const prevStep = () => {
  currentStep.value = 1
}

// 表单提交
const submitForm = async () => {
  // try {
    if (currentStep.value === 1) return

    // 验证规格表单
    await specForm.value.validate()

    loading.value = true
    const productData: ProductVO = {
      ...form.value,
      specifications: specificationForm.value.specs
    }

    await addProduct(productData)
    ElMessage.success('商品创建成功')
    resetForm()
    currentStep.value = 1
    console.log("即将进入跳转")
    router.push({ path: "/allproduct" })


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
  previewUrl.value = ''
}

watch(formValid, (val) => {
  if (!val) {
    console.warn('[formValid 检查失败]')
    console.log('商品名填写:', !!form.value.productName)
    console.log('价格有效:', form.value.price > 0)
    console.log('库存有效:', form.value.amount > 0)
    console.log('封面有效：', !!form.value.cover)
  }
})
</script>

<style scoped>
/* 保持原有样式不变 */
.product-creation-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.product-creation {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.upload-area {
  position: relative;
  width: 150px;
  height: 150px;
}

.cover-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #8c939d;
}

.upload-text {
  margin-top: 8px;
  font-size: 12px;
}

.cover-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
  border-radius: 6px;
}

.cover-mask:hover {
  opacity: 1;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.spec-item {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 4px;
}

</style>