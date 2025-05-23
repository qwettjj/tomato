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
    newCircle.value.cover = res.data.data // 假设返回的地址在 data.data 中
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
const showCreateDialog = ref(false)
const newCircle = ref<CircleVO>({
  id : 0 ,
  title: '',
  description: '',
  cover: '',
  status: 1,
  creatorId: 0,
  cover: '',
})
const userId = Number(sessionStorage.getItem('userId'))

// 加载圈子列表
const loadCircles = async (useSearch: boolean = false) => {
  const loading = ElLoading.service({ fullscreen: true })
  try {
    let response
    if (useSearch && searchKeyword.value) {
      response = await searchCircles(searchKeyword.value)
    } else {
      response = await getCircles()
    }
    circles.value = response.data.map((circle: any) => ({
      id: circle.id,
      title: circle.title,
      description: circle.description,
      cover: circle.cover,
      status : circle.status,
      creatorId: circle.creatorId
    }))
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加载失败')
  } finally {
    loading.close()
  }
}

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

// 初始化加载
onMounted(() => {
  console.log("当前用户Id : " + userId);
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
          @keyup.enter="loadCircles(true)"
          clearable
      >
        <template #append>
          <el-button icon="Search" @click="loadCircles(true)" />
        </template>
      </el-input>

      <el-button type="primary" @click="showCreateDialog = true">
        新建圈子
      </el-button>
    </div>

    <!-- 圈子列表 -->
    <el-row :gutter="20" class="list-container">
      <el-col
          v-for="circle in circles"
          :key="circle.id"
          :xs="24"
          :sm="12"
          :md="8"
      >
        <el-card class="circle-card">
          <template #header>
            <div class="card-header">
              <router-link
                  :to="`/home/circles/${circle.id}`"
                  class="circle-title"
              >
                {{ circle.title }}
              </router-link>
              <el-button
                  v-if="circle.creatorId === userId"
                  type="danger"
                  size="small"
                  @click="handleDelete(circle.id)"
              >
                删除
              </el-button>
            </div>
          </template>

          <el-image :src="circle.cover" fit="cover" class="cover-image" />
          <p class="description">{{ circle.description }}</p>
          <div class="meta-info">
            <el-tag>成员 {{ circle.memberCount }}</el-tag>
            <el-tag type="success">帖子 {{ circle.postCount }}</el-tag>
<!--            <el-tag> {{ circle.creatorId }}</el-tag>-->
          </div>
        </el-card>
      </el-col>
    </el-row>

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
.circle-list-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.operation-bar {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.circle-card {
  margin-bottom: 20px;
  transition: transform 0.2s;
}

.circle-card:hover {
  transform: translateY(-5px);
}

.cover-image {
  width: 100%;
  height: 200px;
  border-radius: 4px;
}

.description {
  margin: 10px 0;
  color: #666;
  height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.meta-info {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 0.9em;
}

.circle-title {
  font-size: 1.2em;
  font-weight: bold;
  color: var(--el-color-primary);
  text-decoration: none;
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