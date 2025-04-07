<script setup lang="ts">
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage, ElUpload } from 'element-plus'
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { uploadImage } from '../../api/tool.ts'
import { Plus } from '@element-plus/icons-vue'
import type { UploadProps } from 'element-plus'

const router = useRouter()

// 表单数据
const form = ref({
  username: '', // 用户名
  password: '', // 密码
  name: '', // 真实姓名
  avatarUrl: '', // 头像URL
  telephone: '', // 手机号
  email: '', // 邮箱
  location: '', // 位置
})

// 预览图片URL
const previewUrl = ref('')

// 手机号验证规则
const telephoneRegex = /^1[3-9]\d{9}$/
// 邮箱验证规则
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

// 表单验证
function validateForm() {
  if (!form.value.username) {
    ElMessage.error('用户名不能为空')
    return false
  }
  if (!form.value.password) {
    ElMessage.error('密码不能为空')
    return false
  }
  if (!form.value.name) {
    ElMessage.error('真实姓名不能为空')
    return false
  }
  if (form.value.telephone && !telephoneRegex.test(form.value.telephone)) {
    ElMessage.error('手机号格式不正确')
    return false
  }
  if (form.value.email && !emailRegex.test(form.value.email)) {
    ElMessage.error('邮箱格式不正确')
    return false
  }
  return true
}

// 上传前处理
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

  // 创建预览URL
  previewUrl.value = URL.createObjectURL(file)
  return true
}

// 上传成功处理
const handleSuccess = (response: any) => {
  if (response.code === '000') {
    form.value.avatarUrl = response.result
    ElMessage.success('头像上传成功')
  }
}

// 自定义上传请求
const customRequest = async (options: any) => {
  const formData = new FormData()
  formData.append('file', options.file)

  try {
    const res = await uploadImage(formData)
    if (res.data.code === '000') {
      options.onSuccess(res.data)
    } else {
      options.onError(new Error(res.data.msg))
    }
  } catch (err) {
    options.onError(err)
  }
}

// 注册按钮触发
function handleRegister() {
  if (!validateForm()) return

  const registerData = {
    username: form.value.username,
    password: form.value.password,
    name: form.value.name,
    avatar: form.value.avatarUrl,
    telephone: form.value.telephone,
    email: form.value.email,
    location: form.value.location
  }

  // 这里替换为实际的注册API调用
  console.log('注册数据:', registerData)

  ElMessage.success('注册成功！')
  router.push('/login')
}
</script>

<template>
  <el-main class="main-frame">
    <el-card class="register-card">
      <div>
        <h1>用户注册</h1>
        <el-form :model="form" label-width="100px">
          <!-- 用户名 -->
          <el-form-item label="用户名" required>
            <el-input v-model="form.username" placeholder="请输入用户名" clearable />
          </el-form-item>

          <!-- 密码 -->
          <el-form-item label="密码" required>
            <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                show-password
                clearable
            />
          </el-form-item>

          <!-- 真实姓名 -->
          <el-form-item label="真实姓名" required>
            <el-input v-model="form.name" placeholder="请输入真实姓名" clearable />
          </el-form-item>

          <!-- 头像上传 -->
          <el-form-item label="头像">
            <el-upload
                class="avatar-uploader"
                action="#"
                :show-file-list="false"
                :before-upload="beforeUpload"
                :http-request="customRequest"
                :on-success="handleSuccess"

            >
              <div class="avatar-container">
                <img
                    v-if="previewUrl || form.avatarUrl"
                    :src="previewUrl || form.avatarUrl"
                    class="avatar-preview"
                    alt="头像预览"
                >
                <div v-else class="avatar-placeholder">
                  <el-icon class="upload-icon"><Plus /></el-icon>
                  <span class="upload-text">点击上传头像</span>
                </div>
                <div v-if="previewUrl || form.avatarUrl" class="avatar-mask">
                  <el-icon><Plus /></el-icon>
                  <span>更换头像</span>
                </div>
              </div>
            </el-upload>
            <div class="upload-tip">支持JPG/PNG格式，大小不超过2MB</div>
          </el-form-item>

          <!-- 手机号 -->
          <el-form-item label="手机号">
            <el-input v-model="form.telephone" placeholder="请输入手机号" clearable />
          </el-form-item>

          <!-- 邮箱 -->
          <el-form-item label="邮箱">
            <el-input v-model="form.email" placeholder="请输入邮箱" clearable />
          </el-form-item>

          <!-- 位置 -->
          <el-form-item label="位置">
            <el-input v-model="form.location" placeholder="请输入位置" clearable />
          </el-form-item>

          <!-- 注册按钮 -->
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
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-card {
  width: 600px;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.avatar-uploader {
  width: 150px;
  height: 150px;
  position: relative;
}

.avatar-container {
  width: 100%;
  height: 100%;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  position: relative;
}

.avatar-container:hover {
  border-color: var(--el-color-primary);
}

.avatar-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-secondary);
}

.upload-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
}

.avatar-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-container:hover .avatar-mask {
  opacity: 1;
}

.upload-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 8px;
}

.el-form-item:last-child {
  display: flex;
  justify-content: space-between;
}
</style>