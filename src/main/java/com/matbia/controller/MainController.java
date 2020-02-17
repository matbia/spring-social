package com.matbia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class MainController {
    @GetMapping("/")
    public String showIndex(Principal user) {
        //If user is not logged in
        if(user == null) return "index";
        return "redirect:/feed/dashboard";
    }
}
