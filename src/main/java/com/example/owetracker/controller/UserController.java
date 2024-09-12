package com.example.owetracker.controller;

import com.example.owetracker.model.User;
import com.example.owetracker.model.UserProfileUpdateRequest;
import com.example.owetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Get user by ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id).orElse(null);
    }

    // Get user by username
    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user, Model model) {
        try {
            userService.registerUser(user);
            model.addAttribute("message", "User registered successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (RuntimeException e) {
            model.addAttribute("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // Login (validate user)
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User loginRequest, HttpSession session) {
        boolean isValid = userService.validateUserLogin(loginRequest.getUsername(), loginRequest.getPassword());

        if (isValid) {
            User user = userService.getUserByUsername(loginRequest.getUsername());
            session.setAttribute("userId", user.getId());
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }



    // Logout
    @PostMapping("/logout")
    public RedirectView logoutUser(HttpSession session) {
        session.invalidate(); // Invalidate the session
        return new RedirectView("/login"); // Redirect to the login page
    }

    // Update the user profile
    @PutMapping("/profile")
    public ResponseEntity<String> updateUserProfile(@RequestBody UserProfileUpdateRequest updateRequest, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in first.");
        }

        try {
            userService.updateUserProfile(userId, updateRequest);
            return ResponseEntity.ok("Profile updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating profile: " + e.getMessage());
        }
    }

    // In UserController.java
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String query) {
        return userService.searchUsers(query);
    }



}
