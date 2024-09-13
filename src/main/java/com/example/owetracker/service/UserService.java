package com.example.owetracker.service;

import com.example.owetracker.model.User;
import com.example.owetracker.model.UserProfileUpdateRequest;
import com.example.owetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    public User findById(Integer id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User registerUser(User user) {
        System.out.println("Registering user: " + user.getUsername() + "  " + user.getEmail());
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username is already taken");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }

        return userRepository.save(user);
    }

    // Validate login (Compare raw password with stored hashed password)
    public boolean validateUserLogin(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    public void updateUserProfile(Integer userId, UserProfileUpdateRequest updateRequest) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Update name and email
            user.setName(updateRequest.getName());
            user.setEmail(updateRequest.getEmail());

            // If password change is requested
            if (updateRequest.getCurrentPassword() != null && updateRequest.getNewPassword() != null) {
                if (passwordEncoder.matches(updateRequest.getCurrentPassword(), user.getPassword())) {
                    String encodedNewPassword = passwordEncoder.encode(updateRequest.getNewPassword());
                    user.setPassword(encodedNewPassword);
                } else {
                    throw new RuntimeException("Current password is incorrect.");
                }
            }

            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found.");
        }
    }
    public List<User> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }



}
