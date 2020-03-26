package com.matbia.controller;

import com.matbia.enums.Gender;
import com.matbia.model.User;
import com.matbia.service.ChatService;
import com.matbia.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ChatController.class)
@WithMockUser(roles = "CONFIRMED")
public class ChatControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private ChatService chatService;

    @Before
    public void init() {
        User u1 = new User();
        u1.setId(1);
        u1.setFirstName("Name1");
        u1.setLastName("Surname1");
        u1.setGender(Gender.MALE);
        u1.setEmail("aBc@deF.org");
        u1.setPassword("12345678");
        User u2 = new User();
        u2.setId(2);
        u2.setFirstName("Name2");
        u2.setLastName("Surname2");
        u2.setGender(Gender.FEMALE);
        u2.setEmail("test@example");
        u2.setPassword("12345678");

        Mockito.when(userService.getCurrent()).thenReturn(u1);
        Mockito.when(userService.getOne(2)).thenReturn(Optional.of(u2));
    }

    @Test
    public void showChat() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/chat/dashboard").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("chat/index"));
    }

    @Test
    public void sentChatMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/chat/send")
                .param("recipientId", "2")
                .param("message", "test message")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    public void loadUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/chat/users").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("chat/users"));
    }

    @Test
    public void loadMessages() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/chat/load/{id}", 1).accept(MediaType.TEXT_HTML))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getMessagesCount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/chat/count/{id}", 2).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}