package com.ecom.Shopping_Cart.Controller;

import com.ecom.Shopping_Cart.model.Category;
import com.ecom.Shopping_Cart.model.Product;
import com.ecom.Shopping_Cart.service.CategoryService;

import com.ecom.Shopping_Cart.service.CategoryServiceImpl;
import com.ecom.Shopping_Cart.service.ProductService;
import com.ecom.Shopping_Cart.service.ProductServiceImpl;
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
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

  private final CategoryServiceImpl categoryService;

  private final ProductServiceImpl productService;

  public AdminController(CategoryServiceImpl categoryService, ProductServiceImpl productService) {
    this.categoryService = categoryService;
    this.productService = productService;
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

      return "admin/viewProduct";
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
                        System.out.println(path);
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



}
