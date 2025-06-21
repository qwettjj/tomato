package com.example.tomatomall.controller;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.CartItem;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.CartItemRepository;
import com.example.tomatomall.repository.OrderRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/{id}")
    public Response<Order> getOrder(@PathVariable Integer id) {
        Optional<Order> orderOptional = orderRepository.findById(id);

        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            return Response.buildSuccess(order);
        } else {
            return Response.buildFailure("订单不存在", "404");
        }
    }

    @PostMapping("/createDirect")
    @Transactional
    public Response<Integer> createDirect(
            @RequestParam Integer amount,
            @RequestParam String paymentMethod,
            @RequestParam Integer productId,
            @RequestParam Integer quantity) {

        try {
            // 使用悲观锁锁定商品
            Product product = productRepository.findByIdWithLock(productId)
                    .orElseThrow(() -> TomatoMallException.productNotFound());

            // 检查库存
            if (product.getAmount() < quantity) {
                return Response.buildFailure("商品【" + product.getProductName() + "】库存不足", "400");
            }

            // 扣减库存
            product.setAmount(product.getAmount() - quantity);
            productRepository.save(product);

            // 创建订单信息
            Map<Integer, Integer> productInfo = new HashMap<>();
            productInfo.put(productId, quantity);

            // 创建订单
            Integer orderId = orderService.createOrder(amount, paymentMethod, null, productInfo);
            return Response.buildSuccess(orderId);

        } catch (TomatoMallException e) {
            return Response.buildFailure(e.getMessage(), "400");
        } catch (Exception e) {
            return Response.buildFailure("系统错误，请稍后再试", "500");
        }
    }

    @Transactional
    @PostMapping("/create")
    public Response<Integer> createOrder(
            @RequestParam Integer totalAmount,
            @RequestParam String paymentMethod,
            @RequestParam List<Integer> cartItemIds) {

        try {
            // 1. 验证参数
            if (cartItemIds == null || cartItemIds.isEmpty()) {
                return Response.buildFailure("购物车不能为空", "400");
            }

            // 2. 获取所有商品ID并排序（避免死锁）
            List<Integer> sortedProductIds = getSortedProductIds(cartItemIds);

            // 3. 使用悲观锁锁定所有商品
            Map<Integer, Product> lockedProducts = lockProducts(sortedProductIds);

            // 4. 验证库存
            Map<Integer, Integer> requiredQuantities = validateStock(cartItemIds, lockedProducts);

            // 5. 扣减库存
            deductStock(lockedProducts, requiredQuantities);

            // 6. 创建订单
            Integer orderId = orderService.createOrder(totalAmount, paymentMethod, cartItemIds, null);

            return Response.buildSuccess(orderId);

        } catch (TomatoMallException e) {
            return Response.buildFailure(e.getMessage(), "400");
        } catch (Exception e) {
            return Response.buildFailure("系统错误，请稍后再试", "500");
        }
    }

    @PatchMapping("/update")
    public Response<Boolean> updateOrderStatus(
            @RequestParam Integer orderId,
            @RequestParam String status) {
        return Response.buildSuccess(orderService.updateOrderStatus(orderId, status));
    }

    @DeleteMapping("/delete")
    @Transactional
    public Response<Boolean> deleteOrder(
            @RequestParam Integer orderId,
            @RequestParam List<Integer> cartItemIds) {

        try {
            // 1. 获取订单
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> TomatoMallException.orderNotExists());

            // 2. 恢复库存
            for (Integer cartItemId : cartItemIds) {
                CartItem cartItem = cartItemRepository.findById(cartItemId)
                        .orElseThrow(() -> TomatoMallException.cartItemNotFound());

                // 使用悲观锁锁定商品
                Product product = productRepository.findByIdWithLock(cartItem.getProductId())
                        .orElseThrow(() -> TomatoMallException.productNotFound());

                // 恢复库存
                product.setAmount(product.getAmount() + cartItem.getQuantity());
                productRepository.save(product);
            }

            // 3. 删除订单
            return Response.buildSuccess(orderService.deleteOrder(orderId));

        } catch (TomatoMallException e) {
            return Response.buildFailure(e.getMessage(), "400");
        } catch (Exception e) {
            return Response.buildFailure("系统错误，请稍后再试", "500");
        }
    }

    @DeleteMapping("/deleteDirect")
    @Transactional
    public Response<Boolean> deleteDirect(
            @RequestParam Integer orderId,
            @RequestParam Integer productId,
            @RequestParam Integer quantity) {

        try {
            // 1. 使用悲观锁锁定商品
            Product product = productRepository.findByIdWithLock(productId)
                    .orElseThrow(() -> TomatoMallException.productNotFound());

            // 2. 恢复库存
            product.setAmount(product.getAmount() + quantity);
            productRepository.save(product);

            // 3. 删除订单
            return Response.buildSuccess(orderService.deleteOrder(orderId));

        } catch (TomatoMallException e) {
            return Response.buildFailure(e.getMessage(), "400");
        } catch (Exception e) {
            return Response.buildFailure("系统错误，请稍后再试", "500");
        }
    }

    @GetMapping("/getUserOrders")
    public Response<List<OrderVO>> getUserOrders() {
        try {
            return Response.buildSuccess(orderService.getUserOrders());
        } catch (TomatoMallException e) {
            return Response.buildFailure(e.getMessage(), "400");
        }
    }

    // ================= 辅助方法 =================

    /**
     * 获取排序后的商品ID列表（避免死锁）
     */
    private List<Integer> getSortedProductIds(List<Integer> cartItemIds) {
        Set<Integer> productIds = new HashSet<>();
        for (Integer cartItemId : cartItemIds) {
            CartItem cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> TomatoMallException.cartItemNotFound());
            productIds.add(cartItem.getProductId());
        }

        // 按商品ID排序（避免死锁的关键）
        return productIds.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 使用悲观锁锁定商品
     */
    private Map<Integer, Product> lockProducts(List<Integer> productIds) {
        Map<Integer, Product> lockedProducts = new HashMap<>();
        for (Integer productId : productIds) {
            Product product = productRepository.findByIdWithLock(productId)
                    .orElseThrow(() -> TomatoMallException.productNotFound());
            lockedProducts.put(productId, product);
        }
        return lockedProducts;
    }

    /**
     * 验证库存
     */
    private Map<Integer, Integer> validateStock(
            List<Integer> cartItemIds,
            Map<Integer, Product> lockedProducts) {

        Map<Integer, Integer> requiredQuantities = new HashMap<>();

        for (Integer cartItemId : cartItemIds) {
            CartItem cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> TomatoMallException.cartItemNotFound());

            Product product = lockedProducts.get(cartItem.getProductId());
            if (product == null) {
                throw TomatoMallException.productNotFound();
            }

            // 累计商品所需数量
            int requiredQuantity = requiredQuantities.getOrDefault(product.getId(), 0);
            requiredQuantity += cartItem.getQuantity();
            requiredQuantities.put(product.getId(), requiredQuantity);

            // 检查库存是否足够
            if (product.getAmount() < requiredQuantity) {
                throw TomatoMallException.insufficientStock(
                        "商品【" + product.getProductName() + "】库存不足：" +
                                "剩余 " + product.getAmount() + "，需要 " + requiredQuantity
                );
            }
        }

        return requiredQuantities;
    }

    /**
     * 扣减库存
     */
    private void deductStock(
            Map<Integer, Product> lockedProducts,
            Map<Integer, Integer> requiredQuantities) {

        for (Map.Entry<Integer, Integer> entry : requiredQuantities.entrySet()) {
            Integer productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = lockedProducts.get(productId);
            product.setAmount(product.getAmount() - quantity);
            productRepository.save(product);
        }
    }
}