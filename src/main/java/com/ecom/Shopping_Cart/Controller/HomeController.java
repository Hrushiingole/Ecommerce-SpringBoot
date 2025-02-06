package com.ecom.Shopping_Cart.Controller;


import com.ecom.Shopping_Cart.model.Category;
import com.ecom.Shopping_Cart.model.Product;
import com.ecom.Shopping_Cart.model.UserDtls;
import com.ecom.Shopping_Cart.service.CartService;
import com.ecom.Shopping_Cart.service.CategoryService;
import com.ecom.Shopping_Cart.service.ProductService;
import com.ecom.Shopping_Cart.service.UserService;
import com.ecom.Shopping_Cart.utils.CommonUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private CartService cartService;


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @ModelAttribute
    public void getUserDetails(Principal p,Model model){
        if(p!=null){
            String email=p.getName();
            UserDtls user=userService.getUserByEmail(email);
            model.addAttribute("user",user);
            Integer count=cartService.getCountCart(user.getId());
            model.addAttribute("countCart",count);

        }else{
            model.addAttribute("user",null);
        }
        List<Category> categories = categoryService.getAllActiveCategory();
        model.addAttribute("categories", categories);
    }




    @GetMapping("/")
    public String index() {
        return "index";
    }
    @GetMapping("/signin")
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

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
            throws IOException {

        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setProfileImage(imageName);
        UserDtls saveUser = userService.saveUser(user);

        if (!ObjectUtils.isEmpty(saveUser)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                        + file.getOriginalFilename());

                System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "Register successfully");
        } else {
            session.setAttribute("errorMsg", "something wrong on server");
        }

        return "redirect:/register";
    }

    // forget password code
    @GetMapping("/forgetPassword")
    public String showForgetPassword() {
        return "forget_password";
    }
    @PostMapping("/forgetPassword")
    public String forgetPassword(@RequestParam String email, HttpSession session, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
        UserDtls user=userService.getUserByEmail(email);
        if (user!=null) {

            String resetToken=UUID.randomUUID().toString();
            userService.updateUserResetToken(email,resetToken);

            //generate URL : http://localhost:8082/resetPassword?token=resetToken

            String Url=commonUtil.generateUrl(request)+"/resetPassword?token="+resetToken;

            Boolean sendMail=commonUtil.sendEmail(Url,email);
            if(sendMail==true){
                session.setAttribute("succMsg","Please Check your Email to reset your password");

            }
            else {
                session.setAttribute("errorMsg","Something went wrong");
            }
        }
        else{
            session.setAttribute("errorMsg","User not found");
        }
            return "redirect:/forgetPassword";
    }

    @GetMapping("/resetPassword")
    public String showResetPassword(@RequestParam String token,HttpSession session,Model model){
        UserDtls user=userService.getUserByToken(token);
        if (user==null){
            model.addAttribute("message","Invalid Token");
           return "message";

        }
        model.addAttribute("token",token);



        return "reset_password";
    }
    @PostMapping("/resetPassword")
    public String showResetPassword(@RequestParam String token,@RequestParam String password, HttpSession session,Model model){
        UserDtls user=userService.getUserByToken(token);
        if (user==null){
            model.addAttribute("errorMsg","Invalid Token");
            return "message";

        }
        else{
            user.setPassword(passwordEncoder.encode(password));
            user.setResetToken(null);
            userService.updateUser(user);
            session.setAttribute("succMsg","Password reset successfully");
            model.addAttribute("message","Password reset successfully");
            return "message";
        }



    }






}
