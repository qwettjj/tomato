package com.example.tomatomall.service.serviceImpl;


import com.example.tomatomall.po.CartItem;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.CartItemRepository;
import com.example.tomatomall.repository.OrderRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import com.example.tomatomall.enums.OrderStatuEnum;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    SecurityUtil securityUtil;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ProductRepository productRepository;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10); // 可配置线程数

    @Override
    public Integer createOrder(Integer totalAmount, String paymentMethod, List<Integer> cartItemId, Map<Integer,Integer> productInfo)  {
        Integer userId = securityUtil.getCurrentAccount().getId();
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(paymentMethod);
        order.setCreateTime(LocalDateTime.now().withNano(0));
        order.setStatus(OrderStatuEnum.PENDING);

        scheduler.schedule(() -> {
            Optional<Order> optionalOrder = orderRepository.findById(order.getOrderId());
            if (optionalOrder.isPresent()) {
                Order savedOrder = optionalOrder.get();
                if (savedOrder.getStatus() == OrderStatuEnum.PENDING) {
                    savedOrder.setStatus(OrderStatuEnum.TIMEOUT);
                    orderRepository.save(savedOrder);
                    //释放库存
                    if(cartItemId != null) {
                        for(Integer cartItemId_ : cartItemId){
                            CartItem cartItem = cartItemRepository.findById(cartItemId_).get();
                            Product product = productRepository.findById(cartItem.getProductId()).get();
                            product.setAmount(product.getAmount()+cartItem.getQuantity());
                            productRepository.save(product);
                        }
                    }
                    else{
                        for(Integer productId_ : productInfo.keySet()){
                            Product product = productRepository.findById(productId_).get();
                            product.setAmount(product.getAmount()+productInfo.get(productId_));
                            productRepository.save(product);
                        }
                    }
                }
            }
        }, 30, TimeUnit.MINUTES);


        return orderRepository.save(order).getOrderId();
    }

    @Override
    public Boolean  updateOrderStatus(Integer orderId,String status){
        Order order = orderRepository.findById(orderId).get();
        order.setStatus(OrderStatuEnum.valueOf(status));
        orderRepository.save(order);
        return true;
    }

    @Override
    public Boolean deleteOrder(Integer orderId) {
        if (orderId == null || orderId <= 0) {
            return false;
        }
        if(orderRepository.existsById(orderId)){
            orderRepository.deleteById(orderId);
        }
        else{
            return false;
        }
        return true;
    }

    @Override
    public List<OrderVO> getUserOrders() {
        Integer userId = securityUtil.getCurrentAccount().getId();
        List<Order> orders = new ArrayList<>();
        if(orderRepository.existsByUserId(userId)){
            orders = orderRepository.findByUserId(userId);
        }

        List<OrderVO> orderVOList = new ArrayList<>();
        for(Order order : orders){
            orderVOList.add(order.toVO());
        }

        return orderVOList;
    }

}
