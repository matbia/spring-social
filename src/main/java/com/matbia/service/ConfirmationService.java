package com.matbia.service;

import com.matbia.misc.Utils;
import com.matbia.model.ConfirmationToken;
import com.matbia.model.User;
import com.matbia.repository.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ConfirmationService {
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private MailService mailService;

    public ConfirmationToken findByToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public void generate(User user) {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setUser(user);
        confirmationTokenRepository.save(confirmationToken);

        String message = "http://" + Utils.getExternalIP() + ":8080/user/confirm?token=" + confirmationToken.getToken();
        mailService.send(user.getContactEmail(), " Coach Meets - Potwierdzenie rejestracji", message);
    }

    @Transactional
    public void deleteByUser(User user) {
        try {
            confirmationTokenRepository.deleteByUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(long id) {
        try {
            confirmationTokenRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
