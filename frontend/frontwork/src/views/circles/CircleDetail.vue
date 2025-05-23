<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElLoading, ElMessageBox, ElIcon } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getCircleAdmins,
  getAllCircleMembers,
  getCircleOwner,
  promoteToAdmin,
  demoteAdmin,
  leaveCircle,
  deleteCircle,
  joinCircle,
  getCircleRole,
  getCircleDetails,
  type CircleVO,
} from '../../api/circles'

import {
  getCirclePosts,
  createPost,
  likePost,
  unlikePost,
  deletePost,
  getLikedPosts,
  type PostInfo,
} from '../../api/posts'

import {
  type AccountVO
} from '../../api/accounts'

const route = useRoute()
const router = useRouter()
const circleId = ref(Number(route.params.circleId))
const activeTab = ref('posts')
const currentUserRole = ref<'VISITOR' | 'MEMBER' | 'ADMIN' | 'OWNER'>('VISITOR')
const currentUserId = ref(Number(sessionStorage.getItem('userId')))

// 数据状态
const circleInfo = ref<CircleVO | null>(null)
const posts = ref<PostInfo[]>([])
const admins = ref<AccountVO[]>([])
const members = ref<AccountVO[]>([])
const owner = ref<AccountVO | null>(null)
const likedPostIds = ref<Set<number>>(new Set())

// 发布相关状态
const showCreatePostDialog = ref(false)
const newPost = ref({
  title: '',
  content: '',
  circleId: circleId.value
})

// 加载数据
const loadData = async () => {
  const loading = ElLoading.service({ fullscreen: true })
  try {
    const [roleRes, detailRes, postsRes, adminsRes, membersRes, ownerRes] = await Promise.all([
      getCircleRole(circleId.value),
      getCircleDetails(circleId.value),
      getCirclePosts(circleId.value),
      getCircleAdmins(circleId.value),
      getAllCircleMembers(circleId.value),
      getCircleOwner(circleId.value)
    ])

    const likedRes = await getLikedPosts()
    likedPostIds.value = new Set(likedRes.map(post => post.postId))

    currentUserRole.value = roleRes.data || 'VISITOR'
    circleInfo.value = detailRes.data
    admins.value = adminsRes.data
    members.value = membersRes.filter(m => m.id !== ownerRes?.id)
    owner.value = ownerRes.data

    posts.value = postsRes.map(post => ({
      ...post,
      createTime: new Date(post.createTime).toLocaleString(),
      isLiked: likedPostIds.value.has(post.postId)
    }))

  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加载失败')
  } finally {
    loading.close()
  }
}

// 加入圈子
const handleJoinCircle = async () => {
  try {
    await joinCircle(circleId.value)
    ElMessage.success('加入成功')
    currentUserRole.value = 'MEMBER'
    await loadData()
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '加入失败')
  }
}

