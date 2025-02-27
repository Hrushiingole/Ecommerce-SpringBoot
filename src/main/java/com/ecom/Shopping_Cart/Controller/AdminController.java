package com.ecom.Shopping_Cart.Controller;

import com.ecom.Shopping_Cart.model.Category;
import com.ecom.Shopping_Cart.model.Product;


import com.ecom.Shopping_Cart.model.ProductOrder;
import com.ecom.Shopping_Cart.model.UserDtls;
import com.ecom.Shopping_Cart.service.*;

import com.ecom.Shopping_Cart.utils.CommonUtil;
import com.ecom.Shopping_Cart.utils.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private  CartService cartService;

    @Autowired
private ProductOrderService productOrderService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ModelAttribute
    public void getUserDetails(Principal p,Model model){
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






    @GetMapping("/")
    public String index(){
        return "admin/index";
    }
    @GetMapping("/loadAddProduct")
    public String loadAddProductPage(Model m){
        List<Category> categories=categoryService.getAllCategory();
        m.addAttribute("categories",categories);
        return "admin/add_product";
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product,HttpSession session,@RequestParam("file") MultipartFile image){

        String imageName=image.isEmpty()? "default.jpg":image.getOriginalFilename();

        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());
        Product saveProduct=productService.saveProduct(product);

        if (saveProduct==null){
            session.setAttribute("errorMsg","Product not saved");
        }else {
            try{
                File saveFile=new ClassPathResource("static/img").getFile();
                Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+"product_img"+File.separator+imageName);
                System.out.println(path);
                Files.copy(image.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            session.setAttribute("succMsg","Product saved");
        }
        return "redirect:/admin/loadAddProduct";
    }

    @GetMapping("/products")
    public String loadViewProduct(Model m,@RequestParam(defaultValue = "") String ch,@RequestParam(defaultValue = "0",name="pageNo") Integer page,@RequestParam(defaultValue = "5",name="pageSize") Integer size){
//        List<Product> products=null;
//        if(ch!=null && ch.length()>0){
//            products=productService.searchProduct(ch);
//        }
//        else{
//            products=productService.getAllProduct();
//        }
//        m.addAttribute("products",products);

        Page<Product> pages=null;
        if(ch!=null && ch.length()>0){
            pages=productService.searchProductPagination(page,size,ch);
        }
        else{
            pages=productService.getAllProductPagination(page,size);
        }
        m.addAttribute("products",pages.getContent());

        m.addAttribute("pageSize", size);
        m.addAttribute("pageNo",pages.getNumber());
        m.addAttribute("totalElements",pages.getTotalElements());
        m.addAttribute("totalPages",pages.getTotalPages());
        m.addAttribute("isFirst",pages.isFirst());
        m.addAttribute("isLast",pages.isLast());


        return "admin/Product";
    }


    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id,HttpSession session){
        Boolean deleteProduct = productService.deleteProduct(id);
        if(deleteProduct){
            session.setAttribute("succMsg","Product deleted");
        }
        else{
            session.setAttribute("errorMsg","Product not deleted");
        }
        return "redirect:/admin/products";
    }


    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id,Model m){
        m.addAttribute("product",productService.getProductById(id));
        m.addAttribute("categories",categoryService.getAllCategory());
        return "admin/editProduct";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product,HttpSession session,@RequestParam("file") MultipartFile image){

      if(product.getDiscount()<0 || product.getDiscount()>100){
          session.setAttribute("errorMsg","Invalid Discount");
      }
      else{
          Product updateProduct = productService.updateProduct(product,image);
          if (updateProduct==null){
              session.setAttribute("errorMsg","Product not updated");
          }
          else{
              session.setAttribute("succMsg","Product updated");
          }
      }

        return "redirect:/admin/editProduct/"+product.getId();
    }






