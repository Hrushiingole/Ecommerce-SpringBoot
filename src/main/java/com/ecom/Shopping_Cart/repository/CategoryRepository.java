package com.ecom.Shopping_Cart.repository;

import com.ecom.Shopping_Cart.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {
    public Boolean existsByName(String name);

}