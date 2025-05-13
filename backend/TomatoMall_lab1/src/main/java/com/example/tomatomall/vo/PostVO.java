package com.example.tomatomall.vo;

import com.example.tomatomall.po.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostVO {
    private Integer circleId;

    private String title;

    private String content;

    public Post toPO(){
        Post post = new Post();
        post.setTitle(this.title);
        post.setContent(this.content);
        post.setCircleId(this.circleId);
        return post;
    }
}
