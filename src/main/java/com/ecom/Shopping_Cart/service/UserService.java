package com.ecom.Shopping_Cart.service;

import com.ecom.Shopping_Cart.model.UserDtls;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    public UserDtls saveUser(UserDtls userDtls);

    public UserDtls getUserByEmail(String email);

    public List<UserDtls> getAllUsers(String role);

    public Boolean updateUserAccountStatus(Boolean status, Integer id);

    public void increaseFailedAttempt(UserDtls userDtls);

    public void userAccountLocked(UserDtls userDtls);

    public boolean unlockAccountTimeExpired(UserDtls userDtls);

    public void resetAttempt(int id);

    public void updateUserResetToken(String email, String resetToken);

    public UserDtls getUserByToken(String token);

    public UserDtls updateUser(UserDtls userDtls);
    public UserDtls updateUserProfile(UserDtls userDtls, MultipartFile img);

    UserDtls saveAdmin(UserDtls userDtls);
}
