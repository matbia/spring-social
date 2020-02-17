package com.matbia.repository;

import com.matbia.model.PasswordRecoveryToken;
import com.matbia.model.User;
import org.springframework.data.repository.CrudRepository;

public interface PasswordRecoveryTokenRepository extends CrudRepository<PasswordRecoveryToken, Long> {
    PasswordRecoveryToken findByToken(String token);
    void deleteByUser(User user);
}
