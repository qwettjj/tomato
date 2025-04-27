package com.example.tomatomall.controller;

import com.example.tomatomall.po.CartItem;
import com.example.tomatomall.repository.CartItemRepository;
import com.example.tomatomall.service.CartItemService;
import com.example.tomatomall.vo.CartItemVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/cart")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    CartItemRepository cartItemRepository;

    @PostMapping("/add")
    public Response<Boolean> addCartItem(@RequestParam Integer productId, @RequestParam Integer quantity) {
        return Response.buildSuccess(cartItemService.addCartItem(productId, quantity));
    }

    @DeleteMapping("/remove")
    public Response<Boolean> removeCartItem(@RequestParam Integer productId) throws Exception {
        return Response.buildSuccess(cartItemService.removeCartItem(productId));
    }

    @DeleteMapping("/removeById")
    public Response<Boolean> removeCartItemById(@RequestParam Integer cartItemId){
        if(cartItemRepository.findById(cartItemId).isPresent()){
            cartItemRepository.deleteById(cartItemId);
            return Response.buildSuccess(true);
        }
        return Response.buildFailure("没有找到购物车物品","404");
    }

    @PatchMapping("/update")
    public Response<Boolean> updateCartItem(@RequestParam Integer productId, @RequestParam Integer newQuantity) throws Exception {
        return Response.buildSuccess(cartItemService.updateCartItem(productId, newQuantity));
    }

    @GetMapping
    public Response<List<CartItem>> getCartItems() {
        return Response.buildSuccess(cartItemService.getCartItem());
    }

    @GetMapping("/{id}")
    public Response<CartItemVO> getCartItem(@PathVariable Integer id) {
        if(!cartItemRepository.existsById(id)) {
            return Response.buildFailure("购物车物品不存在","404");
        }
        else return Response.buildSuccess(cartItemService.getOneCartItem(id));
    }
}
