package com.example.tomatomall.repository;

import com.example.tomatomall.po.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    CartItem findCartItemByUserIdAndProductId(Integer UserId, Integer ProductId);
    CartItem findCartItemByUserId(Integer UserId);
    List<CartItem> findCartItemsByUserId(Integer userId);
}
