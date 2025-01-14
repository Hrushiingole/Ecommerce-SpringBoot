package com.ecom.Shopping_Cart.service;

import com.ecom.Shopping_Cart.model.Category;
import com.ecom.Shopping_Cart.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;




    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategory(){
        return categoryRepository.findAll();
    }

    @Override
    public Boolean existsCategory(String name){
        return categoryRepository.existsByName(name);
    }

    @Override
    public Boolean DeleteCategory(int id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if(category!=null){
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Category getCategory(int id) {
       Category category= categoryRepository.findById(id).orElse(null);
       return category;

    }

    @Override
    public List<Category> getAllActiveCategory() {
        List<Category> categories=categoryRepository.findByIsActiveTrue();
        return categories;
    }
}
