package com.ecom.Shopping_Cart.service;

import com.ecom.Shopping_Cart.model.Product;
import com.ecom.Shopping_Cart.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product saveProduct(Product product) {
        productRepository.save(product);
        return product;
    }
}
