package com.matbia.service;

import org.springframework.beans.factory.annotation.Autowired;
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
    private final String SERVER_MAIL = "webservice@firemail.cc";

    @Autowired
    private JavaMailSender emailSender;

    public void send(String to, String subject, String text) {
        //JavaMailSenderImpl sender = new JavaMailSenderImpl();
        MimeMessage mail = emailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            helper.setTo(to);
            helper.setFrom(SERVER_MAIL);
            helper.setSubject(subject);
            helper.setText("<html><body><center><h3>Coach Meets</h3></center><p>" + text + "</p></body></html>");
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, e.toString());
        }

        /*
        FileSystemResource res = new FileSystemResource(new File("c:/Sample.jpg"));
        helper.addInline("identifier1234", res);
        **/

        try {
            emailSender.send(mail);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send mail: " + e.toString());
        }
    }
}
