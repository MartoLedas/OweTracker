package com.example.owetracker.service;

import com.example.owetracker.model.Friend;
import com.example.owetracker.model.User;
import com.example.owetracker.repository.FriendRepository;
import com.example.owetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FriendServiceTest {

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendService friendService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddFriend() {
        Integer userId = 1;
        Integer friendId = 2;

        // Mocking friendRepository to return false for existsByUserIdAndFriendId
        when(friendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(false);

        // Mocking save method to return the saved entity
        when(friendRepository.save(any(Friend.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method
        friendService.addFriendRequest(userId, friendId);

        // Verify save was called with the correct argument
        verify(friendRepository).save(argThat(friend ->
                friend.getUserId().equals(userId) && friend.getFriendId().equals(friendId)));
    }

    @Test
    void testAddFriendThrowsExceptionWhenFriendshipExists() {
        Integer userId = 1;
        Integer friendId = 2;

        // Mocking friendRepository to return true for existsByUserIdAndFriendId
        when(friendRepository.existsByUserIdAndFriendId(userId, friendId)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            friendService.addFriendRequest(userId, friendId);
        });

        assertEquals("Friend request already sent or friendship exists", exception.getMessage());
    }


    @Test
    void testGetUserFriendsReturnsEmptyListWhenNoFriends() {
        Integer userId = 1;

        // Mocking friendRepository to return an empty list
        when(friendRepository.findFriendIdsByUserId(userId)).thenReturn(List.of());

        // Mocking userRepository to return an empty list
        when(userRepository.findAllById(List.of())).thenReturn(List.of());

        // Call the method
        List<User> result = friendService.getUserFriends(userId);

        assertTrue(result.isEmpty());
    }
}
