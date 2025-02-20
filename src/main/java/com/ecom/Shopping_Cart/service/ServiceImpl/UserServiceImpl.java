package com.ecom.Shopping_Cart.service.ServiceImpl;
import com.ecom.Shopping_Cart.model.UserDtls;
import com.ecom.Shopping_Cart.repository.UserRepository;
import com.ecom.Shopping_Cart.service.UserService;
import com.ecom.Shopping_Cart.utils.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;


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

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        UserDtls userDtls=userRepository.findByEmail(email);
        userDtls.setResetToken(resetToken);
        userRepository.save(userDtls);
    }

    @Override
    public UserDtls getUserByToken(String token) {
        UserDtls userDtls=userRepository.findByResetToken(token);
        return userDtls;
    }
    @Override
    public UserDtls updateUser(UserDtls userDtls) {
        return userRepository.save(userDtls);
    }

    @Override
    public UserDtls updateUserProfile(UserDtls userDtls, MultipartFile img) {
        UserDtls dbUser=userRepository.findById(userDtls.getId()).get();
        if(!img.isEmpty()){
            dbUser.setProfileImage(img.getOriginalFilename());
        }
        if (dbUser!=null){
            dbUser.setName(userDtls.getName());
            dbUser.setMobileNumber(userDtls.getMobileNumber());
            dbUser.setAddress(userDtls.getAddress());
            dbUser.setCity(userDtls.getCity());
            dbUser.setState(userDtls.getState());
            dbUser.setPincode(userDtls.getPincode());
            return userRepository.save(dbUser);
        }

            try {
                if(!img.isEmpty()) {
                    File saveFile = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + img.getOriginalFilename());
//                System.out.println(path);
                    Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        return null;

    }
}
