<script setup lang="ts">
import {parseRole} from "../utils"
import {User, SwitchButton, ShoppingCart} from "@element-plus/icons-vue"
import { ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const role = sessionStorage.getItem('role')
const router = useRouter()

// 退出登录
function logout() {
  ElMessageBox.confirm(
      '是否要退出登录？',
      '提示',
      {
        customClass: "customDialog",
        confirmButtonText: '是',
        cancelButtonText: '否',
        type: "warning",
        showClose: false,
        roundButton: true,
        center: true
      }
  ).then(() => {
    sessionStorage.setItem('token', '')
    router.push({path: "/login"})
  })
}

// 跳转到书友圈
function toCircles() {
  router.push({path:"/home/circles"})
}

function toHistory() {
  router.push({path: "/history"})
}
</script>

<template>
  <el-header class="custom-header" height="20">
    <el-row :gutter="10">

      <el-col :span="3" class="header-icon">
        <router-link to="/dashboard" v-slot="{navigate}" class="no-link">
          <h1 @click="navigate" class="header-text"> 吃饱饱在线购物</h1>
        </router-link>
      </el-col>

      <el-col :span="2">
        <el-tag class="role-tag" size="large">{{ parseRole(role) }}版</el-tag>
      </el-col>

      <el-col :span="8">
      </el-col>

      <!-- 购物车链接 -->
      <el-col :span="2" class="header-icon">
        <router-link to="/home/cart" v-slot="{ navigate }" class="header-link">
          <span @click="navigate" class="header-text">购物车</span>
        </router-link>
      </el-col>

      <!-- 书友圈按钮 -->
      <el-col :span="2" class="header-icon">
        <a @click="toCircles" class="header-link">
          <span class="header-text">书友圈</span>
        </a>
      </el-col>

      <el-col :span="2" class="header-icon">
        <a @click="toHistory" class="header-link">
          <span class="header-text">购买记录</span>
        </a>
      </el-col>

      <el-col :span="2" class="header-icon">
        <router-link to="/allproduct" v-slot="{ navigate }" class="header-link">
          <span @click="navigate" class="header-text">商品主页</span>
        </router-link>
      </el-col>

      <el-col :span="1" class="header-icon">
        <router-link to="/home/dashboard" v-slot="{navigate}">
          <el-icon @click="navigate" :size="35" color="white" ><User /></el-icon>
        </router-link>
      </el-col>

      <el-col :span="1" class="header-icon">
        <a @click="logout">
          <el-icon :size="35" color="white" ><SwitchButton /></el-icon>
        </a>
      </el-col>
    </el-row>
  </el-header>
</template>

<style scoped>
.custom-header {
  background-color: #FF6347;
  border-bottom-left-radius: 20px;
  border-bottom-right-radius: 20px;
  display: flex;
  flex-direction: column;
}

.no-link {
  text-decoration: none;
}

.role-tag {
  margin-top: 20px;
  font-size: 20px;
}

.header-text {
  color:white;
  font-size: x-large;
  min-width: max-content;
  margin-top: 15px;
  margin-bottom: 15px;
}

.header-icon {
  display: flex;
  flex-direction: column;
  align-items:center;
  justify-content: center;
}

.header-link {
  text-decoration: none !important;
  cursor: pointer;
}
</style>