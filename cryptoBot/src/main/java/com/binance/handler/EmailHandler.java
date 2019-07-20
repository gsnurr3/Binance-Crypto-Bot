package com.binance.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * EmailHandler
 */
@Component
public class EmailHandler {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.to}")
    private String to;

    public void sendEmail(String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            emailSender.send(message);
        } catch (MailSendException e) {
            e.printStackTrace();
        }
    }
}