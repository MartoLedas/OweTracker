package com.example.owetracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebController {

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("message", "");
        return "register"; // returns html page
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("message", "");
        return "login"; // returns html page
    }
}
