package com.matbia.repository;

import com.matbia.model.ConfirmationToken;
import com.matbia.model.User;
import org.springframework.data.repository.CrudRepository;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, Long> {
    ConfirmationToken findByToken(String token);
    void deleteByUser(User user);
}
