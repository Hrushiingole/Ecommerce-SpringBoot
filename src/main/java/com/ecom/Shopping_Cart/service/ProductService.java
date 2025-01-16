package com.ecom.Shopping_Cart.service;


import com.ecom.Shopping_Cart.model.Category;
import com.ecom.Shopping_Cart.model.Product;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    public Product saveProduct(Product product);

    public List<Product> getAllProduct();


    public Boolean deleteProduct(int id);

    public Product getProductById(int id);

    public Product updateProduct(Product product, @RequestParam("file") MultipartFile image);

    public List<Product> getAllActiveProducts(String category);

}
