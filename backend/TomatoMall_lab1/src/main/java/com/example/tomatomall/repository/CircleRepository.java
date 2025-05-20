package com.example.tomatomall.repository;

import com.example.tomatomall.po.Circle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CircleRepository extends JpaRepository<Circle, Integer> {
    List<Circle> findByTitle(String title);
    List<Circle> findByCreatorId(Integer creatorId);
}
