package com.example.tomatomall.vo;

import com.example.tomatomall.po.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PostVO {
    private Integer postId;

    private Integer accountId;

    private Integer circleId;

    private LocalDateTime createTime;

    private Integer likeCount;

    private Integer commentCount;

    private Integer viewCount;

    private String title;

    private String content;

    public Post toPO(){
        Post post = new Post();
        post.setId(this.postId);
        post.setAccountId(this.accountId);
        post.setCircleId(this.circleId);
        post.setCreateTime(this.createTime);
        post.setLikeCount(this.likeCount);
        post.setCommentCount(this.commentCount);
        post.setViewCount(this.viewCount);
        post.setTitle(this.title);
        post.setContent(this.content);
        return post;
    }
}
