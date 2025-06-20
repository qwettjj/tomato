package com.example.tomatomall;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.Comment;
import com.example.tomatomall.po.Post;
import com.example.tomatomall.repository.CommentRepository;
import com.example.tomatomall.repository.PostRepository;
import com.example.tomatomall.service.serviceImpl.CommentServiceImpl;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.CommentVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private CommentServiceImpl commentService;

    private CommentVO testCommentVO;
    private Comment testComment;
    private Post testPost;
    private final Integer testUserId = 1;
    private final Integer testPostId = 100;
    private final Integer testCommentId = 200;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testCommentVO = new CommentVO();
        testCommentVO.setPostId(testPostId);
        testCommentVO.setContent("Test comment content");

        testComment = new Comment();
        testComment.setCommentId(testCommentId);
        testComment.setPostId(testPostId);
        testComment.setAccountId(testUserId);
        testComment.setContent("Test comment content");
        testComment.setStatus(1);

        testPost = new Post();
        testPost.setPostId(testPostId);
        testPost.setCommentCount(5);

        // 只有部分测试需要这个桩
        lenient().when(securityUtil.getCurrentAccount()).thenReturn(new Account() {{
            setId(testUserId);
        }});
    }

    @Test
    void createComment_Success() {
        // 模拟帖子存在
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        // 执行测试
        Boolean result = commentService.createComment(testCommentVO);

        // 验证结果
        assertTrue(result);
        verify(postRepository, times(1)).save(testPost);
        assertEquals(6, testPost.getCommentCount()); // 评论数增加
    }

    @Test
    void createComment_PostNotFound() {
        // 模拟帖子不存在
        when(postRepository.findById(testPostId)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        TomatoMallException exception = assertThrows(TomatoMallException.class,
                () -> commentService.createComment(testCommentVO));

        assertEquals("帖子不存在或已经被删除", exception.getMessage());
    }

    @Test
    void getPostComments_Success() {
        // 模拟评论列表
        when(commentRepository.findByPostIdAndStatus(testPostId, 1))
                .thenReturn(Collections.singletonList(testComment));

        // 执行测试
        List<CommentVO> result = commentService.getPostComments(testPostId);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCommentId, result.get(0).getCommentId());
    }

    @Test
    void getPostComments_EmptyList() {
        // 模拟空评论列表
        when(commentRepository.findByPostIdAndStatus(testPostId, 1))
                .thenReturn(Collections.emptyList());

        // 执行测试
        List<CommentVO> result = commentService.getPostComments(testPostId);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteComment_Success() {
        // 模拟评论和帖子存在
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.of(testComment));
        when(postRepository.findById(testPostId)).thenReturn(Optional.of(testPost));

        // 执行测试
        Boolean result = commentService.deleteComment(testCommentId);

        // 验证结果
        assertTrue(result);
        verify(commentRepository, times(1)).delete(testComment);
        assertEquals(4, testPost.getCommentCount()); // 评论数减少
    }

    @Test
    void deleteComment_CommentNotFound() {
        // 模拟评论不存在
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        TomatoMallException exception = assertThrows(TomatoMallException.class,
                () -> commentService.deleteComment(testCommentId));

        assertEquals("评论不存在", exception.getMessage());
    }

    @Test
    void deleteComment_PostNotFound() {
        // 模拟评论存在但帖子不存在
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.of(testComment));
        when(postRepository.findById(testPostId)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        TomatoMallException exception = assertThrows(TomatoMallException.class,
                () -> commentService.deleteComment(testCommentId));

        assertEquals("帖子不存在或已经被删除", exception.getMessage());
    }

    @Test
    void getComment_Success() {
        // 模拟评论存在
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.of(testComment));

        // 执行测试
        CommentVO result = commentService.getComment(testCommentId);

        // 验证结果
        assertNotNull(result);
        assertEquals(testCommentId, result.getCommentId());
    }

    @Test
    void getComment_NotFound() {
        // 模拟评论不存在
        when(commentRepository.findById(testCommentId)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        TomatoMallException exception = assertThrows(TomatoMallException.class,
                () -> commentService.getComment(testCommentId));

        assertEquals("评论不存在", exception.getMessage());
    }
}