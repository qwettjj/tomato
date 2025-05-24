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
          👁️ {{ post.viewCount }} 赞 {{ post.likeCount }}  💬 {{ post.commentCount }}
        </span>
      </div>
      <div class="post-content">{{ post.content }}</div>
      <button
          class="like-button"
          :class="{ 'liked': isLiked }"
          @click="handleLike"
      >
        {{ isLiked.value ? '已赞' : '点赞' }}
      </button>
    </div>

    <!-- 评论输入框 -->
    <div class="comment-input">
      <textarea
          v-model="newComment"
          placeholder="写下你的评论..."
          @keydown.enter.exact.prevent="submitComment"
      ></textarea>
      <button @click="submitComment">发布</button>
    </div>

    <!-- 评论列表 -->
    <div class="comments">
      <CommentItem
          v-for="comment in comments"
          :key="comment.commentId"
          :comment="comment"
          :depth="0"
          @reply="handleReply"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
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

// 评论相关状态
const comments = ref<CommentVO[]>([])
const newComment = ref('')
const replyingTo = ref<number | null>(null)

// 获取帖子详情
const fetchPostDetail = async () => {
  const res = await getPostDetail(postId)
  post.value = res.data
  // 获取作者信息
  authorInfo.value = await getUserInfo(res.data.accountId)
  // 检查是否已点赞
  const res2 = await judgeLiked(postId)
  isLiked.value = res2.data
}

// 获取评论列表
const fetchComments = async () => {
  const res = await getPostComments(postId)
  comments.value = res
  console.log(res)
}

// 处理点赞
const handleLike = async () => {
  console.log("isLiked.value : " + isLiked.value)
  if (isLiked.value) {
    await unlikePost(postId)
    post.value!.likeCount--
  } else {
    await likePost(postId)
    post.value!.likeCount++
  }
  isLiked.value = !isLiked.value
}

// 提交评论
const submitComment = async () => {
  if (!newComment.value.trim()) return

  const commentInfo = {
    postId: postId,
    content: newComment.value,
    parentId: replyingTo.value
  }
  console.log(commentInfo)
  await createComment(commentInfo)
  newComment.value = ''
  replyingTo.value = null
  await fetchComments()
}

// 处理回复
const handleReply = (commentId: number) => {
  replyingTo.value = commentId
  // 这里可以添加自动聚焦到输入框的逻辑
}

// 时间格式化
const formatTime = (timestamp: number) => {
  return new Date(timestamp).toLocaleString()
}

onMounted(async () => {
  await fetchPostDetail()
  await fetchComments()
})
</script>

<style scoped>
.post-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
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
}

.like-button {
  padding: 8px 16px;
  background: #f0f0f0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.like-button.liked {
  background: #409eff;
  color: white;
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

.comment-input button {
  padding: 8px 16px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>