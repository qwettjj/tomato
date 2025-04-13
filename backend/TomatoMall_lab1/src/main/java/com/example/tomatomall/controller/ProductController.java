
package com.example.tomatomall.controller;

import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    ProductService productService;

    @PostMapping("/add")
    public Response<Boolean> addProduct(@RequestBody ProductVO productVO) {
        return Response.buildSuccess(productService.addProduct(productVO));
    }

    @PutMapping("/update")
    public Response<Boolean> updateProduct(@RequestBody ProductVO productVO) {
        return Response.buildSuccess(productService.updateProduct(productVO));
    }

    @DeleteMapping("/{id}")
    public Response<Boolean> deleteProduct(@PathVariable Integer id) {
        return Response.buildSuccess(productService.deleteProduct(id));
    }

    @GetMapping("/all")
    public Response<List<ProductVO>> getAllProducts() {
        return Response.buildSuccess(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public Response<ProductVO> getProduct(@PathVariable Integer id) {
        return Response.buildSuccess(productService.getProduct(id));
    }
}



