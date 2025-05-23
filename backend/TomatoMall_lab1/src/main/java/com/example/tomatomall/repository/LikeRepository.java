package com.example.tomatomall.repository;

import com.example.tomatomall.po.Liked;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Liked, Integer> {
    boolean existsByPostIdAndAccountId(Integer postId, Integer accountId);
    void deleteByPostIdAndAccountId(Integer postId, Integer accountId);
    int countByPostId(Integer postId);
    List<Liked> findByPostId(Integer postId);
    List<Liked> findByAccountId(Integer accountId);
}
