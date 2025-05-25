<template>
  <div class="post-container">
    <!-- 帖子详情部分 -->
    <div v-if="post" class="post-detail">
      <h1 class="post-title">{{ post.title }}</h1>
      <div class="post-meta">
        <span class="author">
          <img :src="authorInfo?.avatar || 'default-avatar.png'" class="avatar" />
          {{ authorInfo?.userName }}
        </span>
        <span class="time">{{ formatTime(post.createTime) }}</span>
        <span class="stats">
          👁️ {{ post.viewCount }} 🤍 {{ post.likeCount }} 💬 {{ post.commentCount }}
        </span>
      </div>
      <div class="post-content">详情：{{ post.content }}</div>
    </div>

    <!-- 评论输入框 -->
    <div class="comment-input">
      <textarea
          v-model="newComment"
          :placeholder="replyPlaceholder"
          @keydown.enter.exact.prevent="submitComment"
      ></textarea>
      <div class="action-buttons">
        <button @click="cancelReply" v-if="replyingTo">取消回复</button>
        <button @click="submitComment">发布</button>
        <button
            class="like-button"
            :class="{ 'liked': isLiked }"
            @click="handleLike"
        >
          {{ isLiked.value ? '' : '❤' }}
        </button>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comments">
      <div v-for="(comment, index) in comments" :key="comment.commentId" class="comment-item">
        <div class="comment-floor">{{ getFloorText(index) }}</div>
        <CommentItem
            :comment="comment"
            :depth="0"
            :currentUserId="currentUserId"
            @reply="handleReply"
            @delete="handleCommentDelete"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getPostDetail,
  likePost,
  unlikePost,
  judgeLiked,
  type PostInfo
} from '../../api/posts'
import {
  getPostComments,
  createComment,
  type CommentVO
} from '../../api/comments'
import { getUserInfo, type accountVO } from '../../api/accounts'
import CommentItem from './CommentItem.vue'

const route = useRoute()
const postId = parseInt(route.params.postId as string)

// 帖子相关状态
const post = ref<PostInfo>()
const authorInfo = ref<accountVO>()
const isLiked = ref(false)
const currentUserId = ref<number>(1) // 这里假设当前用户ID为1，实际应从登录状态获取

// 评论相关状态
const comments = ref<CommentVO[]>([])
const newComment = ref('')
const replyingTo = ref<{commentId: number, userName: string} | null>(null)

// 获取楼层文字
const getFloorText = (index: number) => {
  const floorNumber = index + 1
  return `${floorNumber}楼`
}

// 计算回复提示文本
const replyPlaceholder = computed(() => {
  return replyingTo.value
      ? `回复 @${replyingTo.value.userName}:`
      : '写下你的评论...'
})

// 获取帖子详情
const fetchPostDetail = async () => {
  const res = await getPostDetail(postId)
  post.value = res.data
  authorInfo.value = await getUserInfo(res.data.accountId)

  const res2 = await judgeLiked(postId)
  isLiked.value = res2.data

  currentUserId.value = sessionStorage.getItem('userId')
}

// 获取评论列表
const fetchComments = async () => {
  const res = await getPostComments(postId)
  comments.value = res
}

// 处理点赞
const handleLike = async () => {
  if (isLiked.value) {
    await unlikePost(postId)
    post.value.likeCount--
  } else {
    await likePost(postId)
    post.value.likeCount++
  }
  isLiked.value = !isLiked.value
}

// 处理回复
const handleReply = (payload: {commentId: number, userName: string}) => {
  replyingTo.value = payload
  newComment.value = `@${payload.userName} `
  setTimeout(() => {
    document.querySelector('.comment-input textarea')?.focus()
  }, 0)
}

// 取消回复
const cancelReply = () => {
  replyingTo.value = null
  newComment.value = ''
}

// 提交评论
const submitComment = async () => {
  if (!newComment.value.trim()) return

  const commentInfo = {
    postId: postId,
    content: newComment.value,
    parentId: replyingTo.value?.commentId || null
  }

  try {
    await createComment(commentInfo)
    newComment.value = ''
    replyingTo.value = null
    await fetchComments()
    ElMessage.success('评论成功')
  } catch (error) {
    ElMessage.error('评论失败')
  }
}

// 处理评论删除
const handleCommentDelete = (deletedCommentId: number) => {
  comments.value = comments.value.filter(comment => comment.commentId !== deletedCommentId)
  if (post.value) {
    post.value.commentCount--
  }
}

// 时间格式化
const formatTime = (time: string | Date) => {
  return new Date(time).toLocaleString()
}

onMounted(async () => {
  await fetchPostDetail()
  await fetchComments()
})
</script>

<style scoped>
.post-container {
  max-width: 1200px;
  margin: 20px 50px;
  padding: 0 20px;
}

.post-title {
  font-size: 24px;
  margin-bottom: 15px;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  color: #666;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 8px;
}

.post-content {
  line-height: 1.6;
  margin-bottom: 20px;
  text-align: left;
  padding-left: 0;
}

.comment-input {
  margin: 20px 0;
}

.comment-input textarea {
  width: 100%;
  height: 80px;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.like-button {
  padding: 8px 16px;
  background: #f0f0f0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  color: #666;
}

.like-button.liked {
  background: #fff0f0;
  color: #ff4d4d;
}

.comment-input button {
  padding: 8px 16px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.comments {
  margin-top: 30px;
}

.comment-item {
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
  text-align: left;
}

.comment-floor {
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}
</style>