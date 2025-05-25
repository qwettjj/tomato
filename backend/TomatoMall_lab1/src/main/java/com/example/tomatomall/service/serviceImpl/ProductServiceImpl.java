
package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;

    @Override
    public Boolean addProduct(ProductVO productVO){
        Product product = productRepository.findByProductName(productVO.getProductName());
        if(product != null){
            throw TomatoMallException.productAlreadyExists();
        }
        Product newProduct = productVO.toPO();
        newProduct.setRateNum(0);
        newProduct.setFrozen(0);
        productRepository.save(newProduct);
        return true;
    }

    @Override
    public Boolean updateProduct(ProductVO productVO){
        Integer id = productVO.getId();
        Product product = productRepository.findById(id).isPresent() ? productRepository.findById(id).get() : null;
        if(product == null){
            throw TomatoMallException.productToChangeNotFound();
        }
        if(productVO.getProductName() != null){
            product.setProductName(productVO.getProductName());
        }
        if(productVO.getPrice() != null){
            product.setPrice(productVO.getPrice());
        }
        if(productVO.getDescription() != null){
            product.setDescription(productVO.getDescription());
        }
        if(productVO.getCover() != null){
            product.setCover(productVO.getCover());
        }
        if(productVO.getDetail() != null){
            product.setDetail(productVO.getDetail());
        }
        if(productVO.getAmount() != null){
            product.setAmount(productVO.getAmount());
        }
        productRepository.save(product);
        return true;
    }

    @Override
    public Boolean deleteProduct(Integer id){
        Product product = productRepository.findById(id).isPresent() ? productRepository.findById(id).get() : null;
        if(product == null){
            throw TomatoMallException.productToChangeNotFound();
        }
        productRepository.delete(product);
        return true;
    }

    @Override
    public List<ProductVO> getAllProducts(){
        return productRepository.findAllWithSpecifications().stream().map(Product::toVO).collect(Collectors.toList());
    }

    @Override
    public ProductVO getProduct(Integer productId) {
        Product product = productRepository.findByIdWithSpecifications(productId).isPresent() ? productRepository.findByIdWithSpecifications(productId).get() : null;
        if(product == null){
            throw TomatoMallException.productNotFound();
        }
        return product.toVO();
    }

    @Override
    public Integer rateProduct(Integer productId,Integer rate){
        Product product = productRepository.findById(productId).isPresent() ? productRepository.findById(productId).get() : null;
        if(product == null){
            throw TomatoMallException.productNotFound();
        }
        else{
            product.setRateNum(product.getRateNum()+1);
            Integer newRate = (product.getRate() + rate) / product.getRateNum();
            product.setRate(newRate);
            productRepository.save(product);
            return newRate;
        }
    }

    @Override
    public Boolean frozenProduct(Integer productId){
        Product product = productRepository.findById(productId).isPresent() ? productRepository.findById(productId).get() : null;
        if(product == null){
            throw TomatoMallException.productNotFound();
        }
        else{
            product.setFrozen(1);
            productRepository.save(product);
            return true;
        }
    }

    @Override
    public Boolean unfrozenProduct(Integer productId){
        Product product = productRepository.findById(productId).isPresent() ? productRepository.findById(productId).get() : null;
        if(product == null){
            throw TomatoMallException.productNotFound();
        }
        else{
            product.setFrozen(0);
            productRepository.save(product);
            return true;
        }
    }
}

