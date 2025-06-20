package com.example.tomatomall.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.tomatomall.config.AliPayConfig;
import com.example.tomatomall.enums.OrderStatuEnum;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/alipay")
public class AliPayController {

    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    private static final String FORMAT = "JSON";

    private static final String CHARSET = "UTF-8";

    private static final String SIGN_TYPE = "RSA2";

    @Resource
    private AliPayConfig aliPayConfig;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/pay")
    public void pay(Integer orderId, HttpServletResponse httpResponse) throws Exception {
        Order order = orderRepository.findById(orderId).get();
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL,aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(),FORMAT,CHARSET, aliPayConfig.getAliPayPublicKey(),SIGN_TYPE);

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", order.getOrderId());
        bizContent.put("total_amount", order.getTotalAmount());
        bizContent.put("subject",order.getPaymentMethod());
        bizContent.put("product_code","FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        request.setReturnUrl("https://localhost:8080/home");
        String form ="";
        try{
            form = alipayClient.pageExecute(request).getBody();
        } catch(AlipayApiException e){
            e.printStackTrace();
        }

        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

    @PostMapping("/notify")
    public void payNotify(HttpServletRequest request) throws Exception {
        if(request.getParameter("trade_status").equals("TRADE_FINISHED") || request.getParameter("trade_status").equals("TRADE_SUCCESS")){
            System.out.println("==============支付宝异步回调=================");

            Map<String,String> params = new HashMap<>();
            Map<String,String[]> requestParams = request.getParameterMap();
            for(String key : requestParams.keySet()){
                params.put(key, request.getParameter(key));
            }

            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content,sign,aliPayConfig.getAliPayPublicKey(), "UTF-8");
            if(checkSignature){
                String tradeNo = params.get("out_trade_no");
                Order order = orderRepository.findById(Integer.parseInt(tradeNo)).get();
                order.setStatus(OrderStatuEnum.SUCCESS);
                orderRepository.save(order);
            }
        }
    }
}
