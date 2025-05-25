
package com.example.tomatomall.po;

import com.example.tomatomall.vo.ProductVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "product_id")
    private Integer id;

    @Basic
    @Column(name = "product_name")
    private String productName;

    @Basic
    @Column(name = "price")
    private Integer price;

    @Basic
    @Column(name = "rate")
    private Integer rate;

    @Basic
    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "cover")
    private String cover;

    @Basic
    @Column(name = "detail")
    private String detail;

    @Basic
    @Column(name = "amount")
    private Integer amount;

    @Basic
    @Column(name = "frozen")
    private Integer frozen;

    @Basic
    @Column(name = "rate_num")
    private Integer rateNum;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "specifications", joinColumns = @JoinColumn(name = "product_id"))
    private Set<Specifications> specifications;

    public ProductVO toVO(){
        ProductVO vo = new ProductVO();
        vo.setId(this.id);
        vo.setProductName(this.productName);
        vo.setPrice(this.price);
        vo.setRate(this.rate);
        vo.setDescription(this.description);
        vo.setCover(this.cover);
        vo.setDetail(this.detail);
        vo.setAmount(this.amount);
        vo.setFrozen(this.frozen);
        vo.setSpecifications(this.specifications);
        return vo;
    }
}

