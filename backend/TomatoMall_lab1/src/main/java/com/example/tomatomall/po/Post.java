package com.example.tomatomall.po;

import com.example.tomatomall.vo.PostVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer id;

    @Basic
    @Column(name = "circle_id")
    private Integer circleId;

    @Basic
    @Column(name = "account _id")
    private Integer accountId;

    @Basic
    @Column(name = "title")
    private String title;

    @Basic
    @Column(name = "content")
    private String content;

    @Basic
    @Column(name = "create_time")
    @CreationTimestamp
    private LocalDateTime createTime;

    @Basic
    @Column(name = "update_time")
    @UpdateTimestamp
    private LocalDateTime updateTime;

    @Basic
    @Column(name = "view_count")
    private Integer viewCount;

    @Basic
    @Column(name = "like_count")
    private Integer likeCount;

    @Basic
    @Column(name = "comment_count")
    private Integer commentCount;

    @Basic
    @Column(name = "status")
    private Integer status;

    public PostVO toVO(){
        PostVO postVO = new PostVO();
        postVO.setCircleId(this.circleId);
        postVO.setTitle(this.title);
        postVO.setContent(this.content);
        return postVO;
    }
}
