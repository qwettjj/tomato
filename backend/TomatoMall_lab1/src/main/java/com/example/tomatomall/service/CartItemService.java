package com.example.tomatomall.service;

import com.example.tomatomall.po.CartItem;
import com.example.tomatomall.vo.CartItemVO;
import com.example.tomatomall.vo.OrderVO;

import java.util.List;

public interface CartItemService {
    Boolean addCartItem(Integer productId, Integer quantity);

    Boolean removeCartItem(Integer productId);

    Boolean updateCartItem(Integer productId, Integer newQuantity);

    List<CartItem> getCartItem();

    CartItemVO getOneCartItem(Integer orderId);
}
