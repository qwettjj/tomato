<script setup lang="ts">
import {onMounted, ref, watch} from 'vue'
import {ElLoading, ElMessage, ElMessageBox} from 'element-plus'
import {type CircleVO, createCircle, deleteCircle, getCircles, searchCircles} from '../../api/circles'
import {useRouter} from 'vue-router'
import {Plus} from "@element-plus/icons-vue";
import {uploadImage} from '../../api/tool'

const previewCover = ref('')

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

  // 本地预览
  previewCover.value = URL.createObjectURL(file)
  return true
}

const customCoverUpload = async (options: any) => {
  const formData = new FormData()
  formData.append('file', options.file)

  try {
    const res = await uploadImage(formData)
    newCircle.value.cover = res.data.data
    ElMessage.success('封面上传成功')
  } catch (err) {
    ElMessage.error('封面上传失败')
  }
}

const handleCoverRemove = () => {
  newCircle.value.cover = ''
  previewCover.value = ''
}

const router = useRouter()
const searchKeyword = ref('')
const circles = ref<CircleVO[]>([])
const filteredCircles = ref<CircleVO[]>([]) // 新增：用于存储筛选后的圈子
const showCreateDialog = ref(false)
const newCircle = ref<CircleVO>({
  id: 0,
  title: '',
  description: '',
  cover: '',
  status: 1,
  creatorId: 0,
})
const userId = Number(sessionStorage.getItem('userId'))

// 加载圈子列表
const loadCircles = async () => {
  const loading = ElLoading.service({ fullscreen: true })
  try {
    const response = await getCircles()
    circles.value = response.data.map((circle: any) => ({
      id: circle.id,
      title: circle.title,
      description: circle.description,
      cover: circle.cover,
      status: circle.status,
      creatorId: circle.creatorId,
      memberCount: circle.memberCount || 0,
      postCount: circle.postCount || 0
    }))
    filterCircles() // 加载后立即执行筛选
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加载失败')
  } finally {
    loading.close()
  }
}

// 新增：筛选圈子函数
const filterCircles = () => {
  if (!searchKeyword.value) {
    filteredCircles.value = [...circles.value]
    return
  }

  const keyword = searchKeyword.value.toLowerCase()
  filteredCircles.value = circles.value.filter(circle =>
      circle.title.toLowerCase().includes(keyword)
  )}

// 监听搜索关键词变化
watch(searchKeyword, () => {
  filterCircles()
})

// 创建圈子
const handleCreateCircle = async () => {
  const loading = ElLoading.service({ fullscreen: true })
  try {
    await createCircle(newCircle.value)
    ElMessage.success('创建成功')
    showCreateDialog.value = false
    await loadCircles()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '创建失败')
  } finally {
    loading.close()
  }
}

// 删除圈子
const handleDelete = async (circleId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该圈子吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCircle(circleId)
    ElMessage.success('删除成功')
    await loadCircles()
  } catch (error) {
    // 取消删除不处理
  }
}

// 跳转到圈子详情
const navigateToCircle = (circleId: number) => {
  router.push(`/home/circles/${circleId}`)
}

// 初始化加载
onMounted(() => {
  console.log("当前用户Id : " + userId)
  loadCircles()
})
</script>

<template>
  <div class="circle-list-container">
    <!-- 操作栏 -->
    <div class="operation-bar">
      <el-input
          v-model="searchKeyword"
          placeholder="搜索圈子"
          clearable
      >
        <template #append>
          <el-button icon="Search" />
        </template>
      </el-input>

      <el-button type="primary" @click="showCreateDialog = true">
        新建圈子
      </el-button>
    </div>

    <!-- 圈子列表 -->
    <div class="horizontal-list-container">
      <div
          v-for="circle in filteredCircles"
          :key="circle.id"
          class="horizontal-circle-item"
          @click="navigateToCircle(circle.id)"
      >
        <div class="item-content">
          <el-image :src="circle.cover" fit="cover" class="horizontal-cover-image" />
          <div class="item-info">
            <div class="item-title">{{ circle.title }}</div>
            <div class="item-description">{{ circle.description }}</div>
            <div class="item-meta">
            </div>
          </div>
          <el-button
              v-if="circle.creatorId === userId"
              type="danger"
              size="small"
              class="delete-btn"
              @click.stop="handleDelete(circle.id)"
          >
            删除
          </el-button>
        </div>
      </div>

      <!-- 无搜索结果提示 -->
      <div v-if="filteredCircles.length === 0 && searchKeyword" class="no-result">
        没有找到匹配的圈子
      </div>
    </div>

    <!-- 创建对话框 -->
    <el-dialog v-model="showCreateDialog" title="创建新圈子">
      <el-form :model="newCircle" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="newCircle.title" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
              v-model="newCircle.description"
              type="textarea"
              :rows="3"
          />
        </el-form-item>
        <el-form-item label="封面">
          <el-upload
              action="#"
              list-type="picture-card"
              :auto-upload="true"
              :before-upload="beforeUpload"
              :on-remove="handleCoverRemove"
              :http-request="customCoverUpload"
              :limit="1"
          >
            <div class="upload-area">
              <img
                  v-if="previewCover || newCircle.cover"
                  :src="previewCover || newCircle.cover"
                  class="cover-preview"
              >
              <div v-else class="upload-placeholder">
                <el-icon><Plus /></el-icon>
                <div class="upload-text">点击上传封面</div>
              </div>
              <div v-if="previewCover || newCircle.cover" class="cover-mask">
                <el-icon><Plus /></el-icon>
                <span>更换封面</span>
              </div>
            </div>
            <template #tip>
              <div class="upload-tip">支持JPG/PNG格式，大小不超过2MB</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreateCircle">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 原有样式保持不变，新增无搜索结果样式 */
.no-result {
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 16px;
}

.circle-list-container {
  max-width: 1200px;
  margin: 20px 50px;
  padding: 0 20px;
}

.operation-bar {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.horizontal-list-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.horizontal-circle-item {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  cursor: pointer;
}

.horizontal-circle-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.item-content {
  display: flex;
  align-items: center;
  padding: 16px;
  gap: 16px;
}

.horizontal-cover-image {
  width: 120px;
  height: 80px;
  border-radius: 4px;
  object-fit: cover;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 1.1em;
  font-weight: bold;
  color: var(--el-color-primary);
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-description {
  color: #666;
  font-size: 0.9em;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-meta {
  display: flex;
  gap: 8px;
}

.delete-btn {
  margin-left: auto;
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
</style>