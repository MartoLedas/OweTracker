package com.example.owetracker.controller;

import com.example.owetracker.model.User;
import com.example.owetracker.service.FriendService;
import com.example.owetracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
public class FriendController {
    @Autowired
    private FriendService friendService;

    @PostMapping("/add")
    public ResponseEntity<String> addFriend(@RequestBody Map<String, Integer> payload, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");  // Get logged-in user's ID from session
        Integer friendId = payload.get("friendId");  // Get friend's ID from request body

        if (userId != null && friendId != null) {

            friendService.addFriend(userId, friendId);
            return ResponseEntity.ok("Friend added successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid friend ID or user not logged in");
        }
    }

    @GetMapping("/list")
    public List<User> getUserFriends(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            return friendService.getUserFriends(userId);
        } else {
            throw new RuntimeException("User not logged in");
        }
    }

}
