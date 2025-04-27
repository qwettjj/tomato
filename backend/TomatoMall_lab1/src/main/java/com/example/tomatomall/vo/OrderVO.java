package com.example.tomatomall.vo;

import com.example.tomatomall.enums.OrderStatuEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.tomatomall.po.Order;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderVO {

    private Integer OrderId;

    private Integer UserId;

    private Integer TotalAmount;

    private String PaymentMethod;

    private OrderStatuEnum status;

    private LocalDateTime createTime;

    public Order toPo() {
        Order po = new Order();
        po.setOrderId(OrderId);
        po.setUserId(UserId);
        po.setTotalAmount(TotalAmount);
        po.setPaymentMethod(PaymentMethod);
        po.setStatus(status);
        po.setCreateTime(createTime);
        return po;
    }

}
