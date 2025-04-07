<template>
  <div class="book-home">
    <h1>全部图书</h1>
    <el-row :gutter="20">
      <el-col
          v-for="(book, index) in books"
          :key="index"
          :span="6"
          class="book-col"
      >
        <router-link :to="`/productdetail/${book.id}`" class="book-link">
          <el-card class="book-card">
            <img :src="book.cover" :alt="book.title" class="book-cover" />
            <div class="book-info">
              <h3 class="book-title">{{ book.title }}</h3>
              <div class="book-author">{{ book.author }}</div>
              <div class="book-meta">
                <span class="book-price">¥{{ book.price }}</span>
                <span class="book-rate">评分: {{ book.rate }}/10</span>
              </div>
            </div>
          </el-card>
        </router-link>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// 图书虚拟数据
const books = ref([
  {
    id: 1,
    title: 'JavaScript高级程序设计',
    author: '尼古拉斯·泽卡斯',
    price: 99.80,
    rate: 9.5,
    description: '前端开发必读经典',
    cover: 'https://via.placeholder.com/280x360?text=JavaScript高级程序设计',
    detail: '本书全面介绍了JavaScript语言的核心概念，以及浏览器支持的API'
  },
  {
    id: 2,
    title: 'Vue.js设计与实现',
    author: '霍春阳',
    price: 89.00,
    rate: 9.2,
    description: '深入解析Vue.js框架原理',
    cover: 'https://via.placeholder.com/280x360?text=Vue.js设计与实现',
    detail: '从源码角度分析Vue.js的设计思路和实现原理'
  },
  {
    id: 3,
    title: '活着',
    author: '余华',
    price: 39.90,
    rate: 9.7,
    description: '中国当代文学经典',
    cover: 'https://via.placeholder.com/280x360?text=活着',
    detail: '讲述一个人在中国社会变迁中的苦难人生'
  },
  {
    id: 4,
    title: '三体',
    author: '刘慈欣',
    price: 59.80,
    rate: 9.8,
    description: '科幻文学里程碑作品',
    cover: 'https://via.placeholder.com/280x360?text=三体',
    detail: '中国科幻文学的巅峰之作，雨果奖获奖作品'
  },
  {
    id: 5,
    title: 'Python编程：从入门到实践',
    author: 'Eric Matthes',
    price: 89.00,
    rate: 9.1,
    description: 'Python学习经典教材',
    cover: 'https://via.placeholder.com/280x360?text=Python编程',
    detail: '通过实际项目学习Python编程的实用指南'
  },
  {
    id: 6,
    title: '百年孤独',
    author: '加西亚·马尔克斯',
    price: 49.50,
    rate: 9.6,
    description: '魔幻现实主义文学代表作',
    cover: 'https://via.placeholder.com/280x360?text=百年孤独',
    detail: '布恩迪亚家族七代人的传奇故事'
  },
  {
    id: 7,
    title: 'React设计原理',
    author: '卡颂',
    price: 79.00,
    rate: 9.0,
    description: '深入理解React框架',
    cover: 'https://via.placeholder.com/280x360?text=React设计原理',
    detail: '从源码层面解析React的设计理念和实现细节'
  },
  {
    id: 8,
    title: '人类简史',
    author: '尤瓦尔·赫拉利',
    price: 68.00,
    rate: 9.3,
    description: '全球畅销历史著作',
    cover: 'https://via.placeholder.com/280x360?text=人类简史',
    detail: '从认知革命到科学革命的人类发展史'
  }
]);
</script>

<style scoped>
.book-home {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  margin-bottom: 20px;
  font-size: 24px;
  color: #303133;
}

.book-col {
  margin-bottom: 20px;
}

.book-link {
  text-decoration: none;
}

.book-card {
  width: 280px;
  height: 380px; /* 增加高度以适应图书封面 */
  text-align: center;
  transition: transform 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.book-cover {
  width: 180px;
  height: 240px;
  margin: 10px auto;
  object-fit: cover;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.book-info {
  padding: 15px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.book-title {
  margin: 0;
  font-size: 16px;
  color: #303133;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.book-author {
  font-size: 14px;
  color: #606266;
  margin: 8px 0;
}

.book-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.book-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.book-rate {
  font-size: 14px;
  color: #606266;
}
</style>