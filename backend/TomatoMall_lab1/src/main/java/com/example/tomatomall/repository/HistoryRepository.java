package com.example.tomatomall.repository;

import com.example.tomatomall.po.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Integer> {
    List<History> findByUserId(Integer userId);

    void deleteByUserId(Integer currentUserId);

    void deleteByOrderId(Integer orderId);
}