// 退出圈子
const handleLeaveCircle = async () => {
  try {
    await ElMessageBox.confirm('确定要退出该圈子吗？', '操作确认', { type: 'warning' })
    await leaveCircle(circleId.value)
    ElMessage.success('已退出圈子')
    await router.push('/home/circles')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 删除圈子
const handleDeleteCircle = async () => {
  try {
    await ElMessageBox.confirm('确定要永久删除该圈子吗？', '危险操作', {
      type: 'error',
      confirmButtonText: '确认删除'
    })
    await deleteCircle(circleId.value)
    ElMessage.success('圈子已删除')
    await router.push('/circles')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 发布新帖
const handleCreatePost = async () => {
  try {
    await ElMessageBox.confirm('确定要发布此帖子吗？', '发布确认')
    await createPost({
      ...newPost.value,
      circleId: circleId.value
    })
    ElMessage.success('发布成功')
    showCreatePostDialog.value = false
    newPost.value = { title: '', content: '', circleId: circleId.value }
    await loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败')
    }
  }
}

// 点赞/取消点赞
const toggleLike = async (post: PostInfo) => {
  try {
    const tempLiked = new Set(likedPostIds.value)
    const originalLikeCount = post.likeCount

    if (tempLiked.has(post.postId)) {
      tempLiked.delete(post.postId)
      post.likeCount--
      likedPostIds.value = tempLiked
      await unlikePost(post.postId)
    } else {
      tempLiked.add(post.postId)
      post.likeCount++
      likedPostIds.value = tempLiked
      await likePost(post.postId)
    }

  } catch (error) {
    const tempLiked = new Set(likedPostIds.value)
    if (tempLiked.has(post.postId)) {
      tempLiked.delete(post.postId)
      post.likeCount--
    } else {
      tempLiked.add(post.postId)
      post.likeCount++
    }
    likedPostIds.value = tempLiked
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    post.isLiked = likedPostIds.value.has(post.postId)
  }
}

// 删除帖子
const handleDeletePost = async (postId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除此帖子吗？', '危险操作', {
      type: 'error',
      confirmButtonText: '确认删除'
    })
    await deletePost(postId)
    ElMessage.success('删除成功')
    await loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 权限检查
const canDeletePost = (post: PostInfo) => {
  return currentUserRole.value === 'OWNER' ||
      currentUserRole.value === 'ADMIN' ||
      post.accountId === currentUserId.value
}

// 查看帖子详情
const viewPostDetail = (postId: number) => {
  router.push(`/home/circles/posts/${postId}`)
}

// 成员管理操作
const handlePromote = async (userId: number) => {
  try {
    await ElMessageBox.confirm('确定要晋升该用户为管理员吗？', '操作确认', { type: 'warning' })
    await promoteToAdmin(circleId.value, userId)
    ElMessage.success('晋升成功')
    await loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleDemote = async (userId: number) => {
  try {
    await ElMessageBox.confirm('确定要撤销该用户的管理员权限吗？', '操作确认', { type: 'warning' })
    await demoteAdmin(circleId.value, userId)
    ElMessage.success('撤销成功')
    await loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  if (isNaN(circleId.value)) {
    ElMessage.error('无效的圈子ID')
    router.push('/home/circles')
    return
  }
  loadData()
})
</script>

<template>
  <div class="circle-detail-container">
    <!-- 头部信息 -->
    <div class="header-section">
      <el-image class="cover-image" :src="circleInfo?.cover" fit="cover" />
      <div class="basic-info">
        <h1 class="title">{{ circleInfo?.title }}</h1>
        <div class="stats">
          <el-tag type="info">成员 {{ circleInfo?.memberCount }}</el-tag>
          <el-tag type="success">帖子 {{ circleInfo?.postCount }}</el-tag>
          <el-tag :type="circleInfo?.status === 1 ? 'success' : 'danger'">
            {{ circleInfo?.status === 1 ? '开放中' : '已关闭' }}
          </el-tag>
        </div>
        <p class="description">{{ circleInfo?.description }}</p>
        <div class="actions">
          <el-button
              v-if="currentUserRole === 'VISITOR'"
              type="primary"
              @click="handleJoinCircle"
          >
            加入圈子
          </el-button>
          <el-button
              v-if="currentUserRole === 'MEMBER'"
              type="danger"
              @click="handleLeaveCircle"
          >
            退出圈子
          </el-button>
          <el-button
              v-if="currentUserRole === 'OWNER'"
              type="danger"
              @click="handleDeleteCircle"
          >
            解散圈子
          </el-button>
        </div>
      </div>
    </div>

    <!-- 访客提示 -->
    <div v-if="currentUserRole === 'VISITOR'" class="visitor-notice">
      <el-alert type="info" show-icon :closable="false">
        <div class="alert-content">
          <p class="tip-text">您尚未加入本圈子，加入后可查看完整内容</p>
          <el-button
              type="primary"
              size="small"
              @click="handleJoinCircle"
          >
            立即加入
          </el-button>
        </div>
      </el-alert>
    </div>

    <!-- 帖子详情部分 -->
    <div v-if="posts.length > 0" class="post-detail">
      <h1 class="post-title">{{ posts[0].title }}</h1>
      <div class="post-meta">
        <span class="author">
          <img :src="owner?.avatar || 'default-avatar.png'" class="avatar" />
          {{ owner?.userName }}
        </span>
        <span class="time">{{ posts[0].createTime }}</span>
        <span class="stats">
          👁️ {{ posts[0].viewCount }} 赞 {{ posts[0].likeCount }}  💬 {{ posts[0].commentCount }}
        </span>
      </div>
      <div class="post-content">详情：{{ posts[0].content }}</div>
    </div>

    <!-- 发帖控制栏 -->
    <div class="post-controls" v-if="currentUserRole !== 'VISITOR'">
      <el-button type="primary" @click="showCreatePostDialog = true">
        <el-icon><Plus /></el-icon> 发新帖
      </el-button>
    </div>

    <!-- 内容标签页 -->
    <el-tabs v-model="activeTab" class="content-tabs">
      <!-- 帖子列表 -->
      <el-tab-pane label="帖子列表" name="posts">
        <div v-if="currentUserRole !== 'VISITOR'" class="post-list">
          <el-card
              v-for="post in posts"
              :key="post.postId"
              class="post-item"
              shadow="hover"
              @click="viewPostDetail(post.postId)"
          >
            <template #header>
              <div class="post-header" @click.stop>
                <span class="post-title">{{ post.title }}</span>
                <div class="post-actions">
                  <router-link
                      :to="`/home/circles/posts/${post.postId}`"
                      class="detail-link"
                  >
                    查看详情 →
                  </router-link>
                  <el-button
                      v-if="canDeletePost(post)"
                      type="danger"
                      size="small"
                      @click.stop="handleDeletePost(post.postId)"
                  >
                    删除
                  </el-button>
                </div>
              </div>
            </template>

            <div class="post-content">
              {{ post.content }}
            </div>

            <div class="post-meta">
              <el-tag
                  size="small"
                  :type="post.isLiked ? 'danger' : 'info'"
                  @click.stop="toggleLike(post)"
                  class="like-tag"
              >
                {{ post.isLiked ? '❤' : '🤍' }} {{ post.likeCount }}
              </el-tag>
              <el-tag size="small" type="info">💬 {{ post.commentCount }}</el-tag>
              <el-tag size="small" type="success">👁 {{ post.viewCount }}</el-tag>
              <el-tag size="small" type="warning">📅 {{ post.createTime }}</el-tag>
            </div>
          </el-card>
        </div>
      </el-tab-pane>

      <!-- 成员管理 -->
      <el-tab-pane
          label="成员管理"
          name="members"
          v-if="['ADMIN', 'OWNER'].includes(currentUserRole)"
      >
        <div class="member-section">
          <!-- 拥有者 -->
          <div class="member-category">
            <h3>圈子拥有者</h3>
            <div class="owner-info">
              <el-avatar :size="50" :src="owner?.avatar" />
              <div class="owner-detail">
                <span class="name">{{ owner?.name }}</span>
                <span class="username">@{{ owner?.userName }}</span>
              </div>
            </div>
          </div>

          <!-- 管理员 -->
          <div class="member-category">
            <h3>管理员列表（{{ admins.length }}人）</h3>
            <div v-for="admin in admins" :key="admin.id" class="member-item">
              <el-avatar :size="40" :src="admin.avatar" />
              <div class="member-info">
                <span class="name">{{ admin.name }}</span>
                <span class="username">@{{ admin.userName }}</span>
              </div>
              <div class="actions">
                <el-button
                    v-if="currentUserRole === 'OWNER' && admin.id !== owner?.id"
                    size="small"
                    type="danger"
                    @click="handleDemote(admin.id)"
                >
                  撤销管理员
                </el-button>
              </div>
            </div>
          </div>

          <!-- 普通成员 -->
          <div class="member-category">
            <h3>普通成员（{{ members.length }}人）</h3>
            <div v-for="member in members" :key="member.id" class="member-item">
              <el-avatar :size="40" :src="member.avatar" />
              <div class="member-info">
                <span class="name">{{ member.name }}</span>
                <span class="username">@{{ member.username }}</span>
              </div>
              <div class="actions">
                <el-button
                    size="small"
                    type="primary"
                    @click="handlePromote(member.id)"
                >
                  晋升管理员
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 发布新帖对话框 -->
    <el-dialog v-model="showCreatePostDialog" title="发布新帖子">
      <el-form :model="newPost" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="newPost.title" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input
              v-model="newPost.content"
              type="textarea"
              :rows="4"
              maxlength="500"
              show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreatePostDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreatePost">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.circle-detail-container {
  max-width: 1200px;
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