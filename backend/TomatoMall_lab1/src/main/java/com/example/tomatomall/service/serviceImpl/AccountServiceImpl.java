package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.AccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    SecurityUtil securityUtil;

    @Override
    public Boolean register(AccountVO accountVO) {
        Account account = accountRepository.findByPhone(accountVO.getPhone());
        if (account != null) {
            throw TomatoMallException.phoneAlreadyExists();
        }
        Account newAccount = accountVO.toPO();
        String newPassword = passwordEncoder.encode(newAccount.getPassword());
        newAccount.setPassword(newPassword);
        newAccount.setCreateTime(new Date());
        accountRepository.save(newAccount);
        return true;
    }

    @Override
    public Boolean login(String phone, String password) {
        Account account = accountRepository.findByPhone(phone);
        if (account == null) {
            throw TomatoMallException.accountNotFound();
        }
        Boolean flag = passwordEncoder.matches(password, account.getPassword());
        if (!flag) {
            throw TomatoMallException.phoneOrPasswordError();
        }
        return flag;
    }

    @Override
    public Boolean updateAccount(AccountVO accountVO) {
        Account account = securityUtil.getCurrentAccount();
        if(accountVO.getPassword()!=null) {
            String newPassword = passwordEncoder.encode(accountVO.getPassword());
            account.setPassword(newPassword);
        }
        if(accountVO.getUserName()!=null) {
            account.setUserName(accountVO.getUserName());
        }
        if(accountVO.getAddress()!=null) {
            account.setAdress(accountVO.getAddress());
        }
        if(accountVO.getAvator()!=null) {
            account.setAvator(accountVO.getAvator());
        }
        accountRepository.save(account);
        return true;
    }

    @Override
    public AccountVO getAccount() {
        Account account = securityUtil.getCurrentAccount();
        AccountVO accountVO = account.toVO();
        return accountVO;
    }
}
