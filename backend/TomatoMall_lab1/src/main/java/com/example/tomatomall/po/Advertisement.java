package com.example.tomatomall.po;

import com.example.tomatomall.vo.AdvertisementVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Advertisement {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "advertisement_id")
    private Integer advertisementId;

    @Basic
    @Column(name = "title")
    private String title;

    @Basic
    @Column(name = "content")
    private String content;

    @Basic
    @Column(name = "img_url")
    private String imgUrl;

    @Basic
    @Column(name = "product_id")
    private Integer productId;

    public AdvertisementVO toVO(){
        AdvertisementVO vo = new AdvertisementVO();
        vo.setAdvertisementId(advertisementId);
        vo.setTitle(title);
        vo.setContent(content);
        vo.setImageUrl(imgUrl);
        vo.setProductId(productId);
        return vo;
    }
}
