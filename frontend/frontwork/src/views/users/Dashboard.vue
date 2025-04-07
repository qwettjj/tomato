<script setup lang="ts">
import { ref } from 'vue'
import { UserFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

const name = ref(sessionStorage.getItem('name') || '')
const role = ref(sessionStorage.getItem('role') || '管理员')
const telephone = ref(sessionStorage.getItem('telephone') || '')
const email = ref(sessionStorage.getItem('email') || '')
const location = ref(sessionStorage.getItem('location') || '')
const avatarUrl = ref(sessionStorage.getItem('avatarUrl') || '')
const productId = ref('')

const isEditing = ref(false)
const editForm = ref({
  name: name.value,
  telephone: telephone.value,
  email: email.value,
  location: location.value,
  newPassword: '',
  confirmPassword: ''
})

const hasPasswordInput = ref(false)
const isPasswordIdentical = ref(false)
const changeDisabled = ref(true)

const checkPassword = () => {
  hasPasswordInput.value = editForm.value.newPassword !== '' && editForm.value.confirmPassword !== ''
  isPasswordIdentical.value = editForm.value.newPassword === editForm.value.confirmPassword
  changeDisabled.value = !(hasPasswordInput.value && isPasswordIdentical.value)
}

const saveChanges = () => {
  name.value = editForm.value.name
  telephone.value = editForm.value.telephone
  email.value = editForm.value.email
  location.value = editForm.value.location

  sessionStorage.setItem('name', name.value)
  sessionStorage.setItem('telephone', telephone.value)
  sessionStorage.setItem('email', email.value)
  sessionStorage.setItem('location', location.value)

  if (editForm.value.newPassword && editForm.value.newPassword === editForm.value.confirmPassword) {
    ElMessage.success('个人信息和密码更新成功（模拟）')
    editForm.value.newPassword = ''
    editForm.value.confirmPassword = ''
  } else {
    ElMessage.success('个人信息更新成功')
  }

  isEditing.value = false
}

const cancelEdit = () => {
  editForm.value = {
    name: name.value,
    telephone: telephone.value,
    email: email.value,
    location: location.value,
    newPassword: '',
    confirmPassword: ''
  }
  isEditing.value = false
}

const goToCreateProduct = () => {
  router.push('/createproduct')
}

const goToModifyProduct = () => {
  if (!productId.value) {
    ElMessage.warning('请输入商品ID')
    return
  }
  router.push(`/removeproduct/${productId.value}`)
}
</script>

<template>
  <div class="profile-container">
    <el-main class="main-content">
      <el-card class="info-card">
        <div class="avatar-area">
          <el-avatar :size="80" :src="avatarUrl" :icon="UserFilled">
            <template v-if="!avatarUrl" #default>
              <span class="avatar-text">{{ name.charAt(0) }}</span>
            </template>
          </el-avatar>
          <span class="welcome-text">欢迎您，{{ name }}</span>
        </div>

        <el-divider />

        <el-descriptions :column="1" border title="个人信息">
          <el-descriptions-item label="用户名">{{ name }}</el-descriptions-item>
          <el-descriptions-item label="身份">
            <el-tag :type="role === '管理员' ? 'danger' : 'primary'">{{ role }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="手机号">{{ telephone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ email }}</el-descriptions-item>
          <el-descriptions-item label="位置">{{ location }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="role === '管理员'" class="admin-actions">
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
            <el-input v-model="editForm.name" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="editForm.telephone" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="editForm.email" />
          </el-form-item>
          <el-form-item label="位置">
            <el-input v-model="editForm.location" />
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