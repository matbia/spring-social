package com.matbia.service;

import com.matbia.misc.Utils;
import com.matbia.model.PasswordRecoveryToken;
import com.matbia.model.User;
import com.matbia.repository.PasswordRecoveryTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class PasswordRecoveryService {
    @Autowired
    private PasswordRecoveryTokenRepository passwordRecoveryTokenRepository;
    @Autowired
    private MailService mailService;

    public PasswordRecoveryToken findByToken(String token) {
        return passwordRecoveryTokenRepository.findByToken(token);
    }

    public void generate(User user) {
        PasswordRecoveryToken psr = new PasswordRecoveryToken(user);
        passwordRecoveryTokenRepository.save(psr);

        String message = "Use this link to recover your password: http://" + Utils.getExternalIP() + ":8080/user/recover?token=" + psr.getToken();
        mailService.send(user.getContactEmail(), "Password Recovery", message);
    }

    @Transactional
    public void deleteByUser(User user) {
        try {
            passwordRecoveryTokenRepository.deleteByUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(long id) {
        try {
            passwordRecoveryTokenRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
