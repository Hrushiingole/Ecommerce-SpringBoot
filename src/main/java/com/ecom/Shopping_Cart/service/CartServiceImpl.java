package com.ecom.Shopping_Cart.service;


import com.ecom.Shopping_Cart.model.Cart;
import com.ecom.Shopping_Cart.model.Product;
import com.ecom.Shopping_Cart.model.UserDtls;
import com.ecom.Shopping_Cart.repository.CartRepository;
import com.ecom.Shopping_Cart.repository.ProductRepository;
import com.ecom.Shopping_Cart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {


    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    @Override
    public Cart saveCart(Integer productId, Integer userId) {
        Product product = productRepository.findById(productId).orElse(null);
        UserDtls user = userRepository.findById(userId).orElse(null);

       Cart cartStatus=cartRepository.findByProductIdAndUserId(productId,userId);
        Cart cart=null;
        if (cartStatus==null) {
            cart=new Cart();
            cart.setProduct(product);
            cart.setUser(user);

            cart.setQuantity(1);
            cart.setTotalPrice(1*product.getDiscountPrice());

        }else{
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());

        }

        Cart saveCart=cartRepository.save(cart);

        return saveCart;
    }

    @Override
    public List<Cart> getCartByUser(Integer userId) {

        List<Cart> carts=cartRepository.findByUserId(userId);

        Double totalOrderPrice=0.0;

        List<Cart> updatedCart=new ArrayList<>();
        for(Cart cart:carts){
            Double totalPrice=cart.getProduct().getDiscountPrice()*cart.getQuantity();
            cart.setTotalPrice(totalPrice);
            totalOrderPrice+=totalPrice;
            cart.setTotalOrderPrice(totalOrderPrice);
            updatedCart.add(cart);
        }
        return updatedCart;
    }

    @Override
    public Integer getCountCart(Integer userId) {
        Integer count=cartRepository.countByUserId(userId);
        return count;
    }

    @Override
    public void updateCartQuantity(String sy, Integer id) {
        Cart cart=cartRepository.findById(id).get();
        Integer cartQuantity=cart.getQuantity();
        int updatedQuantity;
        if (sy.equals("de")){

            updatedQuantity=cartQuantity-1;
            if(updatedQuantity<=0){
                cartRepository.delete(cart);

            }
            else{
                cart.setQuantity(updatedQuantity);
                cartRepository.save(cart);
            }
        }else{
            updatedQuantity=cartQuantity+1;
            cart.setQuantity(updatedQuantity);
            cartRepository.save(cart);
        }



    }
}
