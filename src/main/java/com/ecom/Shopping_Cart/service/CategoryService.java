package com.ecom.Shopping_Cart.service;

import com.ecom.Shopping_Cart.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService  {



    public Category saveCategory(Category category);

    public Boolean existsCategory(String name);

    public List<Category> getAllCategory();

    public Boolean DeleteCategory(int id);

    public Category getCategory(int id);

    public List<Category> getAllActiveCategory();

    public Page<Category> getAllActiveCategoryPagination(Integer pageNo, Integer pageSize);
}
