<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElLoading, ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import {
  getLikedPosts,
  type PostInfo
} from '../../api/posts'
import { getCircleOwner } from '../../api/circles'

const router = useRouter()
const posts = ref<PostInfo[]>([])
const currentUserId = ref(Number(sessionStorage.getItem('userId')))

// 加载数据
const loadData = async () => {
  const loading = ElLoading.service({ fullscreen: true })
  try {
    const likedPosts = await getLikedPosts()
    posts.value = likedPosts.map(post => ({
      ...post,
      createTime: new Date(post.createTime).toLocaleString(),
      isLiked: true
    }))

  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加载失败')
  } finally {
    loading.close()
  }
}

// 查看帖子详情
const viewPostDetail = (postId: number) => {
  router.push(`/home/circles/posts/${postId}`)
}

onMounted(() => {
  if (!currentUserId.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  loadData()
})
</script>

<template>
  <div class="circle-detail-container">
    <!-- 头部信息 -->
    <div class="header-section">
      <h1 class="title">我喜欢的帖子</h1>
      <div class="stats">
        <el-tag type="info">共 {{ posts.length }} 条</el-tag>
      </div>
    </div>

    <!-- 帖子列表 -->
    <div class="post-list">
      <el-card
          v-for="post in posts"
          :key="post.postId"
          class="post-item"
          shadow="hover"
          @click="viewPostDetail(post.postId)"
      >
        <template #header>
          <div class="post-header">
            <span class="post-title">{{ post.title }}</span>
          </div>
        </template>

        <div class="post-content">
          {{ post.content }}
        </div>

        <div class="post-meta">
          <el-tag size="small" type="danger">❤ {{ post.likeCount }}</el-tag>
          <el-tag size="small" type="info">💬 {{ post.commentCount }}</el-tag>
          <el-tag size="small" type="success">👁 {{ post.viewCount }}</el-tag>
          <el-tag size="small" type="warning">📅 {{ post.createTime }}</el-tag>
        </div>
      </el-card>

      <div v-if="posts.length === 0" class="no-post">
        暂无喜欢的帖子
      </div>
    </div>
  </div>
</template>

<style scoped>

.header-section {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  margin-bottom: 20px;
}

.title {
  font-size: 24px;
  color: #303133;
  margin: 0;
}

.no-post {
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 16px;
  background: #fff;
  border-radius: 8px;
}

/* 其他样式继承自CircleDetail.vue */
.circle-detail-container {
  max-width: 1600px;
  margin: 20px 50px;
  padding: 0 20px;
}

/* 头部信息样式 */
.header-section {
  display: flex;
  gap: 30px;
  margin-bottom: 30px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.cover-image {
  width: 300px;
  height: 200px;
  border-radius: 8px;
  flex-shrink: 0;
}

.basic-info {
  flex: 1;
  min-width: 0;
}

.title {
  margin: 0 0 15px 0;
  font-size: 2em;
  color: #303133;
  line-height: 1.2;
  text-align: left;
}

.description {
  color: #606266;
  margin-bottom: 20px;
  line-height: 1.7;
  white-space: pre-wrap;
  text-align: left;
}

.stats {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  justify-content: flex-start;
}

.actions {
  margin-top: 15px;
  display: flex;
  gap: 10px;
  justify-content: flex-start;
}

/* 帖子详情部分 */
.post-detail {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  margin-bottom: 20px;
  text-align: left;
}

.post-title {
  font-size: 1.5em;
  margin-bottom: 15px;
  color: #303133;
  text-align: left;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  color: #666;
  justify-content: flex-start;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 8px;
}

.post-content {
  line-height: 1.6;
  color: #606266;
  white-space: pre-wrap;
  text-align: left;
  padding-left: 0;
}

/* 访客提示 */
.visitor-notice {
  margin-bottom: 30px;
}

.alert-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.tip-text {
  margin: 0;
  color: #606266;
}

/* 发帖控制栏 */
.post-controls {
  margin-bottom: 20px;
  background: #fff;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  text-align: left;
}

/* 帖子列表样式 */
.post-list {
  display: grid;
  gap: 20px;
}

.post-item {
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
}

.post-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
}

.post-title {
  font-weight: 600;
  color: #303133;
  flex: 1;
  margin-right: 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.detail-link {
  color: #409EFF;
  text-decoration: none;
  font-size: 0.9em;
  white-space: nowrap;
}

.post-meta {
  display: flex;
  gap: 10px;
  margin-top: 15px;
  flex-wrap: wrap;
  justify-content: flex-start;
}

.like-tag {
  cursor: pointer;
  transition: all 0.3s;
  user-select: none;
}

.like-tag:hover {
  transform: scale(1.1);
}

/* 成员管理样式 */
.member-section {
  display: grid;
  gap: 30px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.member-category h3 {
  color: #303133;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 10px;
  margin-bottom: 15px;
  text-align: left;
}

.owner-info {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  text-align: left;
}

.owner-detail {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  margin: 8px 0;
  background: #f8f9fa;
  border-radius: 6px;
  transition: background 0.2s;
  text-align: left;
}

.member-item:hover {
  background: #f5f7fa;
}

.member-info {
  display: flex;
  flex-direction: column;
  margin-right: auto;
  min-width: 0;
}

.name {
  color: #303133;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.username {
  color: #909399;
  font-size: 0.8em;
}

.actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
  }

  .cover-image {
    width: 100%;
    height: auto;
    max-height: 200px;
  }

  .post-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .post-actions {
    width: 100%;
    justify-content: space-between;
  }

  .member-item {
    flex-wrap: wrap;
    padding: 15px;
  }

  .member-info {
    flex: 1;
    min-width: 200px;
  }

  .stats {
    gap: 8px;
  }

  .title {
    font-size: 1.5em;
  }
}

/* 对话框样式覆盖 */
:deep(.el-dialog) {
  border-radius: 12px;
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid #ebeef5;
  margin-right: 0;
}

:deep(.el-dialog__body) {
  padding: 20px;
}
</style>