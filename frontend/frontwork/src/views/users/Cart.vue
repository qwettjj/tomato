<template>
  <div class="cart-container">
    <h1>我的购物车</h1>

    <!-- 购物车商品列表 -->
    <div v-if="cartItems.length > 0" class="cart-items">
      <div class="cart-header">
        <div class="header-item select">
          <el-checkbox
              v-model="selectAll"
              :indeterminate="isIndeterminate"
              @change="handleSelectAll"
          />
        </div>
        <div class="header-item product-info">商品信息</div>
        <div class="header-item price">单价</div>
        <div class="header-item quantity">数量</div>
        <div class="header-item subtotal">小计</div>
        <div class="header-item action">操作</div>
      </div>

      <div v-for="item in cartItems" :key="item.id" class="cart-item">
        <div class="select">
          <el-checkbox
              v-model="item.selected"
              :disabled="!item.product"
              @change="handleItemSelect"
          />
        </div>
        <div class="product-info">
          <img v-if="item.product" :src="item.product.cover" :alt="item.product.productName" class="product-image">
          <div class="product-details">
            <h3>{{ item.product?.productName || '商品加载中...' }}</h3>
            <p class="description">{{ item.product?.description || '暂无描述' }}</p>
            <p v-if="!item.product" class="loading-text">正在加载商品信息...</p>
          </div>
        </div>
        <div class="price">¥{{ item.product?.price?.toFixed(2) || '0.00' }}</div>
        <div class="quantity">
          <button
              @click="updateQuantity(item, -1)"
              :disabled="item.quantity <= 1 || !item.product"
          >-</button>
          <input
              type="number"
              v-model.number="item.quantity"
              min="1"
              @change="e => {
                const value = parseInt(e.target.value)
                if (value > item.product?.amount) {
                  item.quantity = item.product?.amount || 1
                }
              }"
              :disabled="!item.product"
              :max="item.product?.amount"
          >
          <button
              @click="updateQuantity(item, 1)"
              :disabled="!item.product || item.quantity >= item.product?.amount"
          >+</button>
        </div>
        <div class="subtotal">
          ¥{{ (item.product?.price * item.quantity || 0).toFixed(2) }}
        </div>
        <div class="action">
          <!-- 修改点：参数改为 item.id -->
          <button @click="removeItem(item.id)" class="remove-btn">删除</button>
        </div>
      </div>
    </div>

    <!-- 空购物车提示 -->
    <div v-else class="empty-cart">
      <p>您的购物车还是空的~</p>
      <router-link to="/allproduct" class="go-shopping">去逛逛</router-link>
    </div>

    <!-- 购物车汇总 -->
    <div v-if="cartItems.length > 0" class="cart-summary">
      <div class="summary-details">
        <p>已选 <span class="highlight">{{ selectedItemsCount }}</span> 件商品</p>
        <p>合计: <span class="highlight">¥{{ selectedTotalAmount.toFixed(2) }}</span></p>
      </div>
      <button
          class="checkout-btn"
          :disabled="!canCheckout"
          @click="goToCheckout"
      >去结算</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElCheckbox } from 'element-plus';
import type { AxiosError } from 'axios';
import {
  getCartItems,
  removeCartItem,
  updateCartItem,
  removeCartItemById
} from '../../api/cartItem';
import { getProduct } from '../../api/products';
import { createOrder } from '../../api/orders';
import type { CartItem, Product } from '../../types';

const router = useRouter();
const cartItems = ref<Array<CartItem & { product?: Product; selected: boolean }>>([]);
const allProductsLoaded = ref(false);
const selectAll = ref(false);
const isIndeterminate = ref(false);

// 计算属性
const selectedItems = computed(() =>
    cartItems.value.filter(item => item.selected && item.product)
);

const selectedItemsCount = computed(() =>
    selectedItems.value.reduce((sum, item) => sum + item.quantity, 0)
);

