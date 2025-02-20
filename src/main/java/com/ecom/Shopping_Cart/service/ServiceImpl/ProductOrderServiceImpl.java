package com.ecom.Shopping_Cart.service.ServiceImpl;

import com.ecom.Shopping_Cart.model.Cart;
import com.ecom.Shopping_Cart.model.OrderAddress;
import com.ecom.Shopping_Cart.model.OrderRequest;
import com.ecom.Shopping_Cart.model.ProductOrder;
import com.ecom.Shopping_Cart.repository.CartRepository;
import com.ecom.Shopping_Cart.repository.ProductOrderRepository;
import com.ecom.Shopping_Cart.service.ProductOrderService;
import com.ecom.Shopping_Cart.utils.CommonUtil;
import com.ecom.Shopping_Cart.utils.OrderStatus;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CommonUtil commonUtil;


    @SneakyThrows
    @Override
    public void saveProductOrder(Integer Uid, OrderRequest orderRequest) {
        List<Cart> cartList = cartRepository.findByUserId(Uid);


        for(Cart cart:cartList){
            ProductOrder productOrder=new ProductOrder();
            productOrder.setOrderId(UUID.randomUUID().toString());
            productOrder.setOrderDate(new Date());

            productOrder.setProduct(cart.getProduct());
            productOrder.setPrice(BigDecimal.valueOf(cart.getProduct().getDiscountPrice()));

            productOrder.setQuantity(cart.getQuantity());
            productOrder.setUser(cart.getUser());

            productOrder.setStatus(OrderStatus.IN_PROGRESS.getName());
            productOrder.setPaymentType(orderRequest.getPaymentType());

            OrderAddress orderAddress=new OrderAddress();
            orderAddress.setFirstName(orderRequest.getFirstName());
            orderAddress.setLastName(orderRequest.getLastName());
            orderAddress.setEmail(orderRequest.getEmail());
            orderAddress.setMobileNumber(orderRequest.getMobileNumber());
            orderAddress.setCity(orderRequest.getCity());
            orderAddress.setState(orderRequest.getState());
            orderAddress.setZipCode(orderRequest.getZipCode());

            productOrder.setOrderAddress(orderAddress);
            ProductOrder saveOrder=productOrderRepository.save(productOrder);



            commonUtil.sendMailForProductOrder(saveOrder,OrderStatus.IN_PROGRESS.getName());
        }

    }

    @Override
    public List<ProductOrder> getOrderByUser(Integer Uid) {
        List<ProductOrder> productOrders=productOrderRepository.findByUserId(Uid);
        return  productOrders;
    }

    @Override
    public ProductOrder updateOrderStatus(Integer id, String st) {
      Optional<ProductOrder> productOrder=productOrderRepository.findById(id);
      if(productOrder.isPresent()){
          productOrder.get().setStatus(st);
          ProductOrder productOrder1=productOrderRepository.save(productOrder.get());
          return productOrder1;
      }
        return null;
    }

    @Override
    public List<ProductOrder> getAllOrders() {
       return productOrderRepository.findAll();
    }
}
