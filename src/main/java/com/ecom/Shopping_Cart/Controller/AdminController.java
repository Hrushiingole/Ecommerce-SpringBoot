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
    public String loadViewProduct(Model m){
      m.addAttribute("products",productService.getAllProduct());
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
    public String category(Model m){
        m.addAttribute("categories",categoryService.getAllCategory());
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
    public String getAllUsers(Model m){
        m.addAttribute("users",userService.getAllUsers("ROLE_USER"));
        return "/admin/users";
    }

    @GetMapping("/updateStatus")
    public String updateUserAccountStatus(@RequestParam Boolean status,@RequestParam Integer id,HttpSession session){
          Boolean f = userService.updateUserAccountStatus(status,id);
          if (f==false){
              session.setAttribute("errorMsg","Something went wrong");

          }
          else{
              session.setAttribute("succMsg","User status updated");
          }
          return "redirect:/admin/users";

    }


    //order related handling

    @GetMapping("/orders")
    public String getAllOrders(Model m){
        m.addAttribute("orderList",productOrderService.getAllOrders());
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

}
