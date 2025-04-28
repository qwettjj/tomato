<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElForm, ElFormItem, ElButton, ElInput, ElMessage, ElLoading } from 'element-plus';
import { userLogin } from "../../api/accounts";

const router = useRouter();

// 使用与API一致的字段命名
const phone = ref('');
const password = ref('');

// 验证规则
const chinaMobileRegex = /^1(3[0-9]|4[579]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[189])\d{8}$/;
const isPhoneValid = computed(() => chinaMobileRegex.test(phone.value));
const isFormValid = computed(() => phone.value && isPhoneValid.value && password.value);

// 登录处理
const handleLogin = async () => {
  // const loading = ElLoading.service({
  //   lock: true,
  //   text: '登录中...',
  //   background: 'rgba(0, 0, 0, 0.7)'
  // });
  userLogin({
    phone: phone.value,
    password: password.value
  }).then(res => {
    console.log(res);
    if (res.code === '200') {
      console.log("hello");
      ElMessage({
        message: "登录成功！",
        type: 'success',
        center: true,
      })
      const token = res.data
      sessionStorage.setItem('token', token)
      sessionStorage.setItem('phone', phone.value)

      // userInfo(token).then(res => {
      //   console.log(res)
      //   // sessionStorage.setItem('name', res.data.result.name)
      //   // sessionStorage.setItem('role', res.data.result.role)
      //   // router.push({path: "/home/dashboard"})
      // })

      router.push({path: "/home/dashboard"})

    } else if (res.status === '400') {
      console.log("没进来");
       console.log(res);
        ElMessage({
        message: res.data.msg,
        type: 'error',
        center: true,
      })
      password.value = ''
    }
  })

}
</script>

<template>
  <el-main class="main-frame bgimage">
    <el-card class="login-card">
      <div>
        <h1>登入您的账户</h1>
        <el-form @submit.prevent="handleLogin">
          <el-form-item>
            <label v-if="!phone" for="phone">注册手机号</label>
            <label v-else-if="!isPhoneValid" class="error-warn">手机号不合法</label>
            <label v-else for="phone">注册手机号</label>
            <el-input
                id="phone"
                v-model="phone"
                :class="{ 'error-warn-input': phone && !isPhoneValid }"
                placeholder="请输入手机号"
                maxlength="11"
            />
          </el-form-item>

          <el-form-item>
            <label for="password">账户密码</label>
            <el-input
                id="password"
                v-model="password"
                type="password"
                placeholder="••••••••"
                show-password
            />
          </el-form-item>

          <div class="button-group">
            <el-button
                type="primary"
                native-type="submit"
                :disabled="!isFormValid"
            >
              登入
            </el-button>
            <router-link to="/register">
              <el-button>去注册</el-button>
            </router-link>
          </div>
        </el-form>
      </div>
    </el-card>
  </el-main>
</template>

<style scoped>
.main-frame {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.login-card {
  width: 400px;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.login-card h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #2c3e50;
}

.error-warn {
  color: #ff4d4f;
  transition: color 0.3s;
}

.error-warn-input {
  --el-input-focus-border-color: #ff4d4f;
  --el-input-hover-border-color: #ff7875;
}

.button-group {
  margin-top: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-button {
  width: 48%;
  transition: all 0.3s;
}

.el-button--primary {
  background: #409eff;
  border-color: #409eff;
}

.el-button--primary:hover {
  background: #66b1ff;
  border-color: #66b1ff;
}

@media (max-width: 768px) {
  .login-card {
    width: 90%;
    margin: 0 auto;
  }

  .button-group {
    flex-direction: column;
    gap: 12px;
  }

  .el-button {
    width: 100%;
  }
}
</style>