<template>
  <div class="comment" :style="{ marginLeft: depth * 30 + 'px' }">
    <div class="comment-header">
      <img :src="commentAuthor?.avatar || 'default-avatar.png'" class="avatar" />
      <span class="username">{{ commentAuthor?.userName }}</span>
      <span v-if="parentCommentAuthor" class="reply-to">
        回复 @{{ parentCommentAuthor.userName }}
      </span>
      <span class="time">{{ formatTime(comment.createTime) }}</span>
      <!-- 按钮容器 -->
      <div class="comment-actions">
        <button
            v-if="isCurrentUserComment"
            class="delete-button"
            @click="handleDelete"
        >
          删除
        </button>
        <button class="reply-button" @click="handleReply">回复</button>
      </div>
    </div>
    <div class="comment-content">{{ comment.content }}</div>

    <CommentItem
        v-for="reply in replies"
        :key="reply.commentId"
        :comment="reply"
        :depth="depth + 1"
        :currentUserId="currentUserId"
        @reply="handleChildReply"
        @delete="handleChildDelete"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, defineProps, defineEmits, computed } from 'vue'
import { getUserInfo, type accountVO } from '../../api/accounts'
import { getComment, deleteComment } from '../../api/comments'
import type { CommentVO } from '../../api/comments'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps<{
  comment: CommentVO
  depth: number
  currentUserId?: number | string // 修改为支持string类型
}>()

const emit = defineEmits(['reply', 'delete'])

const commentAuthor = ref<accountVO>()
const parentCommentAuthor = ref<accountVO>()
const replies = ref<CommentVO[]>([])

// 判断是否是当前用户的评论
const isCurrentUserComment = computed(() => {
  return props.currentUserId && props.currentUserId.toString() === props.comment.accountId?.toString()
})

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

// 处理删除点击
const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteComment(props.comment.commentId)
    emit('delete', props.comment.commentId)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 处理子组件的回复事件
const handleChildReply = (payload: { commentId: number, userName: string }) => {
  emit('reply', payload)
}

// 处理子组件的删除事件
const handleChildDelete = (commentId: number) => {
  emit('delete', commentId)
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
  justify-content: space-between;
}

.comment-actions {
  display: flex;
  gap: 10px;
  margin-left: auto;
}

.reply-to {
  color: #666;
  font-size: 0.9em;
  margin-left: 5px;
}

.reply-button, .delete-button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 2px 8px;
  font-size: 0.9em;
}

.reply-button {
  color: #409eff;
  border: 1px solid #409eff;
  border-radius: 3px;
}

.delete-button {
  color: #f56c6c;
  border: 1px solid #f56c6c;
  border-radius: 3px;
}

.reply-button:hover {
  background-color: #ecf5ff;
}

.delete-button:hover {
  background-color: #fef0f0;
}

.username {
  font-weight: bold;
}

.time {
  color: #999;
  font-size: 0.8em;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}
</style>
