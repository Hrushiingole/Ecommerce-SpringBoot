package com.ecom.Shopping_Cart.service;

import com.ecom.Shopping_Cart.model.Cart;

import java.util.List;

public interface CartService {
    public Cart saveCart(Integer productId,Integer userId);

    public List<Cart> getCartByUser(Integer userId);

    public Integer getCountCart(Integer userId);

    public void updateCartQuantity(String sy, Integer id);
}
