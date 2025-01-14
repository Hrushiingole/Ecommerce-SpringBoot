package com.ecom.Shopping_Cart.Controller;


import com.ecom.Shopping_Cart.model.Category;
import com.ecom.Shopping_Cart.model.Product;
import com.ecom.Shopping_Cart.repository.ProductRepository;
import com.ecom.Shopping_Cart.service.CategoryService;
import com.ecom.Shopping_Cart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String index() {
        return "index";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/products")
    public String products(Model model) {

        List<Category> categories = categoryService.getAllCategory();
        model.addAttribute("categories", categories);

        List<Product> products = productService.getAllProduct();
        model.addAttribute("products", products);

        return "product";
    }


    @GetMapping("/product")
    public String product(){
        return "view_product";
    }
}
