<template>
  <div class="cart-container">
    <h1>我的购物车</h1>

    <!-- 购物车商品列表 -->
    <div v-if="cartItems.length > 0" class="cart-items">
      <div class="cart-header">
        <div class="header-item product-info">商品信息</div>
        <div class="header-item price">单价</div>
        <div class="header-item quantity">数量</div>
        <div class="header-item subtotal">小计</div>
        <div class="header-item action">操作</div>
      </div>

      <div v-for="item in cartItems" :key="item.cartItemId" class="cart-item">
        <div class="product-info">
          <img :src="item.cover" :alt="item.title" class="product-image">
          <div class="product-details">
            <h3>{{ item.title }}</h3>
            <p class="description">{{ item.description }}</p>
          </div>
        </div>
        <div class="price">¥{{ item.price.toFixed(2) }}</div>
        <div class="quantity">
          <button @click="decreaseQuantity(item)" :disabled="item.quantity <= 1">-</button>
          <input type="number" v-model.number="item.quantity" min="1" @change="updateQuantity(item)">
          <button @click="increaseQuantity(item)">+</button>
        </div>
        <div class="subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
        <div class="action">
          <button @click="removeItem(item.cartItemId)" class="remove-btn">删除</button>
        </div>
      </div>
    </div>

    <!-- 空购物车提示 -->
    <div v-else class="empty-cart">
      <p>您的购物车还是空的~</p>
      <router-link to="/products" class="go-shopping">去逛逛</router-link>
    </div>

    <!-- 购物车汇总 -->
    <div v-if="cartItems.length > 0" class="cart-summary">
      <div class="summary-details">
        <p>共 <span class="highlight">{{ totalItems }}</span> 件商品</p>
        <p>合计: <span class="highlight">¥{{ totalAmount.toFixed(2) }}</span></p>
      </div>
      <button class="checkout-btn">去结算</button>
    </div>

    <!-- 添加商品模拟区域 -->
    <div class="add-product-demo">
      <h3>模拟添加商品到购物车</h3>
      <div class="demo-products">
        <div class="demo-product" v-for="product in demoProducts" :key="product.productId">
          <img :src="product.cover" :alt="product.title" class="demo-image">
          <h4>{{ product.title }}</h4>
          <p>¥{{ product.price.toFixed(2) }}</p>
          <button @click="addToCart(product)">加入购物车</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

// 模拟购物车数据
const cartItems = ref([
  {
    cartItemId: "201",
    productId: "102",
    title: "代码整洁之道",
    price: 59.00,
    description: "软件工程领域的经典著作",
    cover: "https://example.com/covers/clean-code.jpg",
    detail: "本书提出一种观念：代码质量与其整洁度成正比",
    quantity: 1
  },
  {
    cartItemId: "202",
    productId: "101",
    title: "深入理解Java虚拟机",
    price: 99.50,
    description: "Java开发者必读经典，全面讲解JVM工作原理",
    cover: "https://example.com/covers/jvm.jpg",
    detail: "本书详细讲解了Java虚拟机的体系结构、内存管理、字节码执行等核心内容",
    quantity: 2
  }
]);

// 模拟商品数据
const demoProducts = ref([
  {
    productId: "101",
    title: "深入理解Java虚拟机",
    price: 99.50,
    description: "Java开发者必读经典，全面讲解JVM工作原理",
    cover: "https://example.com/covers/jvm.jpg",
    detail: "本书详细讲解了Java虚拟机的体系结构、内存管理、字节码执行等核心内容",
    stock: 10
  },
  {
    productId: "102",
    title: "代码整洁之道",
    price: 59.00,
    description: "软件工程领域的经典著作",
    cover: "https://example.com/covers/clean-code.jpg",
    detail: "本书提出一种观念：代码质量与其整洁度成正比",
    stock: 5
  },
  {
    productId: "103",
    title: "设计模式",
    price: 79.00,
    description: "面向对象设计经典著作",
    cover: "https://example.com/covers/design-patterns.jpg",
    detail: "介绍23种经典设计模式",
    stock: 8
  }
]);

// 计算属性
const totalItems = computed(() => cartItems.value.length);
const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0);
});

// 方法
const addToCart = (product) => {
  // 检查是否已在购物车中
  const existingItem = cartItems.value.find(item => item.productId === product.productId);

  if (existingItem) {
    // 如果已存在，增加数量
    if (existingItem.quantity < product.stock) {
      existingItem.quantity += 1;
      alert(`已增加 ${product.title} 的数量`);
    } else {
      alert(`无法添加，库存不足 (剩余 ${product.stock} 件)`);
    }
  } else {
    // 如果不存在，添加新商品
    if (product.stock > 0) {
      cartItems.value.push({
        cartItemId: `20${cartItems.value.length + 1}`,
        productId: product.productId,
        title: product.title,
        price: product.price,
        description: product.description,
        cover: product.cover,
        detail: product.detail,
        quantity: 1
      });
      alert(`已添加 ${product.title} 到购物车`);
    } else {
      alert(`无法添加，库存不足 (剩余 ${product.stock} 件)`);
    }
  }
};

