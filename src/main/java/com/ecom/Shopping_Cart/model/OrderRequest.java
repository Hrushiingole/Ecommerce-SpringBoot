package com.ecom.Shopping_Cart.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderRequest {

    private String firstName;

    private  String Email;
    private String lastName;

    private String address;

    private String city;

    private String state;



    private String zipCode;

    private String mobileNumber;

    private String paymentType;
}
