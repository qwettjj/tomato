package com.example.tomatomall.repository;

import com.example.tomatomall.po.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    boolean existsByPostIdAndAccountId(Integer postId, Integer accountId);
    void deleteByPostIdAndAccountId(Integer postId, Integer accountId);
    int countByPostId(Integer postId);
}
