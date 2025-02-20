package com.ecom.Shopping_Cart.utils;

public enum OrderStatus {
    IN_PROGRESS(1,"In progess ") , ORDER_RECEIVED(2,"Order Received"),
    PRODUCT_PACKED(3,"Product Packed"),
    OUT_FOR_DELIVERY(4,"Out for delivery"),
    DELIVERED(5,"Delivered"),
    CANCEL(6,"Cancelled"),
    SUCCESS(7,"successful");

    private Integer id;
    private String name;

    OrderStatus(Integer id ,String name) {
        this.id=id;
        this.name=name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
