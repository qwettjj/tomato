<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { UploadProps } from 'element-plus'
import { userRegister } from '../../api/accounts'
import { uploadImage } from '../../api/tool'

const router = useRouter()

// 使用与API一致的字段命名
const registerForm = ref({
  userName: '',
  password: '',
  phone: '',
  email: '',
  address: '',
  avatar: '',
  role: 'USER' // 默认用户角色
})

// 预览图片URL
const previewUrl = ref('')
const avatarFile = ref<File | null>(null)

// 验证规则
const phoneRegex = /^1[3-9]\d{9}$/
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

// 表单验证
const validateForm = () => {
  if (!registerForm.value.userName) {
    ElMessage.error('用户名不能为空')
    return false
  }
  if (!registerForm.value.password) {
    ElMessage.error('密码不能为空')
    return false
  }
  if (!registerForm.value.phone) {
    ElMessage.error('手机号不能为空')
    return false
  }
  if (!phoneRegex.test(registerForm.value.phone)) {
    ElMessage.error('手机号格式不正确')
    return false
  }
  if (!registerForm.value.email) {
    ElMessage.error('邮箱不能为空')
    return false
  }
  if (!emailRegex.test(registerForm.value.email)) {
    ElMessage.error('邮箱格式不正确')
    return false
  }
  return true
}

// 头像上传处理
const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB!')
    return false
  }

  previewUrl.value = URL.createObjectURL(file)
  avatarFile.value = file
  return true
}

// 提交注册
const handleRegister = async () => {
  if (!validateForm()) return

  const loading = ElLoading.service({
    lock: true,
    text: '注册中...',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  try {
    // 上传头像
    if (avatarFile.value) {
      const formData = new FormData()
      formData.append('image', avatarFile.value)
      const { data } = await uploadImage(formData)
      registerForm.value.avatar = data.url
    }

    // 调用注册接口
    await userRegister(registerForm.value)

    ElMessage.success('注册成功')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '注册失败')
  } finally {
    loading.close()
  }
}
</script>

<template>
  <el-main class="main-frame">
    <el-card class="register-card">
      <div>
        <h1>用户注册</h1>
        <el-form label-width="100px">
          <!-- 用户名 -->
          <el-form-item label="用户名" required>
            <el-input
                v-model="registerForm.userName"
                placeholder="请输入用户名"
                clearable
            />
          </el-form-item>

          <!-- 密码 -->
          <el-form-item label="密码" required>
            <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                show-password
                clearable
            />
          </el-form-item>

          <!-- 手机号 -->
          <el-form-item label="手机号" required>
            <el-input
                v-model="registerForm.phone"
                placeholder="请输入手机号"
                maxlength="11"
                clearable
            />
          </el-form-item>

          <!-- 邮箱 -->
          <el-form-item label="邮箱" required>
            <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱"
                clearable
            />
          </el-form-item>

          <!-- 地址 -->
          <el-form-item label="地址" required>
            <el-input
                v-model="registerForm.address"
                placeholder="请输入地址"
                clearable
            />
          </el-form-item>

          <!-- 头像上传 -->
          <el-form-item label="头像">
            <el-upload
                class="avatar-uploader"
                action="#"
                :show-file-list="false"
                :before-upload="beforeUpload"
            >
              <div class="avatar-container">
                <img
                    v-if="previewUrl || registerForm.avatar"
                    :src="previewUrl || registerForm.avatar"
                    class="avatar-preview"
                >
                <div v-else class="avatar-placeholder">
                  <el-icon class="upload-icon"><Plus /></el-icon>
                  <span class="upload-text">点击上传头像</span>
                </div>
                <div v-if="previewUrl" class="avatar-mask">
                  <el-icon><Plus /></el-icon>
                  <span>更换头像</span>
                </div>
              </div>
            </el-upload>
            <div class="upload-tip">支持JPG/PNG格式，大小不超过2MB</div>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleRegister">注册</el-button>
            <router-link to="/login">
              <el-button>返回登录</el-button>
            </router-link>
          </el-form-item>
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

.register-card {
  width: 600px;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #2c3e50;
}

.avatar-uploader {
  width: 150px;
  height: 150px;
  position: relative;
}

/* 保持原有样式不变 */
</style>