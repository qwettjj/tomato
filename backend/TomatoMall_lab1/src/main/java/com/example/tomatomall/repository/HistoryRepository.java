package com.example.tomatomall.repository;

import com.example.tomatomall.enums.OrderStatuEnum;
import com.example.tomatomall.po.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Integer> {
    void deleteByUserId(Integer currentUserId);

    void deleteByOrderId(Integer orderId);

    @Query("SELECT h FROM History h JOIN Order o ON h.orderId = o.orderId " +
            "WHERE h.userId = :userId AND o.status = :status")
    List<History> findValidHistories(@Param("userId") Integer userId,
                                     @Param("status") OrderStatuEnum status);
}
