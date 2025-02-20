package com.ecom.Shopping_Cart.service;


import com.ecom.Shopping_Cart.model.OrderRequest;
import com.ecom.Shopping_Cart.model.ProductOrder;

import java.util.List;

public interface ProductOrderService {

    public void saveProductOrder(Integer Uid, OrderRequest orderRequest);

    public List<ProductOrder> getOrderByUser(Integer Uid);

    public ProductOrder updateOrderStatus(Integer id, String st);

    public List<ProductOrder> getAllOrders();
}