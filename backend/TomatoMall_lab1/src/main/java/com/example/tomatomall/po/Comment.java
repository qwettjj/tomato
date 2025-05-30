package com.example.tomatomall.po;

import com.example.tomatomall.vo.CommentVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    @Basic
    @Column(name = "post_id")
    private Integer postId;

    @Basic
    @Column(name = "account_id")
    private Integer accountId;

    @Basic
    @Column(name = "content")
    private String content;

    @Basic
    @Column(name = "parent_id")
    private Integer parentId;

    @Basic
    @Column(name = "create_time")
    @CreationTimestamp
    private LocalDateTime createTime;

    @Basic
    @Column(name = "status")
    private Integer status; //记录帖子的状态，比如正在审核，已经删除等

    public CommentVO toVO(){
        CommentVO commentVO = new CommentVO();
        commentVO.setCommentId(commentId);
        commentVO.setPostId(postId);
        commentVO.setAccountId(accountId);
        commentVO.setContent(content);
        commentVO.setParentId(parentId);
        commentVO.setCreateTime(createTime);
        return commentVO;
    }
}
