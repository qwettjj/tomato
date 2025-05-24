<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { UserFilled } from '@element-plus/icons-vue'
import { ElMessage, ElLoading } from 'element-plus'
import { useRouter } from 'vue-router'
import { userInfo, userUpdate, type updateInfo } from '../../api/accounts'
import { uploadImage } from '../../api/tool'

const router = useRouter()

// 用户信息响应式变量
const userName = ref('')
const role = ref('' || "ADMIN" )
const phone = ref('')
const email = ref('')
const address = ref('')
const avatar = ref('')
const productId = ref('')

// 编辑状态
const isEditing = ref(false)
const editForm = ref({
  userName: '',
  phone: '',
  email: '',
  address: '',
  newPassword: '',
  confirmPassword: ''
})

// 头像上传相关
const avatarFile = ref<File | null>(null)
const avatarLoading = ref(false)

// 密码验证状态
const hasPasswordInput = ref(false)
const isPasswordIdentical = ref(false)
const changeDisabled = ref(true)

// 初始化用户信息
// 修改后的 fetchUserInfo 函数
const fetchUserInfo = async () => {
  const loading = ElLoading.service({ fullscreen: true })
  try {
    const response = await userInfo()
    console.log(response)

    // 更新响应式变量（确保使用.value）
    userName.value = response.data.userName
    role.value = response.data.role
    phone.value = response.data.phone
    email.value = response.data.email
    sessionStorage.setItem('userId', response.data.id)
    // 处理地址对象
    address.value = typeof response.data.address === 'string' ?
        response.data.address :
        JSON.stringify(response.data.address)
    avatar.value = response.data.avatar

    // 正确初始化编辑表单（使用.value）
    editForm.value = {
      userName: userName.value,
      phone: phone.value,
      email: email.value,
      address: address.value,
      newPassword: '',
      confirmPassword: ''
    }

    // 安全存储到sessionStorage
    sessionStorage.setItem('userName', userName.value)
    sessionStorage.setItem('role', role.value)
    sessionStorage.setItem('phone', phone.value)
    sessionStorage.setItem('email', email.value)
    sessionStorage.setItem('address', address.value)
    sessionStorage.setItem('avatar', avatar.value)

  } catch (error) {
    console.log(error)
    ElMessage.error('获取用户信息失败')
  } finally {
    loading.close()
  }
}

// 组件挂载时获取用户信息
onMounted(() => {
  fetchUserInfo()
})

// 密码验证逻辑
const checkPassword = () => {
  hasPasswordInput.value = editForm.value.newPassword !== '' && editForm.value.confirmPassword !== ''
  isPasswordIdentical.value = editForm.value.newPassword === editForm.value.confirmPassword
  changeDisabled.value = !(hasPasswordInput.value && isPasswordIdentical.value)
}

// 保存修改
const saveChanges = async () => {
  const loading = ElLoading.service({ fullscreen: true })
  try {
    // 构造更新数据
    const updateData: updateInfo = {
      userName: editForm.value.userName,
      phone: editForm.value.phone,
      email: editForm.value.email,
      address: editForm.value.address
    }

    // 处理密码修改
    if (editForm.value.newPassword && isPasswordIdentical.value) {
      updateData.password = editForm.value.newPassword
    }

    // 处理头像上传
    if (avatarFile.value) {
      const formData = new FormData()
      formData.append('image', avatarFile.value)
      const { data } = await uploadImage(formData)
      updateData.avatar = data.url
      avatar.value = data.url
      sessionStorage.setItem('avatar', data.url)
    }

    // 调用更新接口
    await userUpdate(updateData)

    // 更新本地数据
    userName.value = editForm.value.userName
    phone.value = editForm.value.phone
    email.value = editForm.value.email
    address.value = editForm.value.address

    // 如果有修改密码，则跳转到注册页面
    if (editForm.value.newPassword && isPasswordIdentical.value) {
      ElMessage.success('密码已修改，请重新登录')
      // 清空用户会话信息
      sessionStorage.clear()
      // 延迟跳转让用户看到提示信息
      setTimeout(() => {
        router.push('/login')
      }, 1500)
      return
    }

    ElMessage.success('个人信息更新成功')
    isEditing.value = false
  } catch (error) {
    ElMessage.error('更新失败，请检查输入')
  } finally {
    loading.close()
  }
}

// 取消编辑
const cancelEdit = () => {
  editForm.value = {
    userName: userName.value,
    phone: phone.value,
    email: email.value,
    address: address.value,
    newPassword: '',
    confirmPassword: ''
  }
  isEditing.value = false
}

// 头像上传处理
const handleAvatarUpload = async (file: File) => {
  avatarLoading.value = true
  try {
    avatarFile.value = file
    const reader = new FileReader()
    reader.onload = (e) => {
      avatar.value = e.target?.result as string
    }
    reader.readAsDataURL(file)
  } catch (error) {
    ElMessage.error('头像上传失败')
  } finally {
    avatarLoading.value = false
  }
}

