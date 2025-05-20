package com.example.tomatomall.vo;

import com.example.tomatomall.po.Circle;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CircleVO {
    private String title;

    private String description;

    private String cover;

    private Integer status;

    private Integer creatorId;

    public Circle toPO(){
        Circle circle = new Circle();
        circle.setTitle(this.title);
        circle.setDescription(this.description);
        circle.setCover(this.cover);
        circle.setStatus(this.status);
        circle.setCreatorId(this.creatorId);
        return circle;
    }
}
