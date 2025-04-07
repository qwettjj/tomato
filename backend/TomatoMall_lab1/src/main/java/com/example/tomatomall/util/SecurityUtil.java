<<<<<<< HEAD
package com.example.tomatomall.util;

import com.example.tomatomall.po.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class SecurityUtil {

    @Autowired
    HttpServletRequest httpServletRequest;

    public Account getCurrentAccount() { return (Account) httpServletRequest.getSession().getAttribute("currentAccount"); }
}
=======
package com.example.tomatomall.util;

import com.example.tomatomall.po.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class SecurityUtil {

    @Autowired
    HttpServletRequest httpServletRequest;

    public Account getCurrentAccount() { return (Account) httpServletRequest.getSession().getAttribute("currentAccount"); }
}
>>>>>>> 431dbecd26ca9ceb77461c91897a01de963014ae
