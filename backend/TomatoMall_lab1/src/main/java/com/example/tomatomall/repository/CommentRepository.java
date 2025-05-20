package com.example.tomatomall.repository;

import com.example.tomatomall.po.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPostIdAndStatus(Integer postId, Integer status);
    List<Comment> findByParentId(Integer parentId);
    List<Comment> findByPostId(Integer postId);
}