//    Category controllers starts


    @GetMapping("/category")
    public String category(Model model,@RequestParam(defaultValue = "0",name="pageNo") Integer page,@RequestParam(defaultValue = "5",name="pageSize") Integer size){
//        m.addAttribute("categories",categoryService.getAllCategory());
    Page<Category> categoryPage=categoryService.getAllActiveCategoryPagination(page,size);
        List<Category> categoryList = categoryPage.getContent();
        model.addAttribute("categories", categoryList);
        model.addAttribute("pageSize",categoryList.size());
        model.addAttribute("pageNo",categoryPage.getNumber());
        model.addAttribute("totalElements",categoryPage.getTotalElements());
        model.addAttribute("totalPages",categoryPage.getTotalPages());
        model.addAttribute("isFirst",categoryPage.isFirst());
        model.addAttribute("isLast",categoryPage.isLast());


        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@NotNull @ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session){

            String ImageName=file!=null ? file.getOriginalFilename():"default.jpg";
            category.setImageName(ImageName);
            if (categoryService.existsCategory(category.getName())){
                session.setAttribute("errorMsg","Category already exist");
            }else{
                Category saveCategory = categoryService.saveCategory(category);
                if (saveCategory==null){
                    session.setAttribute("errorMsg","Category not saved");
                }else{
                    try{
                        File saveFile=new ClassPathResource("static/img").getFile();
                        Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+File.separator+ImageName);
//                        System.out.println(path);
                        Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }



                    session.setAttribute("succMsg","Category saved");
                }
            }

        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id,HttpSession session){
        Boolean deleteCategory = categoryService.DeleteCategory(id);
        if(deleteCategory){
            session.setAttribute("succMsg","Category deleted");
        }
        else{
            session.setAttribute("errorMsg","Category not deleted");
        }
        return "redirect:/admin/category";
    }


    @GetMapping("/editCategory/{id}")
    public String editCategory(@PathVariable int id,Model m){
       m.addAttribute("category",categoryService.getCategory(id));
        return "admin/editCategory";

    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category,@RequestParam("file") MultipartFile file,HttpSession session){
        Category oldCategory=categoryService.getCategory(category.getId());
        String imageName= file.isEmpty() ?oldCategory.getImageName():file.getOriginalFilename();
        if(oldCategory!=null){
            oldCategory.setId(category.getId());
            oldCategory.setName(category.getName());
            oldCategory.setImageName(imageName);

        }
        Category upadatedCategory=categoryService.saveCategory(oldCategory);
        if (upadatedCategory==null){
            session.setAttribute("errorMsg","Category not updated");
        }
        else{
            if(!file.isEmpty()){
                try{
                    File saveFile=new ClassPathResource("static/img").getFile();
                    Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+File.separator+imageName);
                    Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            session.setAttribute("succMsg","Category updated");
        }
        return "redirect:/admin/editCategory/"+category.getId();
    }


//    show users Controller


    @GetMapping("/users")
    public String getAllUsers(Model m,@RequestParam Integer type ){
        List<UserDtls> users=null;

        if (type==1){
            users=userService.getAllUsers("ROLE_USER");
        }else{
            users=userService.getAllUsers("ROLE_ADMIN");
        }
        m.addAttribute("users",users);
        m.addAttribute("type",type);
        return "/admin/users";
    }

    @GetMapping("/updateStatus")
    public String updateUserAccountStatus(@RequestParam String type,@RequestParam Boolean status,@RequestParam Integer id,HttpSession session){
          Boolean f = userService.updateUserAccountStatus(status,id);
          if (f==false){
              session.setAttribute("errorMsg","Something went wrong");

          }
          else{
              session.setAttribute("succMsg","User status updated");
          }
          return "redirect:/admin/users?type="+type;

    }


    //order related handling

    @GetMapping("/orders")
    public String getAllOrders(Model m,@RequestParam(defaultValue = "0",name="pageNo") Integer pageNo,@RequestParam(defaultValue = "2",name="pageSize") Integer size){

        Page<ProductOrder> page=productOrderService.getAllOrdersPagination(pageNo,size);

        m.addAttribute("orderList",page.getContent());




        m.addAttribute("pageSize",size);
        m.addAttribute("pageNo",page.getNumber());
        m.addAttribute("totalElements",page.getTotalElements());
        m.addAttribute("totalPages",page.getTotalPages());
        m.addAttribute("isFirst",page.isFirst());
        m.addAttribute("isLast",page.isLast());
        return "/admin/orders";
    }
    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Integer id,@RequestParam Integer st,HttpSession session){
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



        return "redirect:/admin/orders";
    }

    @GetMapping("/search-orders")
    public String searchOrders(@RequestParam String orderId,Model m,HttpSession session){
        ProductOrder productOrder=productOrderService.getOrderById(orderId);
        if(productOrder==null){
            session.setAttribute("errorMsg","Incorrect order id");
            m.addAttribute("orderList",productOrderService.getAllOrders());
        }else{
            List<ProductOrder> orderList=new ArrayList<>();
            orderList.add(productOrder);
            m.addAttribute("orderList", orderList);
        }
        return "/admin/orders";
    }

//add admin

    @GetMapping("/add-admin")
    public String addAdmin(Model m){
//        m.addAttribute("user",new UserDtls());
        return "/admin/add_admin";
    }
    @PostMapping("/save-admin")
    public String saveAdmin(@ModelAttribute UserDtls userDtls,HttpSession session){
        String email=userDtls.getEmail();
        boolean isExist=userService.getUserByEmail(email)!=null;
        if(isExist){
            session.setAttribute("errorMsg","Email already exist");
            return "redirect:/admin/add-admin";
        }

        UserDtls saveAdmin=userService.saveAdmin(userDtls);
        if(saveAdmin==null){
            session.setAttribute("errorMsg","Something went wrong");
        }else{
            session.setAttribute("succMsg","Admin saved");
        }
        return "redirect:/admin/add-admin";
    }

    @GetMapping("/profile")
    public String profile(Model m,Principal p){
        UserDtls user=getLoggedInUserDetails(p);
        m.addAttribute("user",user);
        return "/admin/profile";
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
        return "redirect:/admin/profile";
    }
    private UserDtls getLoggedInUserDetails(Principal p) {
        String email=p.getName();
        UserDtls user=userService.getUserByEmail(email);
        return user;
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



        return "redirect:/admin/profile";

    }

}
