package com.ecom.Shopping_Cart.utils;

import com.ecom.Shopping_Cart.model.ProductOrder;
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


    String msg=null;
    public boolean sendMailForProductOrder(ProductOrder productOrder,String status) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();

        msg="<p>Hello , [[name]]</p><br><p> Thank you for ordering with us</p>\n" + "\n" +
                "<p> Your order has been <b>[[orderStatus]]</b></p>\n" + "\n" +
                "<p> Product details:</p>\n"+
                "<p>product name: [[productName]]</p>\n" +
                "<p>product price: [[productPrice]]</p>\n" +
                "<p>product quantity: [[productQuantity]]</p>\n" +
                "<p>product category : [[productCategory]]</p>\n" +
                "<p> Payment Type : [[paymentType]] </p>\n";
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("hrishikeshingole532@gmail.com","Shopping Cart");
        helper.setTo(productOrder.getOrderAddress().getEmail());
        helper.setSubject("Order status updated");
        msg=msg.replace("[[name]]",productOrder.getOrderAddress().getFirstName());
        msg=msg.replace("[[productName]]",productOrder.getProduct().getTitle());
        msg=msg.replace("[[productPrice]]",productOrder.getProduct().getPrice().toString());
        msg=msg.replace("[[productQuantity]]" , productOrder.getQuantity().toString() );
        msg=msg.replace("[[productCategory]]" , productOrder.getProduct().getCategory() );
        msg=msg.replace("[[paymentType]]" , productOrder.getPaymentType() );


        msg=msg.replace("[[orderStatus]]",status);


        helper.setText(msg,true);
        mailSender.send(message);
        return true;
    }
}
