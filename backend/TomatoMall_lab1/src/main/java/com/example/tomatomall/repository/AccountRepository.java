<<<<<<< HEAD
package com.example.tomatomall.repository;

import com.example.tomatomall.po.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findByPhone(String phone);
}
=======
package com.example.tomatomall.repository;

import com.example.tomatomall.po.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findByPhone(String phone);
}
>>>>>>> 431dbecd26ca9ceb77461c91897a01de963014ae
