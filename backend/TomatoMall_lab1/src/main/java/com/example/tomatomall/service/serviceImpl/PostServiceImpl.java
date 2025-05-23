package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Comment;
import com.example.tomatomall.po.Liked;
import com.example.tomatomall.po.Post;
import com.example.tomatomall.repository.CircleMemberRepository;
import com.example.tomatomall.repository.CommentRepository;
import com.example.tomatomall.repository.LikeRepository;
import com.example.tomatomall.repository.PostRepository;
import com.example.tomatomall.service.PostService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.PostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    SecurityUtil securityUtil;

    @Override
    public Boolean createPost(PostVO postVO) {
        Integer accountId = securityUtil.getCurrentAccount().getId();
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
        postRepository.save(post);
        return post.toVO();
    }

    @Override
    public Integer likePost(Integer postId) {
        Integer accountId = securityUtil.getCurrentAccount().getId();
        Post post = postRepository.findById(postId).isPresent() ? postRepository.findById(postId).get() : null;
        if(post == null){
            throw TomatoMallException.postNotExist();
        }

        if(likeRepository.existsByPostIdAndAccountId(postId,accountId)){
            throw TomatoMallException.alreadyLike();
        }

        Liked liked = new Liked();
        liked.setPostId(postId);
        liked.setAccountId(accountId);
        likeRepository.save(liked);

        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);

        return post.getLikeCount();
    }

    @Override
    public Integer unlikePost(Integer postId) {
        Integer accountId = securityUtil.getCurrentAccount().getId();
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
        return post.getLikeCount();
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
        for(Liked liked : likeRepository.findByPostId(postId)){
            likeRepository.delete(liked);
        }
        postRepository.delete(post);
        return true;
    }

    @Override
    public Boolean judgeLiked(Integer postId) {
        Integer accountId = securityUtil.getCurrentAccount().getId();
        if(likeRepository.existsByPostIdAndAccountId(postId,accountId)){
            return true;
        }
        return false;
    }

    @Override
    public List<PostVO> getLikedPost() {
        Integer accountId = securityUtil.getCurrentAccount().getId();
        List<Liked> liked =  likeRepository.findByAccountId(accountId);
        List<PostVO> postVO = new ArrayList<>();
        for(Liked liked_ : liked){
            postVO.add(postRepository.findById(liked_.getPostId()).get().toVO());
        }

        return postVO;
    }
}
