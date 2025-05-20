package com.example.tomatomall.controller;

import com.example.tomatomall.service.PostService;
import com.example.tomatomall.vo.CircleVO;
import com.example.tomatomall.vo.PostVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping
    public Response<Boolean> createPost(@RequestBody PostVO postVO, @RequestAttribute Integer accountId) {
        return Response.buildSuccess(postService.createPost(postVO, accountId));
    }

    @GetMapping("/{postId}")
    public Response<PostVO> getPostDetail(@PathVariable Integer postId) {
        return Response.buildSuccess(postService.getPostDetail(postId));
    }

    @GetMapping("/circle/{circleId}")
    public Response<List<PostVO>> getCirclePost(@PathVariable Integer circleId) {
        return Response.buildSuccess(postService.getCirclePost(circleId));
    }

    @PostMapping("/like/{postId}")
    public Response<Boolean> likePost(@PathVariable Integer postId, @RequestAttribute Integer accountId) {
        return Response.buildSuccess(postService.likePost(postId, accountId));
    }

    @PostMapping("/unlike/{postId}")
    public Response<Boolean> unlikePost(@PathVariable Integer postId, @RequestAttribute Integer accountId) {
        return Response.buildSuccess(postService.unlikePost(postId, accountId));
    }

    @PostMapping("/delete/{postId}")
    public Response<Boolean> deletePost(@PathVariable Integer postId) {
        return Response.buildSuccess(postService.deletePost(postId));
    }
}
