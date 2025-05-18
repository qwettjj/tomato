package com.example.tomatomall.repository;

import com.example.tomatomall.po.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findByCircleIdAndStatus(Integer circleId, Integer status);
    Page<Post> findByAccountIdAndStatus(Integer accountId, Integer status, Pageable pageable);
    Optional<Post> findByPostIdAndStatus(Integer postId, Integer status);
}
