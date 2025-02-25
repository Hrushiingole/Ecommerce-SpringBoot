package com.ecom.Shopping_Cart.service;


import com.ecom.Shopping_Cart.model.Category;
import com.ecom.Shopping_Cart.model.Product;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public List<Product> searchProduct(String ch);

    public Page<Product> searchProductPagination(Integer pageNo,Integer pageSize,String ch);

    public Page<Product> getAllProductPagination(Integer pageNo,Integer pageSize);

    public Page<Product> getAllActiveProductPagination(Integer pageNo,Integer pageSize,String category);


}
