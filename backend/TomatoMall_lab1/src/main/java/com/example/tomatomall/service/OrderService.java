package com.example.tomatomall.service;

import com.example.tomatomall.po.Order;
import com.example.tomatomall.vo.OrderVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Integer createOrder(Integer totalAmount , String paymentMethod, List<Integer> cartItemId, Map<Integer,Integer> productInfo);
    Boolean updateOrderStatus(Integer orderId,String status);
    Boolean deleteOrder(Integer orderId);

}
