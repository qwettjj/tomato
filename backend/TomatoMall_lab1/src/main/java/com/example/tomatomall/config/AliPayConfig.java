package com.example.tomatomall.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "alipay")
@Getter
@Setter
public class AliPayConfig {
    private String appId;
    private String appPrivateKey;
    private String aliPayPublicKey;
    private String notifyUrl;
}
