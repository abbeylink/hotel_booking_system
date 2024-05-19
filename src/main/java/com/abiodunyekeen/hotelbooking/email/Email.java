package com.abiodunyekeen.hotelbooking.email;


import com.abiodunyekeen.hotelbooking.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;

@Getter
@Setter

public class Email {
    private String subject;
    private String senderName;
    private String content;
    private String emailFrom;
    private final JavaMailSender mailSender;
    private User user;

    public Email(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {


            MimeMessage message = mailSender.createMimeMessage();
            var messageHelper = new MimeMessageHelper(message);
            messageHelper.setFrom(this.emailFrom,senderName);
            messageHelper.setTo(user.getEmail());
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);
            mailSender.send(message);




    }


    public String applicationUrl(HttpServletRequest request){
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(scheme).append("://")
                .append(serverName);
        if (("http".equals(scheme) && serverPort != 80) || ("https".equals(scheme) && serverPort != 443)) {
            urlBuilder.append(":").append(serverPort);
        }
        urlBuilder.append(contextPath);

        return urlBuilder.toString();

    }
}
