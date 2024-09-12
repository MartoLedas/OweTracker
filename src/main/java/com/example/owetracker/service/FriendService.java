package com.example.owetracker.service;

import com.example.owetracker.model.Friend;
import com.example.owetracker.model.User;
import com.example.owetracker.repository.FriendRepository;
import com.example.owetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendService {
    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;
    public void addFriend(Integer userId, Integer friendId) {

        if (!friendRepository.existsByUserIdAndFriendId(userId, friendId)) {
            Friend newFriend = new Friend(userId, friendId);
            friendRepository.save(newFriend);
        } else {
            throw new RuntimeException("Friendship already exists");
        }
    }



    public List<User> getUserFriends(Integer userId) {
        // Fetch the list of friend IDs where the user is the one initiating the friendship
        List<Integer> friendIds = friendRepository.findFriendIdsByUserId(userId);

        // Return a list of User objects for the fetched friend IDs
        return userRepository.findAllById(friendIds);
    }

}
