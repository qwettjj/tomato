package com.example.tomatomall.service;

import com.example.tomatomall.vo.PostVO;

import java.util.List;

public interface PostService {
    Boolean createPost(PostVO postVO);
    List<PostVO> getCirclePost(Integer circleId);
    PostVO getPostDetail(Integer postId);
    Integer likePost(Integer postId);
    Integer unlikePost(Integer postId);
    Boolean deletePost(Integer postId);
    Boolean judgeLiked(Integer postId);
    List<PostVO> getLikedPost();
}
