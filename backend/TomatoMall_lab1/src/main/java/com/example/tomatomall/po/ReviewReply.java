package com.example.tomatomall.po;

import com.example.tomatomall.vo.ReviewReplyVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import javax.persistence.Entity;
import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ReviewReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Account admin;

    @Basic
    @Column(nullable = false , columnDefinition = "TEXT", name = "content")
    private String content;

    @Column(name = "create_at")
    @CreationTimestamp
    private LocalDateTime createAt;

    public ReviewReplyVO toVO(){
        ReviewReplyVO vo = new ReviewReplyVO();
        vo.setContent(content);
        return vo;
    }
}
