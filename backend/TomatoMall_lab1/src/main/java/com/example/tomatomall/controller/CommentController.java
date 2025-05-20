package com.example.tomatomall.controller;

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
        return Response.buildSuccess(commentService.createComment(commentVO, accountId));
    }

    @GetMapping("/{postId}")
    public Response<List<CommentVO>> getPostComment(@PathVariable Integer postId) {
        return Response.buildSuccess(commentService.getPostComments(postId));
    }

    @PostMapping("/delete/{commentId}")
    public Response<Boolean> deletePost(@PathVariable Integer commentId) {
        return Response.buildSuccess(commentService.deleteComment(commentId));
    }
}
