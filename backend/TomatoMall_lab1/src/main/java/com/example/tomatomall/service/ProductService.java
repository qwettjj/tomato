<<<<<<< HEAD
package com.example.tomatomall.service;

import com.example.tomatomall.vo.ProductVO;

import java.util.List;

public interface ProductService {
    Boolean addProduct(ProductVO productVO);

    Boolean updateProduct(ProductVO productVO);

    Boolean deleteProduct(Integer id);

    List<ProductVO> getAllProducts();

    ProductVO getProduct(Integer productId);

}
=======
package com.example.tomatomall.service;

import com.example.tomatomall.vo.ProductVO;

import java.util.List;

public interface ProductService {
    Boolean addProduct(ProductVO productVO);

    Boolean updateProduct(ProductVO productVO);

    Boolean deleteProduct(Integer id);

    List<ProductVO> getAllProducts();

    ProductVO getProduct(Integer productId);

}
>>>>>>> 431dbecd26ca9ceb77461c91897a01de963014ae
