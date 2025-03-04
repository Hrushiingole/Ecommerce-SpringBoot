package com.ecom.Shopping_Cart.repository;

import com.ecom.Shopping_Cart.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {
    public Boolean existsByName(String name);

    List<Category> findByIsActiveTrue();
    Page<Category> findByIsActiveTrue(Pageable pageable);
}
