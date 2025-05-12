package com.example.tomatomall.vo;


import com.example.tomatomall.po.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReviewVO {
    private Integer rating;

    private String title;

    private String content;

    private List<String> reviewImages;

    public Review toPO(){
        Review review = new Review();
        review.setRating(rating);
        review.setTitle(title);
        review.setContent(content);
        review.setReviewImages(reviewImages);
        return review;
    }
}
