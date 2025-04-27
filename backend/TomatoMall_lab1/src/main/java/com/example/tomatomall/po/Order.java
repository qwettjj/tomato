package com.example.tomatomall.po;

import com.example.tomatomall.enums.OrderStatuEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.tomatomall.vo.OrderVO;
import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name =  "`order`")
public class Order {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "order_id")
    private Integer orderId;

    @JoinColumn(name = "user_id")
    private Integer userId;

    @Basic
    @Column(name = "total_amount")
    private Integer totalAmount;

    @Basic
    @Column(name = "payment_method")
    private String paymentMethod;

    @Basic
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatuEnum status;

    @Basic
    @Column(name = "create_time")
    private LocalDateTime createTime;

    public OrderVO toVO() {
        OrderVO vo = new OrderVO();
        vo.setOrderId(orderId);
        vo.setUserId(userId);
        vo.setPaymentMethod(paymentMethod);
        vo.setStatus(status);
        vo.setCreateTime(createTime);
        return vo;
    }
}
