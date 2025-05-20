package com.example.tomatomall.repository;

import com.example.tomatomall.po.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    boolean existsByPostIdAndAccountId(Integer postId, Integer accountId);
    void deleteByPostIdAndAccountId(Integer postId, Integer accountId);
    int countByPostId(Integer postId);
    List<Like> findByPostId(Integer postId);
}
