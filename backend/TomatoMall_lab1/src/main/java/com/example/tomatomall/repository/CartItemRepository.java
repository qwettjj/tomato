package com.example.tomatomall.repository;

import com.example.tomatomall.po.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    CartItem findCartItemByUserIdAndProductId(Integer UserId, Integer ProductId);
    CartItem findCartItemByUserId(Integer UserId);
    List<CartItem> findCartItemsByUserId(Integer userId);
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.userId = :userId AND c.productId = :productId")
    int deleteByUserIdAndProductId(@Param("userId") Integer userId,
                                   @Param("productId") Integer productId);
}
