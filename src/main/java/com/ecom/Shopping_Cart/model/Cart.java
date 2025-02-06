package com.ecom.Shopping_Cart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer id;

    @ManyToOne
    private UserDtls user;

    @ManyToOne
    private Product product;

    private Integer quantity;

    @Transient //doesnt create a column in the database
    private Double totalPrice;

    @Transient
    private Double totalOrderPrice;


}
