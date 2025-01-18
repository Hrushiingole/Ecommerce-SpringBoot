package com.ecom.Shopping_Cart.config;

import com.ecom.Shopping_Cart.model.UserDtls;
import com.ecom.Shopping_Cart.repository.UserRepository;
import com.ecom.Shopping_Cart.service.UserService;
import com.ecom.Shopping_Cart.utils.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String email=request.getParameter("username");
        UserDtls user = userRepository.findByEmail(email);

        if (user.getIsEnable()){
            if(user.getAccountNonLocked()){
                if (user.getFailAttempt() < AppConstant.ATTEMPT_TIME){
                    userService.increaseFailedAttempt(user);
                }
                else{
                    userService.userAccountLocked(user);
                    exception=new LockedException("your account is locked!, try after some time");
                }
            }else{
                if (userService.unlockAccountTimeExpired(user)){
                    exception=new LockedException("your account is unlocked!! Please try to login");

                }
                else{
                    exception=new LockedException("your account is locked!, try after some time");
                }

            }
        }else{
            exception=new LockedException("your account is inactive");
        }

        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
