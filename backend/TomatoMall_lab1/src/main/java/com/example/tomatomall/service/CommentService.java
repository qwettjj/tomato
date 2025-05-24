package com.example.tomatomall.service;

import com.example.tomatomall.vo.CommentVO;

import java.util.List;

public interface CommentService {
    Boolean createComment(CommentVO commentVO);
    List<CommentVO> getPostComments(Integer postId);
    Boolean deleteComment(Integer commentId);
    CommentVO getComment(Integer commentId);
}
