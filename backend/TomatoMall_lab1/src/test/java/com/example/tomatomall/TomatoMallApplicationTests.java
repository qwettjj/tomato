package com.example.tomatomall;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.vo.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.tomatomall.vo.Response;


@SpringBootTest
class TomatoMallApplicationTests {

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
        Account user=new Account();
        user.setId(1);
        user.setPassword(passwordEncoder.encode("123456"));
        System.out.println(Response.buildSuccess(tokenUtil.getToken(user)));
    }

}
