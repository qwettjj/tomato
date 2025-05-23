package com.example.tomatomall.service;

import com.example.tomatomall.vo.AccountVO;

public interface AccountService {
    Boolean register(AccountVO accountVO);

    String login(String phone, String password);

    AccountVO getAccount();

    Boolean updateAccount(AccountVO accountVO);

    AccountVO getAccountById(Integer id);
}
