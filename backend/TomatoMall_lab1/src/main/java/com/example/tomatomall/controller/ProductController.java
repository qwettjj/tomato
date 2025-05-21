
package com.example.tomatomall.controller;

import com.example.tomatomall.exception.TomatoMallException;
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
        try{
            return Response.buildSuccess(productService.addProduct(productVO));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }

    }

    @PutMapping("/update")
    public Response<Boolean> updateProduct(@RequestBody ProductVO productVO) {
        try{
            return Response.buildSuccess(productService.updateProduct(productVO));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }

    }

    @DeleteMapping("/{id}")
    public Response<Boolean> deleteProduct(@PathVariable Integer id) {
        try{
            return Response.buildSuccess(productService.deleteProduct(id));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
    }

    @GetMapping("/all")
    public Response<List<ProductVO>> getAllProducts() {
        try{
            return Response.buildSuccess(productService.getAllProducts());
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }

    }

    @GetMapping("/{id}")
    public Response<ProductVO> getProduct(@PathVariable Integer id) {
        try{
            return Response.buildSuccess(productService.getProduct(id));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
        
    }
}



