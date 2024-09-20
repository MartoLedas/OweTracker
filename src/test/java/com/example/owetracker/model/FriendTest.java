package com.example.owetracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FriendTest {

    private Friend friend;

    @BeforeEach
    void setUp() {
        // Initialize a Friend instance with sample data
        friend = new Friend(1, 2, "pending");
    }

    @Test
    void testFriendConstructorAndGetters() {
        // Test constructor and getters
        assertThat(friend.getUserId()).isEqualTo(1);
        assertThat(friend.getFriendId()).isEqualTo(2);
    }

    @Test
    void testSetters() {
        // Test setters
        friend.setUserId(3);
        friend.setFriendId(4);

        assertThat(friend.getUserId()).isEqualTo(3);
        assertThat(friend.getFriendId()).isEqualTo(4);
    }

    @Test
    void testNoArgsConstructor() {
        // Test no-args constructor and default values
        Friend emptyFriend = new Friend();
        assertThat(emptyFriend.getId()).isNull();
        assertThat(emptyFriend.getUserId()).isNull();
        assertThat(emptyFriend.getFriendId()).isNull();
    }

    @Test
    void testIdSetterAndGetter() {
        // Test setter and getter for id
        friend.setId(5);
        assertThat(friend.getId()).isEqualTo(5);
    }
}
