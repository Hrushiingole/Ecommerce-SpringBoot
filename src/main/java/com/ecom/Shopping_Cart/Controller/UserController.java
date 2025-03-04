package com.ecom.Shopping_Cart.Controller;


import com.ecom.Shopping_Cart.model.*;
import com.ecom.Shopping_Cart.service.*;
import com.ecom.Shopping_Cart.service.ServiceImpl.ProductOrderServiceImpl;
import com.ecom.Shopping_Cart.utils.CommonUtil;
import com.ecom.Shopping_Cart.utils.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
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

    @Autowired
    private ProductOrderServiceImpl productOrderService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;


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
            Integer count=cartService.getCountCart(user.getId());
            model.addAttribute("countCart",count);
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

    @GetMapping("/cart")
    public String loadCartPage(Principal p,Model m){

        UserDtls userDtls=getLoggedInUserDetails(p);
       List<Cart> carts= cartService.getCartByUser(userDtls.getId());
       m.addAttribute("carts",carts);
       if(carts.size()>0){
           m.addAttribute("totalOrderPrice",carts.get(carts.size()-1).getTotalOrderPrice());
       }
       else{
           m.addAttribute("totalOrderPrice",0.0);
       }

        return "/user/cart";
    }

    private UserDtls getLoggedInUserDetails(Principal p) {
        String email=p.getName();
        UserDtls user=userService.getUserByEmail(email);
        return user;
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy,@RequestParam Integer cid){
        cartService.updateCartQuantity(sy,cid);
        return "redirect:/user/cart";
    }

    @GetMapping("/order")
    public String orderPage(Principal p,Model m){
        UserDtls userDtls=getLoggedInUserDetails(p);
        List<Cart> carts= cartService.getCartByUser(userDtls.getId());
        m.addAttribute("carts",carts);
        if(carts.size()>0){
            m.addAttribute("orderPrice",carts.get(carts.size()-1).getTotalOrderPrice());
            m.addAttribute("totalOrderPrice",carts.get(carts.size()-1).getTotalOrderPrice()+250+100);
        }
        else{
            m.addAttribute("totalOrderPrice",0.0);
        }
        return "/user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest orderRequest,Principal p){
//        System.out.println(orderRequest);
        UserDtls userDtls=getLoggedInUserDetails(p);
        productOrderService.saveProductOrder(userDtls.getId(),orderRequest);
        return "redirect:/user/success";
    }
    @GetMapping("/success")
    public String loadSuccess(){
        return  "/user/success";
    }

    @GetMapping("/user-orders")
    public String myOrders(Model m,Principal p){
        List<ProductOrder> productOrders=productOrderService.getOrderByUser(getLoggedInUserDetails(p).getId());
        m.addAttribute("orderList",productOrders);
        return "/user/my_order";
    }

    @GetMapping("/update-status")
    public String updateOrderStatus(@RequestParam Integer id,@RequestParam Integer st,HttpSession session) throws MessagingException, UnsupportedEncodingException {
        OrderStatus[] values = OrderStatus.values();
        String status =null;
        for(OrderStatus orderStatus:values){
            if(orderStatus.getId().equals(st)){
                status=orderStatus.getName();
            }
        }
        ProductOrder updateOrder =productOrderService.updateOrderStatus(id,status);
        try{
            commonUtil.sendMailForProductOrder(updateOrder,status);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(updateOrder!=null){
            session.setAttribute("succMsg","Order status updated");
        }else{
            session.setAttribute("errorMsg","Something went wrong");
        }

        return "redirect:/user/user-orders";


    }

    //profile section
    @GetMapping("/profile")
    public String profile(Model m,Principal p){
        UserDtls userDtls=getLoggedInUserDetails(p);
        m.addAttribute("user",userDtls);
        return "/user/profile";
    }


    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img,HttpSession session){
        UserDtls updatedUserProfile=userService.updateUserProfile(user,img);
        if(updatedUserProfile==null){
                session.setAttribute("errorMsg","Something went wrong");
        }
        else{
            session.setAttribute("succMsg","Profile updated");
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public  String changePassword(@RequestParam String newPassword,@RequestParam String currentPassword,Principal p,HttpSession session){
        UserDtls userDtls=getLoggedInUserDetails(p);
       if(passwordEncoder.matches(currentPassword,userDtls.getPassword())){
           userDtls.setPassword(passwordEncoder.encode(newPassword));
           userService.updateUser(userDtls);
           session.setAttribute("succMsg","Password changed successfully");
       }
       else{
           session.setAttribute("errorMsg","Current password is incorrect");
       }



        return "redirect:/user/profile";

    }

}
