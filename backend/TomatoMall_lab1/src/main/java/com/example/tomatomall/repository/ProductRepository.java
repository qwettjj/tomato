
package com.example.tomatomall.repository;

import com.example.tomatomall.po.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findByProductName(String productName);

    @Query("SELECT p from Product p LEFT JOIN FETCH p.specifications WHERE p.id = :id")
    Optional<Product> findByIdWithSpecifications(@Param("id") Integer id);
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.specifications")
    List<Product> findAllWithSpecifications();

}

