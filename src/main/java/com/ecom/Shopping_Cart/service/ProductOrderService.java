package com.ecom.Shopping_Cart.service;


import com.ecom.Shopping_Cart.model.OrderRequest;
import com.ecom.Shopping_Cart.model.ProductOrder;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductOrderService {

    public void saveProductOrder(Integer Uid, OrderRequest orderRequest);

    public List<ProductOrder> getOrderByUser(Integer Uid);

    public ProductOrder updateOrderStatus(Integer id, String st);

    public List<ProductOrder> getAllOrders();
    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize);

    public  ProductOrder getOrderById(String id);
}