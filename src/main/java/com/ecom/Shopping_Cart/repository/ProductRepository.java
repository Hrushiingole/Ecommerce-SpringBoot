package com.ecom.Shopping_Cart.repository;

import com.ecom.Shopping_Cart.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Page<Product> findByIsActiveTrue(Pageable pageable );


    List<Product> findByIsActiveTrue();
    List<Product> findByCategory(String category);
    Page<Product> findByCategory( Pageable pageable,String category);

    List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch,String ch2);

}
