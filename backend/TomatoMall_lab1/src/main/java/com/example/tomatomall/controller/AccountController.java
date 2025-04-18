package com.example.tomatomall.controller;

import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.vo.AccountVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    AccountService accountService;

    /**
     * 获取用户详情
     */
    @GetMapping("/info")
    public Response<AccountVO> getUser() {
        return Response.buildSuccess(accountService.getAccount());
    }

    /**
     * 创建新的用户
     */
    @PostMapping("/create")
    public Response<Boolean> createUser(@RequestBody AccountVO accountVO) {
        return Response.buildSuccess(accountService.register(accountVO));
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Response<Boolean> updateUser(@RequestBody AccountVO accountVO) {
        return Response.buildSuccess(accountService.updateAccount(accountVO));
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Response<String> login(@RequestParam("phone") String phone, @RequestParam("password") String password) {
        return Response.buildSuccess(accountService.login(phone, password));
    }
}
