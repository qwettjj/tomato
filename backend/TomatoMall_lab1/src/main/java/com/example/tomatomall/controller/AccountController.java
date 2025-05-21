package com.example.tomatomall.controller;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.vo.AccountVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
        try{
            return Response.buildSuccess(accountService.getAccount());
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }

    }

    /**
     * 创建新的用户
     */
    @PostMapping("/create")
    public Response<Boolean> createUser(@RequestBody AccountVO accountVO) {
        try{
            return Response.buildSuccess(accountService.register(accountVO));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }

    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Response<Boolean> updateUser(@RequestBody AccountVO accountVO) {
        try{
            return Response.buildSuccess(accountService.updateAccount(accountVO));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Response<String> login(@RequestParam("phone") String phone, @RequestParam("password") String password) {
        try{
            return Response.buildSuccess(accountService.login(phone, password));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
    }
}
