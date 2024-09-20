package com.example.owetracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        // Create an instance of User for testing
        user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("testuser@example.com");
        user.setName("Test User");
        user.setRole("user");
    }

    @Test
    void testUserConstructorAndDefaultValues() {
        // Check default values
        assertThat(user.getRole()).isEqualTo("user");
    }

    @Test
    void testSettersAndGetters() {
        // Test the setters and getters

        // Test ID
        user.setId(2);
        assertThat(user.getId()).isEqualTo(2);

        // Test Username
        user.setUsername("newuser");
        assertThat(user.getUsername()).isEqualTo("newuser");

        // Test Password
        user.setPassword("newpassword");
        assertThat(user.getPassword()).isEqualTo("newpassword");

        // Test Email
        user.setEmail("newuser@example.com");
        assertThat(user.getEmail()).isEqualTo("newuser@example.com");

        // Test Name
        user.setName("New User");
        assertThat(user.getName()).isEqualTo("New User");

        // Test Role
        user.setRole("admin");
        assertThat(user.getRole()).isEqualTo("admin");
    }

    @Test
    void testPrePersistCreatedAt() {
        // Test that created_at is set automatically if not provided
        user.onCreate();
        assertThat(user.getCreated_at()).isNotNull();
    }

    @Test
    void testSetCreatedAt() {
        // Test setting a custom created_at value
        LocalDateTime customDate = LocalDateTime.of(2022, 1, 1, 12, 0);
        user.setCreated_at(customDate);
        assertThat(user.getCreated_at()).isEqualTo(customDate);
    }

    @Test
    void testCreatedAtNotOverwrittenIfExists() {
        // Test that created_at is not overwritten if it already has a value
        LocalDateTime existingDate = LocalDateTime.of(2022, 1, 1, 12, 0);
        user.setCreated_at(existingDate);
        user.onCreate();
        assertThat(user.getCreated_at()).isEqualTo(existingDate);
    }

    @Test
    void testNoArgsConstructor() {
        // Test the no-args constructor
        User emptyUser = new User();
        assertThat(emptyUser.getId()).isNull();
        assertThat(emptyUser.getUsername()).isNull();
        assertThat(emptyUser.getPassword()).isNull();
        assertThat(emptyUser.getEmail()).isNull();
        assertThat(emptyUser.getName()).isNull();
        assertThat(emptyUser.getRole()).isEqualTo("user");
        assertThat(emptyUser.getCreated_at()).isNull();
    }
}
