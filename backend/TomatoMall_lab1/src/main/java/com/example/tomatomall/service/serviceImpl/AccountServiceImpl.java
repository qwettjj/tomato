package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.service.serviceImpl.tools.AccountFieldUpdater;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.vo.AccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    SecurityUtil securityUtil;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    AccountFieldUpdater accountFieldUpdater;

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
    public String login(String phone, String password) {
        Account account = accountRepository.findByPhone(phone);
        if (account == null) {
            throw TomatoMallException.accountNotFound();
        }
        Boolean flag = passwordEncoder.matches(password, account.getPassword());
        if (!flag) {
            throw TomatoMallException.phoneOrPasswordError();
        }

        return tokenUtil.getToken(account);
    }

    @Override
    public Boolean updateAccount(AccountVO accountVO) {
        Account account = accountRepository.findByPhone(accountVO.getPhone());
        if (account == null) throw TomatoMallException.accountNotFound();

        // 使用表查询更新字段
        accountFieldUpdater.updateField(account, "password", accountVO.getPassword());
        accountFieldUpdater.updateField(account, "userName", accountVO.getUserName());
        accountFieldUpdater.updateField(account, "address", accountVO.getAddress());
        accountFieldUpdater.updateField(account, "avatar", accountVO.getAvatar());

        accountRepository.save(account);
        return true;
    }

    @Override
    public AccountVO getAccount() {
        Account account = securityUtil.getCurrentAccount();
        return account.toVO();
    }

    @Override
    public AccountVO getAccountById(Integer id){
        Account account = accountRepository.findById(id).isPresent() ? accountRepository.findById(id).get() : null;
        if(account == null) {
            throw TomatoMallException.accountNotFound();
        }
        return account.toVO();
    }
}
