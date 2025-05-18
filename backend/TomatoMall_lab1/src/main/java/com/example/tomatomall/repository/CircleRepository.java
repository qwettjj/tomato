package com.example.tomatomall.repository;

import com.example.tomatomall.po.Circle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CircleRepository extends JpaRepository<Circle, Integer> {
    Page<Circle> findByTitleContaining(String title, Pageable pageable);
    List<Circle> findByCreatorId(Integer creatorId);
}
