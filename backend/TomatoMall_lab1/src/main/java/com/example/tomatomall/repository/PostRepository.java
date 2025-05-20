package com.example.tomatomall.repository;

import com.example.tomatomall.po.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByCircleIdAndStatus(Integer circleId, Integer status);
    List<Post> findByAccountIdAndStatus(Integer accountId, Integer status);
    Optional<Post> findByPostIdAndStatus(Integer postId, Integer status);
    List<Post> findByCircleId(Integer circleId);
}
