package com.ecom.Shopping_Cart.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
@Component
public class CommonUtil {

    @Autowired
    private  JavaMailSender mailSender;


    public  Boolean sendEmail(String URL, String recipientEmail) throws UnsupportedEncodingException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("hrishikeshingole532@gmail.com","Shopping Cart");
        helper.setTo(recipientEmail);
        helper.setSubject("Reset Password");

        String content="<p>Click the link below to reset your password</p>\n" + "\n" +
                        "<a href=\""+URL+"\">Reset Password</a>\n" + "\n" +
                        "<p>Thanks</p>\n" + "\n" +
                        "<p>Shopping Cart</p>";
        helper.setText(content,true);
        mailSender.send(message);
        return true;
    }

    public static String generateUrl(HttpServletRequest request) {
        //http://localhost:8080/forgetPassword
       String url=request.getRequestURL().toString();
       return url.replace(request.getServletPath(),"");



    }
}
