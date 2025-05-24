<template>
  <div class="comment" :style="{ marginLeft: depth * 30 + 'px' }">
    <div class="comment-header">
      <img :src="commentAuthor?.avatar || 'default-avatar.png'" class="avatar" />
      <span class="username">{{ commentAuthor?.userName }}</span>
      <span v-if="parentCommentAuthor" class="reply-to">
        回复 @{{ parentCommentAuthor.userName }}
      </span>
      <span class="time">{{ formatTime(comment.createTime) }}</span>
      <button class="reply-button" @click="handleReply">回复</button>
    </div>
    <div class="comment-content">{{ comment.content }}</div>

    <!-- 递归渲染子评论 -->
    <CommentItem
        v-for="reply in replies"
        :key="reply.commentId"
        :comment="reply"
        :depth="depth + 1"
        @reply="handleChildReply"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, defineProps, defineEmits } from 'vue'
import { getUserInfo, type accountVO } from '../../api/accounts'
import { getComment } from '../../api/comments'
import type { CommentVO } from '../../api/comments'

const props = defineProps<{
  comment: CommentVO
  depth: number
}>()

const emit = defineEmits(['reply'])

const commentAuthor = ref<accountVO>()
const parentCommentAuthor = ref<accountVO>()
const replies = ref<CommentVO[]>([])

// 时间格式化方法
const formatTime = (time: string | Date) => {
  return new Date(time).toLocaleString()
}

// 获取评论作者信息
const fetchAuthor = async () => {
  const res = await getUserInfo(props.comment.accountId)
  commentAuthor.value = res.data
}

// 获取父评论作者信息（如果是回复的话）
const fetchParentAuthor = async () => {
  if (props.comment.parentId) {
    try {
      const parentComment = await getComment(props.comment.parentId)
      const res = await getUserInfo(parentComment.accountId)
      parentCommentAuthor.value = res.data
    } catch (error) {
      console.error('获取父评论失败:', error)
    }
  }
}

// 处理回复点击
const handleReply = () => {
  if (!commentAuthor.value) return

  emit('reply', {
    commentId: props.comment.commentId,
    userName: commentAuthor.value.userName
  })
}

// 处理子组件的回复事件
const handleChildReply = (payload: { commentId: number, userName: string }) => {
  emit('reply', payload)
}

onMounted(async () => {
  await fetchAuthor()
  await fetchParentAuthor()
})
</script>

<style scoped>
.comment {
  margin: 15px 0;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 4px;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.reply-to {
  color: #666;
  font-size: 0.9em;
  margin-left: 5px;
}

.reply-button {
  margin-left: auto;
  background: none;
  border: none;
  color: #409eff;
  cursor: pointer;
}
</style>