// 导航功能
const goToCreateProduct = () => {
  router.push('/createproduct')
}

const goToModifyProduct = () => {
  if (!productId.value) {
    ElMessage.warning('请输入商品ID')
    return
  }
  router.push(`/modifyproduct/${productId.value}`)
}
</script>

<template>
  <div class="profile-container">
    <el-main class="main-content">
      <el-card class="info-card">
        <div class="avatar-area">
          <el-upload
              action=""
              :auto-upload="false"
              :show-file-list="false"
              :disabled="!isEditing"
              :on-change="(file) => handleAvatarUpload(file.raw)"
          >
            <el-avatar :size="80" :src="avatar" :icon="UserFilled">
              <template v-if="!avatar" #default>
                <span class="avatar-text">{{ userName.charAt(0) }}</span>
              </template>
            </el-avatar>
          </el-upload>
          <span class="welcome-text">欢迎您，{{ userName }}</span>
        </div>

        <el-divider />

        <el-descriptions :column="1" border title="个人信息">
          <el-descriptions-item label="用户名">{{ userName }}</el-descriptions-item>
          <el-descriptions-item label="身份">
            <el-tag :type="role === 'ADMIN' ? 'danger' : 'primary'">{{ role }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="手机号">{{ phone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ email }}</el-descriptions-item>
          <el-descriptions-item label="地址">{{ address }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="role === 'ADMIN'" class="admin-actions">
          <div class="product-id-input">
            <el-input
                v-model="productId"
                placeholder="输入商品ID"
                clearable
                @keyup.enter="goToModifyProduct"
            />
          </div>
          <el-button
              type="primary"
              @click="goToModifyProduct"
              class="admin-button"
          >
            修改商品信息
          </el-button>
          <el-button
              type="warning"
              @click="goToCreateProduct"
              class="admin-button"
          >
            创建商品
          </el-button>
        </div>
      </el-card>

      <el-card class="edit-card">
        <template #header>
          <div class="card-header">
            <span>个人信息管理</span>
            <el-button v-if="!isEditing" type="primary" @click="isEditing = true">
              修改信息
            </el-button>
            <div v-else class="edit-buttons">
              <el-button type="success" @click="saveChanges">保存</el-button>
              <el-button @click="cancelEdit">取消</el-button>
            </div>
          </div>
        </template>

        <el-form v-if="isEditing">
          <el-form-item label="用户名">
            <el-input v-model="editForm.userName" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="editForm.phone" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="editForm.email" />
          </el-form-item>
          <el-form-item label="地址">
            <el-input v-model="editForm.address" />
          </el-form-item>

          <el-divider />

          <div class="password-section">
            <h4>修改密码</h4>
            <el-form-item label="新密码">
              <el-input
                  type="password"
                  v-model="editForm.newPassword"
                  @input="checkPassword"
                  placeholder="请输入新密码"
                  show-password
              />
            </el-form-item>
            <el-form-item>
              <label v-if="!editForm.confirmPassword">确认密码</label>
              <label v-else-if="!isPasswordIdentical" class="error-warn">
                密码不一致
              </label>
              <label v-else>确认密码</label>
              <el-input
                  type="password"
                  v-model="editForm.confirmPassword"
                  @input="checkPassword"
                  :class="{ 'error-warn-input': editForm.confirmPassword && !isPasswordIdentical }"
                  placeholder="请再次输入密码"
                  show-password
              />
            </el-form-item>
          </div>
        </el-form>

        <div v-else class="view-mode">
          <p>点击"修改信息"按钮编辑您的个人信息和密码</p>
        </div>
      </el-card>
    </el-main>
  </div>
</template>

<style scoped>
/* 保持原有样式不变 */
.profile-container {
  padding: 10px;
  display: flex;
  flex-direction: row;
  gap: 5px;
  justify-content: center;
}

.main-content {
  display: flex;
  gap: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.info-card {
  flex: 1;
  min-width: 300px;
  max-width: 400px;
}

.edit-card {
  flex: 2;
  min-width: 500px;
  max-width: 700px;
}

.avatar-area {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px;
}

.welcome-text {
  font-size: 1.2rem;
  font-weight: bold;
}

.avatar-text {
  font-size: 2rem;
  font-weight: bold;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.password-section {
  margin-top: 20px;
}

.password-section h4 {
  margin-bottom: 20px;
}

.error-warn {
  color: red;
}

.error-warn-input {
  --el-input-focus-border-color: red;
}

.edit-buttons {
  display: flex;
  gap: 10px;
}

.view-mode {
  color: var(--el-text-color-secondary);
  text-align: center;
  padding: 20px 0;
}

.admin-actions {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.product-id-input {
  margin-bottom: 10px;
}

.admin-button {
  width: 100%;
  height: 40px;
  font-size: 14px;
  padding: 0 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 0 0 0;
}

@media (max-width: 992px) {
  .main-content {
    flex-direction: column;
  }

  .info-card, .edit-card {
    max-width: 100%;
    min-width: auto;
  }
}
</style>