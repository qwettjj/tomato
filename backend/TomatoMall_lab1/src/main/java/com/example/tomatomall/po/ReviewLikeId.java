package com.example.tomatomall.po;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class  ReviewLikeId implements Serializable {
    private Integer reviewId;
    private Integer accountId;
}
