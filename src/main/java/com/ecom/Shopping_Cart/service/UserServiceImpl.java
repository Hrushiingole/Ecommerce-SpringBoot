package com.ecom.Shopping_Cart.service;
import com.ecom.Shopping_Cart.model.UserDtls;
import com.ecom.Shopping_Cart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDtls saveUser(UserDtls userDtls) {
        userDtls.setRole("ROLE_USER");
        userDtls.setIsEnable(true);
        String encodedPassword=passwordEncoder.encode(userDtls.getPassword());
        userDtls.setPassword(encodedPassword);
        UserDtls saveUser=userRepository.save(userDtls);

        return saveUser;
    }

    @Override
    public UserDtls getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDtls> getAllUsers(String role) {
        List<UserDtls> userDtls=userRepository.findByRole(role);
        return userDtls;
    }

    @Override
    public Boolean updateUserAccountStatus(Boolean status, Integer id) {
        UserDtls userDtls=userRepository.findById(id).orElse(null);
        if(userDtls!=null){
            userDtls.setIsEnable(status);
            userRepository.save(userDtls);
            return true;
        }

        return false;
    }

    @Override
    public void increaseFailedAttempt(UserDtls userDtls) {

    }

    @Override
    public void userAccountLocked(UserDtls userDtls) {

    }
}
