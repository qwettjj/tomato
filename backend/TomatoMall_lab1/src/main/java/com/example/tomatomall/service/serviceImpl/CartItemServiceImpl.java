package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.CartItem;
import com.example.tomatomall.repository.CartItemRepository;
import com.example.tomatomall.service.CartItemService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.CartItemVO;
import com.example.tomatomall.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    SecurityUtil securityUtil;

    @Override
    public Boolean addCartItem(Integer productId, Integer quantity) {
        Integer userId = securityUtil.getCurrentAccount().getId();
        CartItem cartItem = cartItemRepository.findCartItemByUserIdAndProductId(userId, productId);
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
        }
        else{
            cartItem.setQuantity(cartItem.getQuantity()+quantity);
        }
        cartItemRepository.save(cartItem);
        return true;
    }

    @Override
    public Boolean removeCartItem(Integer productId){
        Integer userId = securityUtil.getCurrentAccount().getId();
        CartItem cartItem = cartItemRepository.findCartItemByUserIdAndProductId(userId, productId);
        if (cartItem != null) {
            cartItemRepository.delete(cartItem);
            return true;
        }
        else {
            throw TomatoMallException.cartItemNotFound();
        }
    }

    @Override
    @Transactional
    public Boolean updateCartItem(Integer productId, Integer newQuantity){
        Integer userId = securityUtil.getCurrentAccount().getId();
        CartItem cartItem = cartItemRepository.findCartItemByUserIdAndProductId(userId, productId);
        if (cartItem != null) {
            cartItem.setQuantity(newQuantity);
            return true;
        }
        else {
            throw TomatoMallException.cartItemNotFound();
        }
    }

    @Override
    public List<CartItem> getCartItem() {
        Account account = securityUtil.getCurrentAccount();
        return cartItemRepository.findCartItemsByUserId(account.getId());
    }

    @Override
    public CartItemVO getOneCartItem(Integer Id){
        return cartItemRepository.findById(Id).get().toVO();
    }
}