const selectedTotalAmount = computed(() =>
    selectedItems.value.reduce((sum, item) => {
      const price = item.product?.price || 0;
      return sum + (price * item.quantity);
    }, 0)
);

const canCheckout = computed(() =>
    selectedItems.value.length > 0 && allProductsLoaded.value
);

// 加载购物车数据
const loadCart = async () => {
  try {
    const res = await getCartItems();
    const basicItems = res.data;
    console.log(basicItems);

    const itemsWithProducts = await Promise.all(
        basicItems.map(async item => {
          try {
            const productRes = await getProduct(item.productId);
            return {
              ...item,
              cartItemId : item.cartItemId,
              id:item.cartItemId,
              product: productRes.data,
              quantity: item.quantity,
              selected: false
            };
          } catch (error) {
            console.error(`无法获取商品 ${item.productId} 的信息`, error);
            return {
              ...item,
              cartItemId : item.cartItemId,
              id:item.cartItemId,
              product: null,
              quantity: item.quantity,
              selected: false
            };
          }
        })
    );

    cartItems.value = itemsWithProducts;
    allProductsLoaded.value = itemsWithProducts.every(item => !!item.product);
    selectAll.value = false;
    isIndeterminate.value = false;
    updateSelectAllState();
  } catch (error) {
    const axiosError = error as AxiosError;
    ElMessage.error(axiosError.response?.data?.message || '获取购物车失败');
  }
};

// 更新全选状态
const updateSelectAllState = () => {
  const selectedCount = selectedItems.value.length;
  const totalCount = cartItems.value.filter(item => item.product).length;
  selectAll.value = selectedCount === totalCount && totalCount > 0;
  isIndeterminate.value = selectedCount > 0 && selectedCount < totalCount;
};

// 全选/取消全选
const handleSelectAll = (val: boolean) => {
  cartItems.value.forEach(item => {
    if (item.product) {
      item.selected = val;
    }
  });
};

// 单个商品选择
const handleItemSelect = () => {
  updateSelectAllState();
};

// 更新商品数量
const updateQuantity = async (item: CartItem, change: number) => {
  if (!item.product) {
    ElMessage.warning('商品信息未加载完成，请稍后');
    return;
  }

  try {
    let newQuantity = item.quantity;

    if (change === 1) {
      newQuantity += 1;
    } else if (change === -1) {
      newQuantity -= 1;
    } else {
      newQuantity = Math.max(1, newQuantity);
    }

    // 增强边界检查
    newQuantity = Math.max(1, Math.min(newQuantity, item.product.amount));

    await updateCartItem({
      productId: item.productId,
      quantity: newQuantity
    });

    item.quantity = newQuantity;
  } catch (error) {
    const axiosError = error as AxiosError;
    ElMessage.error(axiosError.response?.data?.message || '更新失败');
    await loadCart();
  }
};

// 删除商品
const removeItem = async (cartItemId: number) => {
  try {
    await removeCartItemById(cartItemId);
    ElMessage.success('商品已移除');
    cartItems.value = cartItems.value.filter(item => item.id !== cartItemId);
    updateSelectAllState();
  } catch (error) {
    const axiosError = error as AxiosError;
    ElMessage.error(axiosError.response?.data?.message || '删除失败');
  }
};

// 结算功能
const goToCheckout = async () => {
  try {
    const selectedCartItemIds = selectedItems.value
        .filter(item => !!item.cartItemId) // 添加过滤确保有效ID
        .map(item => item.cartItemId!); // 非空断言
    console.log(selectedTotalAmount.value,);
    console.log(selectedCartItemIds)
    const res = await createOrder({
      totalAmount: selectedTotalAmount.value,
      paymentMethod: "Alipay",
      cartItemId: selectedCartItemIds
    });

    console.log(res.data);
    router.push({
      path: '/order',
      query: {
        orderId: res.data,
        isDirect : false,
        amount: selectedTotalAmount.value.toFixed(2),
        cartItemIds: selectedCartItemIds
      }
    });
  } catch (error) {
    const axiosError = error as AxiosError;
    let errorMessage = '创建订单失败';
    if (axiosError.response) {
      errorMessage = (axiosError.response.data as any)?.message || axiosError.message;
    }
    ElMessage.error(errorMessage);
  }
};

