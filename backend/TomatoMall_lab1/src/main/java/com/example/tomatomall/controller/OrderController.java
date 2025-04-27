package com.example.tomatomall.controller;

import com.example.tomatomall.po.CartItem;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.CartItemRepository;
import com.example.tomatomall.repository.OrderRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

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
    public Response<Integer> createDirect(@RequestParam Integer amount,@RequestParam String paymentMethod,@RequestParam Integer productId,@RequestParam Integer quantity) {
        Product product = productRepository.findById(productId).get();
        if(product.getAmount() < quantity) {
            return Response.buildFailure("商品库存不足","400");
        }
        else{
            product.setAmount(product.getAmount() - quantity);
            productRepository.save(product);
        }

        Map<Integer,Integer> productInfo = new HashMap<>();
        productInfo.put(productId,quantity);

        return Response.buildSuccess(orderService.createOrder(amount,paymentMethod,null,productInfo));
    }

    @PostMapping("/create")
    public Response<Integer> createOrder(@RequestParam Integer totalAmount ,@RequestParam String paymentMethod,@RequestParam List<Integer> cartItemId) {
        //锁库存
        List<Product> products = new ArrayList<>();
        for(Integer cartItemId_ : cartItemId){
            CartItem cartItem = cartItemRepository.findById(cartItemId_).get();
            Product product = productRepository.findById(cartItem.getProductId()).get();
            if(product.getAmount() < cartItem.getQuantity())
                return Response.buildFailure("商品库存不足","400");
            products.add(product);
        }

        for(int i=0;i<products.size();i++){
            Product product = products.get(i);
            int quantity = cartItemRepository.findById(cartItemId.get(i)).get().getQuantity();
            product.setAmount(product.getAmount() - quantity);
            productRepository.save(product);
        }



        return Response.buildSuccess(orderService.createOrder(totalAmount, paymentMethod,cartItemId,null));
    }

    @PatchMapping("/update")
    public Response<Boolean> updateOrderStatus(@RequestParam Integer orderId,@RequestParam String status){ return Response.buildSuccess(orderService.updateOrderStatus(orderId,status));}

    @DeleteMapping("/delete")
    public Response<Boolean> deleteOrder(@RequestParam Integer orderId,@RequestParam List<Integer> cartItemId){
        for(Integer cartItemId_ : cartItemId){
            CartItem cartItem = cartItemRepository.findById(cartItemId_).get();
            Product product = productRepository.findById(cartItem.getProductId()).get();
            product.setAmount(product.getAmount()+cartItem.getQuantity());
            productRepository.save(product);
        }
        return Response.buildSuccess(orderService.deleteOrder(orderId));
    }

    @DeleteMapping("/deleteDirect")
    public Response<Boolean> deleteDirect(@RequestParam Integer orderId,@RequestParam Integer productId,@RequestParam Integer quantity){
        Product product = productRepository.findById(productId).get();
        product.setAmount(product.getAmount()+quantity);
        productRepository.save(product);
        return Response.buildSuccess(orderService.deleteOrder(orderId));
    }
}
