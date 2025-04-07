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
        <el-form-item label="商品名称" prop="title">
          <el-input
              v-model="form.title"
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

        <!-- 商品数量 -->
        <el-form-item label="商品数量" prop="amount">
          <el-input-number
              v-model="form.amount"
              :min="1"
              :step="1"
              controls-position="right"
              placeholder="请输入商品数量"
          ></el-input-number>
        </el-form-item>

        <!-- 商品评分 -->
        <el-form-item label="初始评分" prop="rate">
          <el-rate
              v-model="form.rate"
              :max="10"
              show-score
              allow-half
              score-template="{value} 分"
          />
        </el-form-item>

        <!-- 商品分类标签 -->
        <el-form-item label="商品分类">
          <el-select
              v-model="form.tags"
              multiple
              placeholder="请选择分类标签"
              style="width: 100%"
          >
            <el-option
                v-for="tag in popularTags"
                :key="tag"
                :label="tag"
                :value="tag"
            ></el-option>
          </el-select>
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
            >创建商品</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'

// 当前步骤
const currentStep = ref(1)

// 静态标签数据
const popularTags = [
  '科幻', '玄幻', '历史', '文学', '科技',
  '艺术', '教育', '生活', '儿童', '其他'
]

// 表单数据
const form = ref({
  title: '',
  price: null,
  amount: 1,
  rate: null,
  description: '',
  detail: '',
  cover: null,
  tags: []
})

// 规格表单数据
const specificationForm = ref({
  specs: [
    { item: '', value: '' }
  ]
})

// 计算表单是否有效
const formValid = computed(() => {
  return (
      form.value.title &&
      form.value.price !== null &&
      form.value.amount !== null &&
      form.value.rate !== null &&
      form.value.cover
  )
})

// 表单验证规则
const rules = {
  title: [
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
  rate: [
    { required: true, message: '请设置商品评分', trigger: 'change' },
    { type: 'number', min: 0, max: 10, message: '评分必须在0-10之间', trigger: 'blur' }
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

// 图片上传处理
const handleImageChange = (file) => {
  form.value.cover = file.raw
  file.url = URL.createObjectURL(file.raw)
}

// 图片移除处理
const handleImageRemove = () => {
  form.value.cover = null
}

// 添加规格
const addSpec = () => {
  specificationForm.value.specs.push({ item: '', value: '' })
}

// 移除规格
const removeSpec = (index) => {
  if (specificationForm.value.specs.length > 1) {
    specificationForm.value.specs.splice(index, 1)
  }
}

// 下一步
const nextStep = () => {
  if (!formValid.value) {
    ElMessage.warning('请完成所有必填项')
    return
  }
  currentStep.value = 2
}

// 上一步
const prevStep = () => {
  currentStep.value = 1
}

// 提交表单
const submitForm = () => {
  // 格式化价格为两位小数
  const formattedPrice = parseFloat(form.value.price).toFixed(2)

  // 准备规格数据
  const specifications = specificationForm.value.specs.map(spec => ({
    item: spec.item,
    value: spec.value
  }))

  // 这里模拟API调用
  setTimeout(() => {
    ElMessage.success('商品创建成功！')
    console.log('创建的商品数据：', {
      ...form.value,
      price: formattedPrice,
      specifications
    })
    resetForm()
    currentStep.value = 1
  }, 1000)
}

// 重置表单
const resetForm = () => {
  form.value = {
    title: '',
    price: null,
    amount: 1,
    rate: null,
    description: '',
    detail: '',
    cover: null,
    tags: []
  }
  specificationForm.value = {
    specs: [
      { item: '', value: '' }
    ]
  }
}
</script>

<style scoped>
.product-creation-container {
  padding: 20px;
}

.product-creation {
  background: white;
  padding: 30px;
  border-radius: 8px;
  max-width: 800px;
  margin: 0 auto;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  color: #303133;
}

.upload-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.upload-text {
  margin-top: 8px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.el-upload-list__item-thumbnail {
  object-fit: contain;
}

.el-button[disabled] {
  cursor: not-allowed;
}

.spec-item {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>