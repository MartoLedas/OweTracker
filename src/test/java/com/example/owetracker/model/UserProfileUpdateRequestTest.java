package com.example.owetracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserProfileUpdateRequestTest {

    private UserProfileUpdateRequest request;

    @BeforeEach
    void setUp() {
        // Initialize a UserProfileUpdateRequest instance
        request = new UserProfileUpdateRequest();
    }

    @Test
    void testNameSetterAndGetter() {
        // Test the name field
        String name = "John Doe";
        request.setName(name);
        assertThat(request.getName()).isEqualTo(name);
    }

    @Test
    void testEmailSetterAndGetter() {
        // Test the email field
        String email = "john.doe@example.com";
        request.setEmail(email);
        assertThat(request.getEmail()).isEqualTo(email);
    }

    @Test
    void testCurrentPasswordSetterAndGetter() {
        // Test the currentPassword field
        String currentPassword = "oldPassword";
        request.setCurrentPassword(currentPassword);
        assertThat(request.getCurrentPassword()).isEqualTo(currentPassword);
    }

    @Test
    void testNewPasswordSetterAndGetter() {
        // Test the newPassword field
        String newPassword = "newPassword";
        request.setNewPassword(newPassword);
        assertThat(request.getNewPassword()).isEqualTo(newPassword);
    }
}
