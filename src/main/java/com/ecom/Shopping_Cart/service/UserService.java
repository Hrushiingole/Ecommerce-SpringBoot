package com.ecom.Shopping_Cart.service;

import com.ecom.Shopping_Cart.model.UserDtls;

public interface UserService {
    public UserDtls saveUser(UserDtls userDtls);

    public UserDtls getUserByEmail(String email);
}
