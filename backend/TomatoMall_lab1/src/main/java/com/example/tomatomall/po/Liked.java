package com.example.tomatomall.po;


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
public class Liked {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Integer likeId;

    @Basic
    @Column(name = "post_id")
    private Integer postId;

    @Basic
    @Column(name = "account_id")
    private Integer accountId;

    @Basic
    @Column(name = "createTime")
    @CreationTimestamp
    private LocalDateTime createTime;
}
