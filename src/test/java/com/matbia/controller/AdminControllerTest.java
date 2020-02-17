package com.matbia.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest(AdminController.class)
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void indexAsAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void indexAsUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/dashboard"))
                .andExpect(status().isForbidden());
    }
}