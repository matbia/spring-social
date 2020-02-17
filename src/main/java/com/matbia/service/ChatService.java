package com.matbia.service;

import com.matbia.model.ChatMessage;
import com.matbia.model.User;
import com.matbia.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ChatService {
    @Autowired
    private ChatMessageRepository repository;

    public void save(ChatMessage chatMessage) {
        try {
            repository.save(chatMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ChatMessage> getMessages(User u1, User u2) {
        //Fetch all messages sent from u1 to u2
        List<ChatMessage> messages = repository.findAllByUserFromAndUserTo(u1, u2);
        //Fetch all messages sent from u2 to u1
        messages.addAll(repository.findAllByUserFromAndUserTo(u2, u1));
        //Sort by timestamp
        messages.sort(Comparator.comparing(ChatMessage::getTimestamp));
        return messages;
    }

    public long getCount(User userFrom, User userTo) {
        return repository.countByUserFromAndUserTo(userFrom, userTo);
    }
}