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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String products(Model model, @RequestParam(value = "category",defaultValue ="" ) String category) {
//        System.out.println("category="+category);
        List<Category> categories = categoryService.getAllActiveCategory();
        model.addAttribute("categories", categories);

        List<Product> products = productService.getAllActiveProducts(category);
        model.addAttribute("products", products);
        model.addAttribute("paramValue",category);

        return "product";
    }


    @GetMapping("/product/{id}")
    public String product(@PathVariable int id,Model model){
        Product product=productService.getProductById(id);
        model.addAttribute("product",product);
        return "view_product";
    }
}
