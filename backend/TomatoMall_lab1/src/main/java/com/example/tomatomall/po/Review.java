package com.example.tomatomall.po;

import com.example.tomatomall.vo.ReviewVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Review {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "review_id")
    private Integer reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id",nullable = false)
    private Account account;

    @Column(nullable = false,name = "rating") //这边可以限制一下评分最高为5最低为1
    private Integer rating;

    @Basic
    @Column(name = "title")
    private String title;

    @Basic
    @Column(nullable = false, columnDefinition = "TEXT", name = "content")
    private String content;

    @Basic
    @Column(name = "is_verified")
    private boolean isVerified = false;

    @Basic
    @Column(name = "helpful_count")
    private Integer helpfulCount = 0;

    @Basic
    @Column(name = "create_at")
    @CreationTimestamp
    private LocalDateTime createAt;

    @Basic
    @Column(name = "update_at")
    @UpdateTimestamp
    private LocalDateTime updateAt;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> reviewImages;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewLike> likes = new HashSet<>();

    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReviewReply reply;

    public ReviewVO toVO() {
        ReviewVO vo = new ReviewVO();
        vo.setTitle(title);
        vo.setContent(content);
        vo.setRating(rating);
        vo.setReviewImages(reviewImages);
        return vo;
    }
}
