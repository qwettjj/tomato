package com.example.tomatomall.vo;

import com.example.tomatomall.po.Advertisement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdvertisementVO {
    private Integer advertisementId;
    private String title;
    private String content;
    private String imageUrl;
    private Integer productId;

    public Advertisement toPO(){
        Advertisement advertisement = new Advertisement();
        advertisement.setAdvertisementId(advertisementId);
        advertisement.setTitle(title);
        advertisement.setContent(content);
        advertisement.setImgUrl(imageUrl);
        advertisement.setProductId(productId);
        return advertisement;
    }
}
