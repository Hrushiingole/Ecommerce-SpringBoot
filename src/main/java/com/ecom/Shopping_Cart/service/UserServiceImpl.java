package com.ecom.Shopping_Cart.service;
import com.ecom.Shopping_Cart.model.UserDtls;
import com.ecom.Shopping_Cart.repository.UserRepository;
import com.ecom.Shopping_Cart.utils.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
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
        userDtls.setAccountNonLocked(true);
        userDtls.setFailAttempt(0);

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
       int attempt=userDtls.getFailAttempt()+1;
       userDtls.setFailAttempt(attempt);
       userRepository.save(userDtls);
    }

    @Override
    public void userAccountLocked(UserDtls userDtls) {
           userDtls.setAccountNonLocked(false);
           userDtls.setLockTime(new Date());
           userRepository.save(userDtls);

    }

    @Override
    public boolean unlockAccountTimeExpired(UserDtls userDtls) {
        long lockTime=userDtls.getLockTime().getTime();
        long unlockTime=lockTime+ AppConstant.UNLOCK_DURATION_TIME;
        long currentTimeMillis=System.currentTimeMillis();
        if(currentTimeMillis>unlockTime){
            userDtls.setAccountNonLocked(true);
            userDtls.setFailAttempt(0);
            userDtls.setLockTime(null);
            userRepository.save(userDtls);
            return true;
        }
        return false;
    }

    @Override
    public void resetAttempt(int id) {

    }
}
