package com.example.tomatomall.po;

import com.example.tomatomall.enums.RoleEnum;
import com.example.tomatomall.vo.AccountVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Account {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "user_name")
    private String userName;

    @Basic
    @Column(name = "phone")
    private String phone;

    @Basic
    @Column(name = "password")
    private String password;

    @Basic
    @Column(name = "create_time")
    private Date createTime;

    @Basic
    @Column(name = "address")
    private String address;

    @Basic
    @Column(name = "avatar")
    private String avatar;

    @Basic
    @Column(name = "email")
    private String email;

    @Basic
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    public AccountVO toVO() {
        AccountVO accountVO = new AccountVO();
        accountVO.setId(this.id);
        accountVO.setUserName(this.userName);
        accountVO.setPhone(this.phone);
        accountVO.setPassword(this.password);
        accountVO.setCreateTime(this.createTime);
        accountVO.setAddress(this.address);
        accountVO.setAvatar(this.avatar);
        accountVO.setEmail(this.email);
        accountVO.setRole(this.role);
        return accountVO;
    }
}
