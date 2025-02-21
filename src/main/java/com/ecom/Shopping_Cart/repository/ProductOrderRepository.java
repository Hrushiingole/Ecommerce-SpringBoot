package com.ecom.Shopping_Cart.repository;

import com.ecom.Shopping_Cart.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderRepository  extends JpaRepository<ProductOrder,Integer> {
        public List<ProductOrder> findByUserId(Integer Uid);
        public ProductOrder findByOrderId(String id);
}
