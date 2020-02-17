package com.matbia.controller;

import com.matbia.exception.ObjectNotFoundException;
import com.matbia.model.ChatMessage;
import com.matbia.model.User;
import com.matbia.service.ChatService;
import com.matbia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("chat")
public class ChatController {
    @Autowired
    private UserService userService;
    @Autowired
    private ChatService chatService;

    @GetMapping("dashboard")
    public String showChat(Model model) {
        model.addAttribute("user", userService.getCurrent());
        return "chat/index";
    }

    @PostMapping("send")
    public ResponseEntity<HttpStatus> sentChatMessage(@RequestParam("recipientId") long recipientId, @RequestParam("message") String msg) {
        Optional<User> recipient = userService.getOne(recipientId);
        if(recipient.isPresent()) {
            chatService.save(new ChatMessage(userService.getCurrent(), recipient.get(), msg));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("users")
    public String loadUsers(Model model) {
        Set<User> users = userService.idsToUsers(userService.getCurrent().getWatchedUsersIds());
        users.forEach(u -> u.setOnline(userService.isOnline(u.getEmail())));
        model.addAttribute("users", users);
        return "chat/users";
    }

    @GetMapping("load/{userId}")
    public String loadMessages(@PathVariable("userId") long userId, Model model) {
        Optional<User> user = userService.getOne(userId);
        if (!user.isPresent()) throw new ObjectNotFoundException();
        model.addAttribute("messages", chatService.getMessages(user.get(), userService.getCurrent()));
        return "chat/messages";
    }

    @ResponseBody
    @GetMapping("count/{userId}")
    public long getMessagesCount(@PathVariable("userId") long userId) {
        return chatService.getCount(userService.getOne(userId).get(), userService.getCurrent());
    }
}
