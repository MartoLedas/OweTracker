package com.example.owetracker.controller;

import com.example.owetracker.model.User;
import com.example.owetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;

@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("message", "");
        return "register"; // Returns register.html
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("message", "");
        return "login"; // Returns login.html
    }


    @GetMapping("/profile")
    public String showUserProfile(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            User user = userService.getUserById(userId).orElse(null);
            System.out.println("logged in " + userId);
            if (user != null) {
                System.out.println("username " + user.getUsername());
                model.addAttribute("user", user);
                return "profile"; // returns profile.html page
            }
        }

        return "redirect:/login"; // redirect to login if user not found or not logged in
    }


    @GetMapping("/home")
    public String showHomePage(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            return "home";
        }
        return "redirect:/login"; // Redirect to login if user is not logged in
    }

    @GetMapping("/friends")
    public String showFriendsPage(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            return "friends";
        }
        return "redirect:/login"; // Redirect to login if user is not logged in
    }

    @GetMapping("/expense")
    public String showExpenseForm(Model model) {
        model.addAttribute("message", "");
        return "expense"; // returns html page
    }
}
