package com.example.tomatomall.repository;

import com.example.tomatomall.po.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    boolean existsByUserId(Integer userId);
    List<Order> findByUserId(Integer userId);
}
