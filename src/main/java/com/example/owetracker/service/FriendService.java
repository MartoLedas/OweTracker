package com.example.owetracker.service;

import com.example.owetracker.model.Friend;
import com.example.owetracker.model.User;
import com.example.owetracker.repository.FriendRepository;
import com.example.owetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

/*    public void addFriend(Integer userId, Integer friendId) {

        if (!friendRepository.existsByUserIdAndFriendId(userId, friendId)) {
            Friend newFriend = new Friend(userId, friendId);
            friendRepository.save(newFriend);
        } else {
            throw new RuntimeException("Friendship already exists");
        }
    }*/



    public List<User> getUserFriends(Integer userId) {
        List<Friend> friends = friendRepository.findByUserIdOrFriendIdAndStatus(userId, userId, "ACCEPTED");

        List<Integer> friendIds = new ArrayList<>();

        for (Friend friend : friends) {
            int id = friend.getUserId().equals(userId) ? friend.getFriendId() : friend.getUserId();
            friendIds.add(id);
        }

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

    public BigDecimal getTotalOwedByUser(Integer userId) {

        return friendRepository.getTotalOwedByUser(userId);
    }

    public BigDecimal getTotalOwedToUser(Integer userId) {

        return friendRepository.getTotalOwedToUser(userId);
    }

    public void addFriendRequest(Integer userId, Integer friendId) {
        if (!friendRepository.existsByUserIdAndFriendId(userId, friendId)) {
            Friend newFriend = new Friend(userId, friendId, "PENDING");
            friendRepository.save(newFriend);
        } else {
            throw new RuntimeException("Friend request already sent or friendship exists");
        }
    }

    public void acceptFriendRequest(Integer userId, Integer friendId) {
        Friend friend = friendRepository.findByUserIdAndFriendId(friendId, userId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
        friend.setStatus("ACCEPTED");
        friendRepository.save(friend);
    }

    public void declineFriendRequest(Integer userId, Integer friendId) {
        Friend friend = friendRepository.findByUserIdAndFriendId(friendId, userId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
        friend.setStatus("DECLINED");
        friendRepository.save(friend);
    }
    public List<User> getPendingFriendRequests(Integer userId) {
        // Fetch all pending friend requests where the current user is the friendId (i.e., the recipient of the request)
        List<Friend> pendingFriends = friendRepository.findByFriendIdAndStatus(userId, "PENDING");

        List<Integer> friendIds = new ArrayList<>();

        for (Friend friend : pendingFriends) {
            friendIds.add(friend.getUserId());  // The sender of the request
        }

        return userRepository.findAllById(friendIds);
    }
}
