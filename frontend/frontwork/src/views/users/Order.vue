<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Location, CreditCard, Picture } from '@element-plus/icons-vue'
import { createOrderDirectly, getOrder, deleteOrder, deleteOrderDirectly,updateOrderStatus } from '../../api/order'
import { createAlipay } from '../../api/payments'
import { getProduct } from "../../api/products";
import { getCartItem,removeCartItemById } from "../../api/cartItem";

const route = useRoute()
const router = useRouter()

interface OrderItem {
  orderId: number
  orderNo: string
  userId: number
  totalAmount: number
  paymentMethod: string
  status: string
  createTime: string
  products: {
    id: number
    productName: string
    price: number
    quantity: number
    cover: string
    specifications: {
      key: string
      value: string
    }[]
    name?: string
    image?: string
    cartItemId?: number
  }[]
  address: string
}

const currentOrder = ref<OrderItem>({
  orderId: 0,
  orderNo: '',
  userId: 0,
  totalAmount: 0,
  paymentMethod: '',
  status: 'PENDING',
  createTime: '',
  products: [],
  address: ''
})

const paymentMethods = [
  { id: 'alipay', name: '支付宝', icon: 'https://dummyimage.com/40x40/1677ff/fff' },
  { id: 'wechat', name: '微信支付', icon: 'https://dummyimage.com/40x40/07c160/fff' }
]

const selectedMethod = ref('alipay')
const isPaying = ref(false)
const productId = Number(route.query.productId)
const quantity = Number(route.query.quantity)
const isDirect = route.query.isDirect === 'true'
let cartItemQuantity

let pollIntervalId: number | null = null
const POLL_INTERVAL = 5000  // 5秒轮询一次

const fetchProductInfo = async (id: number) => {
  try {
    const response = await getProduct(id)
    const product = response.data
    return [{
      id: product.id,
      productName: product.productName,
      price: product.price,
      quantity: quantity || cartItemQuantity,
      cover: product.cover,
      specifications: product.specifications || [],
      name: product.productName,
      image: product.cover
    }]
  } catch (error) {
    ElMessage.error('商品信息加载失败')
    return []
  }
}

const initOrder = async () => {
  try {
    const orderId = Number(route.query.orderId)
    if (orderId) {
      const res = await getOrder({ id: orderId })
      currentOrder.value = res.data
      currentOrder.value.address = sessionStorage.getItem('address') || '请填写收货地址'

      if (isDirect) {
        const products = await fetchProductInfo(productId)
        currentOrder.value.products = products.map(p => ({
          ...p,
          name: p.productName,
          image: p.cover
        }))
      } else {
        let products = []
        for(const id of route.query.cartItemIds) {
          const cartItem = await getCartItem(id);
          if (cartItem) {
            cartItemQuantity = cartItem.data.quantity
            const productInfo = await fetchProductInfo(cartItem.data.productId)
            products.push(...productInfo)
          }
        }
        currentOrder.value.products = products.map(p => ({
          ...p,
          name: p.productName,
          image: p.cover
        }))
      }
    } else if (isDirect) {
      const products = await fetchProductInfo(productId)
      if (products.length === 0) {
        throw new Error('无法获取商品信息')
      }
      const product = products[0]
      const createRes = await createOrderDirectly({
        amount: product.price * quantity,
        paymentMethod: selectedMethod.value,
        productId,
        quantity,
        specifications: product.specifications
      })

      const orderRes = await getOrder({ id: createRes.data })
      currentOrder.value = {
        ...orderRes.data,
        products: products.map(p => ({
          ...p,
          name: p.productName,
          image: p.cover
        }))
      }
    } else {
      throw new Error('无法获得商品信息')
    }

  } catch (error) {
    ElMessage.error(`订单初始化失败: ${(error as Error).message}`)
    await router.push('/home')
  }
}

const handlePayment = async () => {
  try {
    isPaying.value = true
    const paymentFormHtml = await createAlipay(currentOrder.value.orderId)

    const paymentWindow = window.open('', '_blank')
    if (!paymentWindow) {
      ElMessage.warning('请允许弹出窗口以完成支付')
      return
    }
    paymentWindow.document.write(paymentFormHtml)
    paymentWindow.document.close()

    pollIntervalId = window.setInterval(async () => {
      try {
        const res = await getOrder({ id: currentOrder.value.orderId })
        const latestStatus = res.data.status
        if (latestStatus === 'SUCCESS') {
          clearInterval(pollIntervalId!)
          ElMessage.success('支付成功！')
          if(!isDirect) {
            for (const id of route.query.cartItemIds) {
              const res = await removeCartItemById(id)
              if(res.code !== "200") {
                throw Error('未找到订单')
              }
            }
          }
          router.push('/home')
        } else if (latestStatus === 'TIMEOUT') {
          clearInterval(pollIntervalId!)
          ElMessage.warning('支付超时，请检查支付结果')
          router.push('/home')
        } else if(latestStatus === 'FAILURE') {
          clearInterval(pollIntervalId!)
          ElMessage.warning('支付失败')
          router.push('/home')
        }
      } catch (error) {
        ElMessage.error('状态查询失败')
        clearInterval(pollIntervalId!)
      }
    }, POLL_INTERVAL)

  } catch (error) {
    deleteCurrentOrder()
    ElMessage.error((error as Error).message || '支付发起失败')
  } finally {
    isPaying.value = false
  }
}

