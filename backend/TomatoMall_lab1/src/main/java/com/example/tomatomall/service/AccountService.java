package com.example.tomatomall.service;

import com.example.tomatomall.vo.AccountVO;

public interface AccountService {
    Boolean register(AccountVO accountVO);

    Boolean login(String phone, String password);

    AccountVO getAccount();

    Boolean updateAccount(AccountVO accountVO);
}