const removeItem = (cartItemId) => {
  const index = cartItems.value.findIndex(item => item.cartItemId === cartItemId);
  if (index !== -1) {
    const removedItem = cartItems.value[index];
    cartItems.value.splice(index, 1);
    alert(`已移除 ${removedItem.title}`);
  }
};

const increaseQuantity = (item) => {
  const product = demoProducts.value.find(p => p.productId === item.productId);
  if (item.quantity < product.stock) {
    item.quantity += 1;
  } else {
    alert(`无法增加，库存不足 (剩余 ${product.stock} 件)`);
  }
};

const decreaseQuantity = (item) => {
  if (item.quantity > 1) {
    item.quantity -= 1;
  }
};

const updateQuantity = (item) => {
  const product = demoProducts.value.find(p => p.productId === item.productId);
  if (item.quantity < 1) {
    item.quantity = 1;
  } else if (item.quantity > product.stock) {
    item.quantity = product.stock;
    alert(`数量超过库存，已调整为最大可用数量 ${product.stock}`);
  }
};
</script>

<style scoped>
.cart-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

/* 购物车商品列表样式 */
.cart-items {
  border: 1px solid #eee;
  border-radius: 5px;
  margin-bottom: 30px;
}

.cart-header {
  display: flex;
  padding: 15px;
  background-color: #f9f9f9;
  border-bottom: 1px solid #eee;
  font-weight: bold;
}

.header-item {
  padding: 0 10px;
}

.product-info {
  flex: 3;
}

.price, .quantity, .subtotal, .action {
  flex: 1;
  text-align: center;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.product-info {
  display: flex;
  align-items: center;
}

.product-image {
  width: 80px;
  height: 100px;
  object-fit: cover;
  margin-right: 15px;
}

.product-details h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
}

.description {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.quantity {
  display: flex;
  justify-content: center;
  align-items: center;
}

.quantity button {
  width: 30px;
  height: 30px;
  border: 1px solid #ddd;
  background-color: #f5f5f5;
  cursor: pointer;
}

.quantity input {
  width: 50px;
  height: 30px;
  text-align: center;
  margin: 0 5px;
  border: 1px solid #ddd;
}

.remove-btn {
  background-color: #ff4d4f;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 3px;
  cursor: pointer;
}

.remove-btn:hover {
  background-color: #ff7875;
}

/* 空购物车样式 */
.empty-cart {
  text-align: center;
  padding: 50px;
  border: 1px dashed #ddd;
  border-radius: 5px;
  margin-bottom: 30px;
}

.go-shopping {
  display: inline-block;
  margin-top: 15px;
  padding: 8px 20px;
  background-color: #1890ff;
  color: white;
  text-decoration: none;
  border-radius: 3px;
}

.go-shopping:hover {
  background-color: #40a9ff;
}

/* 购物车汇总样式 */
.cart-summary {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 5px;
}

.summary-details {
  margin-right: 30px;
  text-align: right;
}

.highlight {
  color: #f5222d;
  font-size: 18px;
  font-weight: bold;
}

.checkout-btn {
  padding: 10px 30px;
  background-color: #52c41a;
  color: white;
  border: none;
  border-radius: 3px;
  font-size: 16px;
  cursor: pointer;
}

.checkout-btn:hover {
  background-color: #73d13d;
}

/* 添加商品模拟区域 */
.add-product-demo {
  margin-top: 50px;
  padding-top: 20px;
  border-top: 1px dashed #ddd;
}

.demo-products {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-top: 20px;
}

.demo-product {
  width: calc(33.333% - 20px);
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 5px;
  text-align: center;
}

.demo-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  margin-bottom: 10px;
}

.demo-product button {
  margin-top: 10px;
  padding: 5px 15px;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 3px;
  cursor: pointer;
}

.demo-product button:hover {
  background-color: #40a9ff;
}

@media (max-width: 768px) {
  .cart-header {
    display: none;
  }

  .cart-item {
    flex-direction: column;
    align-items: flex-start;
    position: relative;
    padding-bottom: 50px;
  }

  .product-info, .price, .quantity, .subtotal {
    width: 100%;
    margin-bottom: 10px;
  }

  .action {
    position: absolute;
    right: 15px;
    bottom: 15px;
  }

  .demo-product {
    width: calc(50% - 10px);
  }
}

@media (max-width: 480px) {
  .demo-product {
    width: 100%;
  }
}
</style>