package com.ecom.Shopping_Cart.Controller;


import com.ecom.Shopping_Cart.model.Cart;
import com.ecom.Shopping_Cart.model.Category;
import com.ecom.Shopping_Cart.model.UserDtls;
import com.ecom.Shopping_Cart.service.CartService;
import com.ecom.Shopping_Cart.service.CategoryService;
import com.ecom.Shopping_Cart.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private  CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;


    @GetMapping("/")
    public String home(){
        return "user/home";
    }
    @ModelAttribute
    public void getUserDetails(Principal p, Model model){
        if(p!=null){
            String userName=p.getName();
            UserDtls user=userService.getUserByEmail(userName);
            model.addAttribute("user",user);
        }
        List<Category> categories = categoryService.getAllActiveCategory();
        model.addAttribute("categories", categories);
    }


    @GetMapping("/addCart")
    public String addToCart(@RequestParam int pid, @RequestParam int uid, HttpSession session){
        Cart cart=cartService.saveCart(pid,uid);
        if (cart==null) {
            session.setAttribute("errorMsg", "Something went wrong");
        }else {
            session.setAttribute("succMsg", "Product added to cart");
        }

        return "redirect:/product/"+pid;
    }
}
