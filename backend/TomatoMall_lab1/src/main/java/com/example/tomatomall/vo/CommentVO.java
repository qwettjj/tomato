package com.example.tomatomall.vo;

import com.example.tomatomall.po.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentVO {
    private Integer postId;

    private String content;

    private Integer parentId;

    public Comment toPO(){
        Comment comment = new Comment();
        comment.setPostId(this.postId);
        comment.setContent(this.content);
        comment.setParentId(this.parentId);
        return comment;
    }
}
