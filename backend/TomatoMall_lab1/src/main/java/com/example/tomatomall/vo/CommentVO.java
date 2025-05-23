package com.example.tomatomall.vo;

import com.example.tomatomall.po.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentVO {
    private Integer commentId;

    private Integer postId;

    private Integer accountId;

    private String content;

    private Integer parentId;

    private LocalDateTime createTime;

    public Comment toPO(){
        Comment comment = new Comment();
        comment.setCommentId(commentId);
        comment.setPostId(postId);
        comment.setAccountId(accountId);
        comment.setContent(content);
        comment.setParentId(parentId);
        comment.setCreateTime(createTime);
        return comment;
    }
}
