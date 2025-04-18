
package com.example.tomatomall.vo;

import com.example.tomatomall.enums.RoleEnum;
import com.example.tomatomall.po.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class AccountVO {
    private Integer id;

    private String userName;

    private String password;

    private String phone;

    private String email;

    private String address;

    private RoleEnum role;

    private Date createTime;

    private String avatar;

    public Account toPO(){
        Account account = new Account();
        account.setId(this.id);
        account.setUserName(this.userName);
        account.setPassword(this.password);
        account.setPhone(this.phone);
        account.setEmail(this.email);
        account.setAddress(this.address);
        account.setRole(this.role);
        account.setAvatar(this.avatar);
        account.setCreateTime(this.createTime);
        return account;
    }
}

