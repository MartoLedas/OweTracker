package com.example.owetracker.controller;

import com.example.owetracker.dto.FriendAmountDTO;
import com.example.owetracker.model.User;
import com.example.owetracker.service.FriendAmountService;
import com.example.owetracker.service.FriendService;
import com.example.owetracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    @Autowired
    private FriendService friendService;
    @Autowired
    private UserService userService;
    @Autowired
    private FriendAmountService friendOwedAmountService;


    @PostMapping("/add")
    public ResponseEntity<String> addFriend(@RequestBody Map<String, Integer> payload, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");  // Get logged-in user's ID from session
        Integer friendId = payload.get("friendId");  // Get friend's ID from request body

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        if (friendId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid friend ID");
        }

        if (userId.equals(friendId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You cannot add yourself as a friend");
        }

        try {
            if (friendService.areFriends(userId, friendId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are already friends with this user");
            }

            friendService.addFriend(userId, friendId);
            return ResponseEntity.ok("Friend added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding friend: " + e.getMessage());
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


    @GetMapping("/amounts")
    public List<FriendAmountDTO> getFriendAmounts(@RequestParam Integer userId) {
        List<User> friends = friendService.getUserFriends(userId);
        List<FriendAmountDTO> amounts = new ArrayList<>();

        for (User friend : friends) {
            BigDecimal totalOwedToFriend = friendOwedAmountService.getTotalOwedToFriend(userId, friend.getId());
            BigDecimal totalOwedByFriend = friendOwedAmountService.getTotalOwedByFriend(userId, friend.getId());

            amounts.add(new FriendAmountDTO(friend.getId(), friend.getUsername(), totalOwedToFriend, totalOwedByFriend));
        }

        return amounts;
    }

    @PostMapping("/remove")
    public ResponseEntity<String> removeFriend(@RequestBody Map<String, Integer> payload, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        Integer friendId = payload.get("friendId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        if (friendId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid friend ID");
        }

        try {
            friendService.removeFriend(userId, friendId);
            return ResponseEntity.ok("Friend removed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing friend: " + e.getMessage());
        }
    }

}
