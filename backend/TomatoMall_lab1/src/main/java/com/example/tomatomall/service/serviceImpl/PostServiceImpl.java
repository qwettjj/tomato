package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Comment;
import com.example.tomatomall.po.Like;
import com.example.tomatomall.po.Post;
import com.example.tomatomall.repository.CircleMemberRepository;
import com.example.tomatomall.repository.CommentRepository;
import com.example.tomatomall.repository.LikeRepository;
import com.example.tomatomall.repository.PostRepository;
import com.example.tomatomall.service.PostService;
import com.example.tomatomall.vo.PostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    PostRepository postRepository;

    @Autowired
    CircleMemberRepository circleMemberRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    CommentRepository commentRepository;

    @Override
    public Boolean createPost(PostVO postVO, Integer accountId) {
        if(!circleMemberRepository.existsByCircleIdAndAccountId(postVO.getCircleId(),accountId)){
            throw TomatoMallException.NotCircleMember();
        }

        Post post = new Post();
        post.setTitle(postVO.getTitle());
        post.setContent(postVO.getContent());
        post.setAccountId(accountId);
        post.setCircleId(postVO.getCircleId());
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setStatus(1);
        postRepository.save(post);
        return true;
    }

    @Override
    public List<PostVO> getCirclePost(Integer circleId) {
        return postRepository.findByCircleIdAndStatus(circleId, 1).stream().map(Post::toVO).collect(Collectors.toList());
    }

    @Override
    public PostVO getPostDetail(Integer postId) {
        Post post = postRepository.findById(postId).isPresent() ? postRepository.findById(postId).get() : null;
        if(post == null){
            throw TomatoMallException.postNotExist();
        }

        post.setViewCount(post.getViewCount() + 1);
        return post.toVO();
    }

    @Override
    public Boolean likePost(Integer postId,Integer accountId) {
        Post post = postRepository.findById(postId).isPresent() ? postRepository.findById(postId).get() : null;
        if(post == null){
            throw TomatoMallException.postNotExist();
        }

        if(likeRepository.existsByPostIdAndAccountId(postId,accountId)){
            throw TomatoMallException.alreadyLike();
        }

        Like like = new Like();
        like.setPostId(postId);
        like.setAccountId(accountId);
        likeRepository.save(like);

        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);

        return true;
    }

    @Override
    public Boolean unlikePost(Integer postId,Integer accountId) {
        Post post = postRepository.findById(postId).isPresent() ? postRepository.findById(postId).get() : null;
        if(post == null){
            throw TomatoMallException.postNotExist();
        }

        if(likeRepository.existsByPostIdAndAccountId(postId,accountId)){
            throw TomatoMallException.notAlreadyLike();
        }

        likeRepository.deleteByPostIdAndAccountId(postId,accountId);

        post.setLikeCount(post.getLikeCount() - 1);
        postRepository.save(post);
        return true;
    }

    @Override
    public Boolean deletePost(Integer postId) {
        Post post = postRepository.findById(postId).isPresent() ? postRepository.findById(postId).get() : null;
        if(post == null){
            throw TomatoMallException.postNotExist();
        }
        for(Comment comment : commentRepository.findByPostId(postId)){
            commentRepository.delete(comment);
        }
        for(Like like : likeRepository.findByPostId(postId)){
            likeRepository.delete(like);
        }
        postRepository.delete(post);
        return true;
    }
}
