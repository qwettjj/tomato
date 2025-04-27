package com.example.tomatomall.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.tomatomall.po.CartItem;


@Getter
@Setter
@NoArgsConstructor
public class CartItemVO {
    private int cartItemId;

    private int userId;

    private int productId;

    private int quantity;

    public CartItem toPO()
    {
        CartItem po = new CartItem();
        po.setCartItemId(cartItemId);
        po.setUserId(userId);
        po.setProductId(productId);
        po.setQuantity(quantity);
        return po;
    }

}
