package com.example.tomatomall.controller;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.service.CommentService;
import com.example.tomatomall.vo.CommentVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    public Response<Boolean> createComment(@RequestBody CommentVO commentVO, @RequestParam Integer accountId) {
        try{
            return Response.buildSuccess(commentService.createComment(commentVO, accountId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }

    }

    @GetMapping("/{postId}")
    public Response<List<CommentVO>> getPostComment(@PathVariable Integer postId) {
        try{
            return Response.buildSuccess(commentService.getPostComments(postId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
    }

    @PostMapping("/delete/{commentId}")
    public Response<Boolean> deletePost(@PathVariable Integer commentId) {
        try{
            return Response.buildSuccess(commentService.deleteComment(commentId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
    }
}
