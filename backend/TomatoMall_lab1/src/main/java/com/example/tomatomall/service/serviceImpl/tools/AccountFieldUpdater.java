package com.example.tomatomall.service.serviceImpl.tools;

import com.example.tomatomall.po.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

//更新用户信息
@Service
public class AccountFieldUpdater {
    private final Map<String, BiConsumer<Account, Object>> fieldUpdaters = new HashMap<>();

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountFieldUpdater(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        fieldUpdaters.put("password", (account, value) -> {
            String newPassword = passwordEncoder.encode((String) value);
            account.setPassword(newPassword);
        });
        fieldUpdaters.put("userName", (account, value) ->
                account.setUserName((String) value));
        fieldUpdaters.put("address", (account, value) ->
                account.setAddress((String) value));
        fieldUpdaters.put("avatar", (account, value) ->
                account.setAvatar((String) value));
    }

    public void updateField(Account account, String field, Object value) {
        BiConsumer<Account, Object> updater = fieldUpdaters.get(field);
        if (updater != null && value != null) {
            updater.accept(account, value);
        }
    }
}