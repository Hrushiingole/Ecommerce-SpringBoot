package com.ecom.Shopping_Cart.Controller;

import com.ecom.Shopping_Cart.model.Category;
import com.ecom.Shopping_Cart.service.CategoryService;

import com.ecom.Shopping_Cart.service.CategoryServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Controller
@RequestMapping("/admin")
public class AdminController {
    private final CategoryServiceImpl categoryService;

    public AdminController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String index(){
        return "admin/index";
    }
    @GetMapping("/loadAddProduct")
    public String loadAddProductPage(){
        return "admin/add_product";
    }


    @GetMapping("/category")
    public String category(){
        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@NotNull @ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session){

            String ImageName=file!=null ? file.getOriginalFilename():"default.jpg";
            category.setImageName(ImageName);
            if (categoryService.existCategory(category.getName())){
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



}
