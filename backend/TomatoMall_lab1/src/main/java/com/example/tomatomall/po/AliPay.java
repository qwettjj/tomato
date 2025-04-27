package com.example.tomatomall.po;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AliPay {

    /**
     * 商户订单号 (对应支付宝 out_trade_no)
     */
    @JSONField(name = "out_trade_no")
    private String outTradeNo;

    /**
     * 订单总金额（单位：元，字符串类型避免精度问题）
     * 对应支付宝 total_amount
     */
    @JSONField(name = "total_amount")
    private String totalAmount;

    /**
     * 订单标题
     * 对应支付宝 subject
     */
    private String subject;

    /**
     * 支付宝交易号（回调时使用）
     * 对应支付宝 trade_no
     */
    @JSONField(name = "trade_no")
    private String tradeNo;

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }
}