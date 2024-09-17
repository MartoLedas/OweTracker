package com.example.owetracker.service;

import com.example.owetracker.model.Friend;
import com.example.owetracker.model.User;
import com.example.owetracker.repository.FriendRepository;
import com.example.owetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class FriendService {
    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean areFriends(Integer userId, Integer friendId) {
        return friendRepository.existsByUserIdAndFriendId(userId, friendId);
    }
    public void addFriend(Integer userId, Integer friendId) {

        if (!friendRepository.existsByUserIdAndFriendId(userId, friendId)) {
            Friend newFriend = new Friend(userId, friendId);
            friendRepository.save(newFriend);
        } else {
            throw new RuntimeException("Friendship already exists");
        }
    }



    public List<User> getUserFriends(Integer userId) {
        // Fetch all friends where the user is either the userId or the friendId
        List<Friend> friends = friendRepository.findByUserIdOrFriendId(userId, userId);

        // Initialize the list of friend IDs
        List<Integer> friendIds = new ArrayList<>();

        // Populate the friend IDs
        for (Friend friend : friends) {
            int id = friend.getUserId().equals(userId) ? friend.getFriendId() : friend.getUserId();
            friendIds.add(id);
        }

        // Return a list of User objects for the fetched friend IDs
        return userRepository.findAllById(friendIds);
    }


    public void removeFriend(Integer userId, Integer friendId) {
        if (friendRepository.existsByUserIdAndFriendId(userId, friendId)) {
            friendRepository.deleteByUserIdAndFriendId(userId, friendId);
        } else if (friendRepository.existsByUserIdAndFriendId(friendId, userId)) {
            friendRepository.deleteByUserIdAndFriendId(friendId, userId);
        } else {
            throw new RuntimeException("Friendship does not exist");
        }
    }


}
