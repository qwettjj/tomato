package com.example.tomatomall.vo;

import com.example.tomatomall.po.History;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class HistoryVO {
    private Integer historyId;

    private Integer productId;

    private Integer quantity;

    private Integer orderId;

    private LocalDateTime createTime;

    public History toPo() {
        History history = new History();
        history.setHistoryId(historyId);
        history.setProductId(productId);
        history.setQuantity(quantity);
        history.setOrderId(orderId);
        history.setCreateTime(createTime);
        return history;
    }
}
