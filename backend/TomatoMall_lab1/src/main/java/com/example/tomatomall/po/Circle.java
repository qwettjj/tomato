package com.example.tomatomall.po;

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
public class Circle {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "circle_id")
    private Integer circleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)//如果不和商品联系起来可以删掉
    private Product product;

    @JoinColumn(name = "creator_id",nullable = false)
    private Integer creatorId;

    @Basic
    @Column(name = "title")
    private String title;

    @Basic
    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "create_at")
    @CreationTimestamp
    private LocalDateTime createAt;

    @Basic
    @Column(name = "update_at")
    @UpdateTimestamp
    private LocalDateTime updateAt;

//    @ElementCollection(fetch = FetchType.EAGER)
//    private List<String> reviewImages;

    @Basic
    @Column(name = "cover")
    private String cover;

    @Basic
    @Column(name = "member_count")
    private Integer memberCount;

    @Basic
    @Column(name = "status")
    private String status;


//    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<ReviewLike> likes = new HashSet<>();
//
//    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
//    private ReviewReply reply;
}
