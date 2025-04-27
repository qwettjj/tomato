package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.tomatomall.vo.CartItemVO;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class CartItem {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "cart_item_id")
    private Integer cartItemId;


    @JoinColumn(name = "user_id")
    private Integer userId;


    @JoinColumn(name = "product_id")
    private Integer productId;

    @Basic
    @Column(name = "quantity")
    private Integer quantity;



    public CartItemVO toVO() {
        CartItemVO VO = new CartItemVO();
        VO.setCartItemId(cartItemId);
        VO.setUserId(userId);
        VO.setProductId(productId);
        VO.setQuantity(quantity);
        return VO;
    }
}
