package com.matbia.repository;

import com.matbia.model.ChatMessage;
import com.matbia.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByUserFromAndUserTo(User userFrom, User userTo);
    long countByUserFromAndUserTo(User userFrom, User userTo);
}
