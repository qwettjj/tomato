<<<<<<< HEAD
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

    private String avator;

    public Account toPO(){
        Account account = new Account();
        account.setId(this.id);
        account.setUserName(this.userName);
        account.setPassword(this.password);
        account.setPhone(this.phone);
        account.setEmail(this.email);
        account.setAdress(this.address);
        account.setRole(this.role);
        account.setAvator(this.avator);
        account.setCreateTime(this.createTime);
        return account;
    }
}
=======
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

    private String avator;

    public Account toPO(){
        Account account = new Account();
        account.setId(this.id);
        account.setUserName(this.userName);
        account.setPassword(this.password);
        account.setPhone(this.phone);
        account.setEmail(this.email);
        account.setAdress(this.address);
        account.setRole(this.role);
        account.setAvator(this.avator);
        account.setCreateTime(this.createTime);
        return account;
    }
}
>>>>>>> 431dbecd26ca9ceb77461c91897a01de963014ae
