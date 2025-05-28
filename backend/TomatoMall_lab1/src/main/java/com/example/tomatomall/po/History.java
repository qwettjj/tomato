package com.example.tomatomall.po;

import com.example.tomatomall.vo.HistoryVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Integer historyId;

    @Basic
    @Column(name = "user_id")
    private Integer userId;

    @Basic
    @Column(name = "product_id")
    private Integer productId;

    @Basic
    @Column(name = "quantity")
    private Integer quantity;

    @Basic
    @Column(name = "order_id")
    private Integer orderId;

    @Basic
    @Column(name = "create_time")
    private LocalDateTime createTime;

    public HistoryVO toVo(){
        HistoryVO vo = new HistoryVO();
        vo.setCreateTime(createTime);
        vo.setQuantity(quantity);
        vo.setProductId(productId);
        vo.setOrderId(orderId);
        return vo;
    }
}
