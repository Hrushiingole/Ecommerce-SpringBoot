package com.ecom.Shopping_Cart.service;
import com.ecom.Shopping_Cart.model.UserDtls;
import com.ecom.Shopping_Cart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDtls saveUser(UserDtls userDtls) {
        userDtls.setRole("ROLE_USER");
        String encodedPassword=passwordEncoder.encode(userDtls.getPassword());
        userDtls.setPassword(encodedPassword);
        UserDtls saveUser=userRepository.save(userDtls);

        return saveUser;
    }

    @Override
    public UserDtls getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