onMounted(() => {
  loadCart();
});
</script>

<style scoped>
.cart-container {
  max-width: 1200px;
  margin: 40px 100px;
  padding: 30px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
  font-family: 'Segoe UI', system-ui, sans-serif;
}

h1 {
  font-size: 32px;
  margin-bottom: 30px;
  color: #2c3e50;
  text-align: center;
  font-weight: 600;
  letter-spacing: 0.5px;
}

/* 购物车头部 */
.cart-header {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 2px solid #f0f2f5;
  font-weight: 500;
  color: #7f8c8d;
  background-color: #f8f9fa;
  border-radius: 8px 8px 0 0;
}

/* 购物车商品项 */
.cart-item {
  display: flex;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #eceff1;
  transition: background-color 0.2s;
}

.cart-item:hover {
  background-color: #fafafa;
}

/* 列宽分配 */
.header-item,
.cart-item > div {
  flex: 1;
  padding: 0 12px;
}

.select {
  flex: 0 0 60px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.product-info {
  flex: 3;
  display: flex;
  align-items: center;
  gap: 20px;
}

.price {
  font-weight: 600;
  color: #2c3e50;
}

.quantity {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.quantity button {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  background-color: #f5f6fa;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.quantity button:hover:not(:disabled) {
  background-color: #42b983;
  color: white;
}

.quantity button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background-color: #f5f6fa !important; /* 保持禁用状态颜色一致 */
}


.quantity input {
  width: 60px;
  height: 36px;
  text-align: center;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
}

.subtotal {
  font-weight: 600;
  color: #42b983;
}

/* 商品图片 */
.product-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #eee;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.product-details h3 {
  font-size: 16px;
  margin: 0 0 8px;
  color: #34495e;
  font-weight: 600;
}

.description {
  font-size: 13px;
  color: #95a5a6;
  line-height: 1.4;
  max-width: 400px;
}

/* 操作按钮 */
.action {
  text-align: center;
}

.remove-btn {
  padding: 8px 16px;
  background-color: #ff4757;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.remove-btn:hover {
  background-color: #ff6b81;
}

/* 空购物车 */
.empty-cart {
  text-align: center;
  padding: 60px 0;
  color: #b2bec3;
}

.go-shopping {
  display: inline-block;
  margin-top: 20px;
  padding: 12px 30px;
  background-color: #42b983;
  color: white;
  text-decoration: none;
  border-radius: 25px;
  transition: transform 0.2s;
}

.go-shopping:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

/* 购物车汇总 */
.cart-summary {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 2px solid #f0f2f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-details p {
  margin: 8px 0;
  font-size: 16px;
  color: #34495e;
}

.highlight {
  color: #42b983;
  font-weight: 700;
  font-size: 18px;
}

.checkout-btn {
  padding: 14px 45px;
  font-size: 16px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  letter-spacing: 0.5px;
}

.checkout-btn:hover:not(:disabled) {
  background-color: #36976d;
  box-shadow: 0 4px 15px rgba(66, 185, 131, 0.4);
}

.checkout-btn:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

.loading-text {
  color: #b2bec3;
  font-size: 12px;
  font-style: italic;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .cart-container {
    padding: 20px;
    margin: 20px;
  }

  .cart-header {
    display: none;
  }

  .cart-item {
    flex-wrap: wrap;
    position: relative;
    padding: 15px;
    gap: 10px;
  }

  .select {
    position: absolute;
    left: 10px;
    top: 10px;
  }

  .product-info {
    flex: 1 1 100%;
    order: 1;
  }

  .price,
  .quantity,
  .subtotal,
  .action {
    flex: 1 1 45%;
    text-align: left;
    padding: 5px;
  }

  .checkout-btn {
    width: 100%;
    margin-top: 15px;
  }
}
</style>