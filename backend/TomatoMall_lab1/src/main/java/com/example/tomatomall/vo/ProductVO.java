
package com.example.tomatomall.vo;

import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Specifications;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ProductVO {
    private Integer id;

    private String productName;

    private Integer price;

    private Integer rate;

    private String description;

    private String cover;

    private String detail;

    private Integer amount;

    private Integer frozen;

    private Set<Specifications> specifications;

    public Product toPO(){
        Product po = new Product();
        po.setId(this.id);
        po.setProductName(this.productName);
        po.setPrice(this.price);
        po.setRate(this.rate);
        po.setDescription(this.description);
        po.setCover(this.cover);
        po.setDetail(this.detail);
        po.setAmount(this.amount);
        po.setFrozen(this.frozen);
        po.setSpecifications(this.specifications);
        return po;
    }
}

