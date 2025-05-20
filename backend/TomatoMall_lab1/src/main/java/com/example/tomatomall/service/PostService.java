package com.example.tomatomall.service;

import com.example.tomatomall.vo.PostVO;

import java.util.List;

public interface PostService {
    Boolean createPost(PostVO postVO, Integer accountId);
    List<PostVO> getCirclePost(Integer circleId);
    PostVO getPostDetail(Integer postId);
    Boolean likePost(Integer postId, Integer AccountId);
    Boolean unlikePost(Integer postId, Integer AccountId);
    Boolean deletePost(Integer postId);
}
