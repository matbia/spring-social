package com.matbia.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorControllerImpl implements ErrorController {
    @RequestMapping("/error")
    public String handleError(Model model, HttpServletRequest request) {
        model.addAttribute("errorCode", request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString());
        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
