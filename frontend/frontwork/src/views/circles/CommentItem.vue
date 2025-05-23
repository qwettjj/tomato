<template>
  <div class="comment" :style="{ marginLeft: depth * 30 + 'px' }">
    <div class="comment-header">
      <img :src="commentAuthor?.avatar || 'default-avatar.png'" class="avatar" />
      <span class="username">{{ commentAuthor?.userName }}</span>
      <span class="time">{{ new Date(comment.createTime).toLocaleString() }}</span>
      <button class="reply-button" @click="$emit('reply', comment.commentId)">回复</button>
    </div>
    <div class="comment-content">{{ comment.content }}</div>

    <!-- 递归渲染子评论 -->
    <CommentItem
        v-for="reply in replies"
        :key="reply.commentId"
        :comment="reply"
        :depth="depth + 1"
        @reply="$emit('reply', $event)"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, defineProps } from 'vue'
import { getUserInfo, type accountVO } from '../../api/accounts'
import type { CommentVO } from '../../api/comments'

const props = defineProps<{
  comment: CommentVO
  depth: number
}>()

const commentAuthor = ref<accountVO>()
const replies = ref<CommentVO[]>([])

// 获取评论作者信息
const fetchAuthor = async () => {
  const res = await getUserInfo(props.comment.accountId)
  commentAuthor.value = res.data
  console.log(commentAuthor.value)
}

// 获取回复（假设后端返回时已经包含嵌套结构）
// 如果后端返回的是平铺结构，需要自行处理成树形结构
// 这里假设直接通过children字段获取
onMounted(async () => {
  await fetchAuthor()
  // 这里需要根据实际API调整获取回复的方式
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
}

.reply-button {
  margin-left: auto;
  background: none;
  border: none;
  color: #409eff;
  cursor: pointer;
}
</style>