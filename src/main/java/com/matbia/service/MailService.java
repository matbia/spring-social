package com.matbia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class MailService {
    private static final Logger LOGGER = Logger.getLogger(MailService.class.getName());

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Autowired
    private JavaMailSender emailSender;

    /**
     * Sends a stylized email message
     * @param to recipient's email address
     * @param subject email subject line
     * @param text content that will be included in the message body
     */
    public void send(String to, String subject, String text) {
        MimeMessage mail = emailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            helper.setTo(to);
            helper.setFrom(mailUsername);
            helper.setSubject(subject);
            helper.setText("<html><body><center><h3>Spring Social</h3></center><p>" + text + "</p></body></html>");
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, e.toString());
        }

        try {
            emailSender.send(mail);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send mail: " + e.toString());
        }
    }
}
