
package com.example.tomatomall.repository;

import com.example.tomatomall.po.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findByProductName(String productName);

    @Query("SELECT p from Product p LEFT JOIN FETCH p.specifications WHERE p.id = :id")
    Optional<Product> findByIdWithSpecifications(@Param("id") Integer id);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.specifications")
    List<Product> findAllWithSpecifications();
    
    @Modifying
    @Query("UPDATE Product p SET " +
            "p.rate = ((p.rate * p.rateNum) + :rate) / (p.rateNum + 1), " +
            "p.rateNum = p.rateNum + 1 " +
            "WHERE p.id = :productId")
    void updateRating(@Param("productId") Integer productId,
                      @Param("rate") Integer rate);

    @Query("SELECT p.rate FROM Product p WHERE p.id = :productId")
    Integer getRating(@Param("productId") Integer productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Integer id);
}

