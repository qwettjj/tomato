package com.example.tomatomall.controller;

import com.example.tomatomall.exception.TomatoMallException;
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
    public Response<Boolean> createPost(@RequestBody PostVO postVO) {
        try{
            return Response.buildSuccess(postService.createPost(postVO));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"400");
        }

    }

    @GetMapping("/{postId}")
    public Response<PostVO> getPostDetail(@PathVariable Integer postId) {
        try{
            return Response.buildSuccess(postService.getPostDetail(postId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"400");
        }
    }

    @GetMapping("/circle/{circleId}")
    public Response<List<PostVO>> getCirclePost(@PathVariable Integer circleId) {
        try{
            return Response.buildSuccess(postService.getCirclePost(circleId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"400");
        }
    }

    @PostMapping("/like/{postId}")
    public Response<Integer> likePost(@PathVariable Integer postId) {
        try{
            return Response.buildSuccess(postService.likePost(postId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"400");
        }
    }

    @PostMapping("/unlike/{postId}")
    public Response<Integer> unlikePost(@PathVariable Integer postId) {
        try{
            return Response.buildSuccess(postService.unlikePost(postId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"400");
        }
    }

    @PostMapping("/delete/{postId}")
    public Response<Boolean> deletePost(@PathVariable Integer postId) {
        try{
            return Response.buildSuccess(postService.deletePost(postId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"400");
        }
    }

    @GetMapping("/judgeLiked/{postId}")
    public Response<Boolean> judgeLiked(@PathVariable Integer postId) {
        try{
            return Response.buildSuccess(postService.judgeLiked(postId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"400");
        }
    }

    @GetMapping("/getLiked")
    public Response<List<PostVO>> getLiked() {
        try{
            return Response.buildSuccess(postService.getLikedPost());
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"400");
        }
    }
}