const deleteCurrentOrder = async () => {
  try {
    if (isDirect) {
      await deleteOrderDirectly({
        orderId: currentOrder.value.orderId,
        productId,
        quantity
      })
    } else {
      const cartItemIds = route.query.cartItemIds
      if (cartItemIds.length === 0) {
        throw new Error('未找到关联的购物车项')
      }
      await deleteOrder({
        orderId: currentOrder.value.orderId,
        cartItemId: cartItemIds
      })
    }
    ElMessage.success('订单已取消')
  } catch (error) {
    ElMessage.error(`取消订单失败: ${(error as Error).message}`)
    throw error
  }
}

const cancelOrder = async () => {
  try {
    await ElMessageBox.confirm('确定要取消当前订单吗？', '取消订单', {
      confirmButtonText: '确定',
      cancelButtonText: '继续支付',
      type: 'warning'
    })

    await deleteCurrentOrder()
    await router.replace({
      path: '/allproduct',
      query: { from: 'payment', canceled: 'true' }
    })
  } catch {
    // 用户取消操作
  }
}

onMounted(initOrder)

onBeforeUnmount(() => {
  if (pollIntervalId) {
    clearInterval(pollIntervalId)
  }
})
</script>


<template>
  <!-- 以下模板部分保持不变 -->
  <div class="payment-container">
    <!-- 头部 -->
    <el-header class="payment-header" height="60">
      <h1 class="header-text">订单支付</h1>
      <el-button
          type="text"
          @click="cancelOrder"
          style="margin-left: auto; color: white;"
      >
        取消订单
      </el-button>

    </el-header>

    <!-- 订单内容 -->
    <el-card class="payment-card" shadow="never">
      <!-- 订单信息 -->
      <div class="order-section">
        <h3>订单信息</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderId }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
          <el-descriptions-item label="收货地址">
            <el-icon><Location /></el-icon>
            {{ currentOrder.address }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 商品清单 -->
      <div class="product-section">
        <h3>商品清单</h3>
        <el-table :data="currentOrder.products">
          <el-table-column label="商品" width="400">
            <template #default="{ row }">
              <div class="product-info">
                <el-image
                    :src="row.cover"
                    class="product-image"
                    fit="cover"
                    :preview-src-list="[row.cover]"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
                <div class="product-detail">
                  <div class="product-name">{{ row.productName }}</div>
                  <div v-if="row.specifications?.length" class="specifications">
                    <el-tag
                        v-for="(spec, index) in row.specifications"
                        :key="index"
                        size="small"
                        class="spec-tag"
                    >
                      {{ spec.key }}: {{ spec.value }}
                    </el-tag>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" align="center">
            <template #default="{ row }">¥{{ row.price.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" align="center" />
          <el-table-column label="小计" align="center">
            <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 支付方式 -->
      <div class="payment-methods">
        <h3>选择支付方式</h3>
        <el-radio-group v-model="selectedMethod">
          <el-radio
              v-for="method in paymentMethods"
              :key="method.id"
              :label="method.id"
              class="method-item"
          >
            <img :src="method.icon" class="method-icon" />
            <span>{{ method.name }}</span>
          </el-radio>
        </el-radio-group>
      </div>

      <!-- 支付金额 -->
      <div class="payment-summary">
        <div class="total-amount">
          应付总额：<span class="price">¥{{ currentOrder.totalAmount.toFixed(2) }}</span>
        </div>
        <el-button
            type="danger"
            size="large"
            :loading="isPaying"
            @click="handlePayment"
            class="payment-button"
        >
          <template #loading>
            <span>支付处理中...</span>
          </template>
          <el-icon><CreditCard /></el-icon>
          立即支付
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.payment-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.payment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #FF6347;
  border-radius: 8px;
  padding: 0 20px;
  margin-bottom: 20px;
}

.header-text {
  color: white;
  margin: 0;
}

.payment-card {
  border-radius: 12px;
  padding: 20px;
}

.payment-card h3 {
  color: #606266;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid #FF6347;
}

.order-section {
  margin-bottom: 30px;
}

.product-section {
  margin: 30px 0;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.product-detail {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.specifications {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.spec-tag {
  margin-right: 5px;
  background-color: #f0f2f5;
  border-color: #e4e7ed;
  color: #606266;
  font-size: 12px;
  padding: 0 8px;
  height: 24px;
  line-height: 22px;
}

.payment-methods {
  margin: 30px 0;
}

.method-item {
  width: 300px;
  height: 60px;
  margin: 10px 0;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.method-icon {
  width: 40px;
  margin-right: 15px;
}

.payment-summary {
  text-align: right;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.total-amount {
  font-size: 18px;
  margin-bottom: 20px;
}

.price {
  color: #ff6347;
  font-size: 24px;
  font-weight: bold;
}

.payment-button {
  width: 200px;
  height: 50px;
  font-size: 18px;
}

.image-error {
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 768px) {
  .payment-header {
    flex-direction: column;
    padding: 15px;
    text-align: center;
  }

  .countdown {
    margin-top: 10px;
  }

  .method-item {
    width: 100%;
  }

  .payment-button {
    width: 100%;
  }
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}

.el-radio-group,
.el-table,
.el-button {
  transition: all 0.3s ease;
}

.el-table__body-wrapper {
  overflow-x: auto;
}
</style>