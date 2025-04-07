<script setup lang="ts">
import { ElForm, ElFormItem, ElButton, ElInput, ElMessage } from 'element-plus';
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

// 输入框值（需要在前端拦截不合法输入：是否为空+额外规则）
const tel = ref('');
const password = ref('');

// 电话号码是否为空
const hasTelInput = computed(() => tel.value != '');
// 密码是否为空
const hasPasswordInput = computed(() => password.value != '');
// 电话号码的规则
const chinaMobileRegex = /^1(3[0-9]|4[579]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[189])\d{8}$/;
const telLegal = computed(() => chinaMobileRegex.test(tel.value));
// 密码不设置特殊规则
// 登录按钮可用性
const loginDisabled = computed(() => {
  return !(hasTelInput.value && telLegal.value && hasPasswordInput.value);
});

const router = useRouter();

// 登录按钮触发
function handleLogin() {
  // 模拟登录逻辑
  if (tel.value === '13333333333' && password.value === '123456') {
    ElMessage({
      message: '登录成功！',
      type: 'success',
      center: true,
    });
    sessionStorage.setItem('token', 'mock-token'); // 模拟存储 token
    // router.push({ path: '/home/dashboard' }); // 跳转到仪表盘页面
    // 打印当前路由信息

    router.push({ name: 'Dashboard' })  // 通过名称跳转，避免路径问题
    // 打印当前路由信息

  } else {
    ElMessage({
      message: '手机号或密码错误',
      type: 'error',
      center: true,
    });
    password.value = ''; // 清空密码输入框
  }
}
</script>

<template>
  <el-main class="main-frame bgimage">
    <el-card class="login-card">
      <div>
        <h1>登入您的账户</h1>
        <el-form>
          <el-form-item>
            <label v-if="!hasTelInput" for="tel">注册手机号</label>
            <label v-else-if="!telLegal" for="tel" class="error-warn">手机号不合法</label>
            <label v-else for="tel">注册手机号</label>
            <el-input
                id="tel"
                type="text"
                v-model="tel"
                required
                :class="{ 'error-warn-input': hasTelInput && !telLegal }"
                placeholder="请输入手机号"
            />
          </el-form-item>

          <el-form-item>
            <label for="password">账户密码</label>
            <el-input
                id="password"
                type="password"
                v-model="password"
                required
                placeholder="••••••••"
            />
          </el-form-item>

          <span class="button-group">
            <el-button @click.prevent="handleLogin" :disabled="loginDisabled" type="primary">登入</el-button>
            <router-link to="/register" v-slot="{ navigate }">
              <el-button @click="navigate">去注册</el-button>
            </router-link>
          </span>
        </el-form>
      </div>
    </el-card>
  </el-main>
</template>

<style scoped>
/* 主容器样式 */
.main-frame {
  width: 100%; /* 宽度占满父容器 */
  height: 100%; /* 高度占满父容器 */
  display: flex; /* 使用 Flex 布局 */
  align-items:center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
}

/* 背景图片样式 */


/* 登录卡片样式 */
.login-card {
  width: 3000px; /* 卡片宽度为父容器的 80% */
  max-width: 1000px; /* 最大宽度为 */
  padding: 30px; /* 内边距为 30px */
  border-radius: 8px; /* 圆角为 8px */
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); /* 添加阴影效果 */
}

/* 错误提示文字样式 */
.error-warn {
  color: red; /* 文字颜色为红色 */
}

/* 输入框错误提示样式 */
.error-warn-input {
  --el-input-focus-border-color: red; /* 输入框聚焦时边框颜色为红色 */
}

/* 按钮组样式 */
.button-group {
  padding-top: 20px; /* 上边距为 20px */
  display: flex; /* 使用 Flex 布局 */
  flex-direction: row; /* 子元素横向排列 */
  gap: 30px; /* 子元素之间的间距为 30px */
  align-items: center; /* 垂直居中 */
  justify-content: right; /* 子元素靠右对齐 */
}
</style>