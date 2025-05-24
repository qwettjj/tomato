package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Comment;
import com.example.tomatomall.po.Post;
import com.example.tomatomall.repository.CommentRepository;
import com.example.tomatomall.repository.PostRepository;
import com.example.tomatomall.service.CommentService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    SecurityUtil securityUtil;

    @Override
    public Boolean createComment(CommentVO commentVO) {
        Integer accountId = securityUtil.getCurrentAccount().getId();
        Post post = postRepository.findById(commentVO.getPostId()).isPresent() ? postRepository.findById(commentVO.getPostId()).get() : null;
        if (post == null) {
            throw TomatoMallException.postNotExist();
        }

        Comment comment = commentVO.toPO();
        comment.setAccountId(accountId);
        comment.setStatus(1);
        commentRepository.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        return true;
    }

    @Override
    public List<CommentVO> getPostComments(Integer postId){
        return commentRepository.findByPostIdAndStatus(postId, 1).stream().map(Comment::toVO).collect(Collectors.toList());
    }

    @Override
    public Boolean deleteComment(Integer commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            throw TomatoMallException.commentNotExist();
        }
        commentRepository.delete(comment);
        Post post = postRepository.findById(comment.getPostId()).orElse(null);
        if (post == null) {
            throw TomatoMallException.postNotExist();
        }
        post.setCommentCount(post.getCommentCount() - 1);
        postRepository.save(post);
        return true;
    }

    @Override
    public CommentVO getComment(Integer commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            throw TomatoMallException.commentNotExist();
        }
        return comment.toVO();
    }

}
