package com.example.owetracker.service;

import com.example.owetracker.model.User;
import com.example.owetracker.model.UserProfileUpdateRequest;
import com.example.owetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void testFindByIdThrowsExceptionWhenUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.findById(1);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetUserByUsername() {
        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(user);

        User result = userService.getUserByUsername("testuser");

        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testRegisterUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        when(userRepository.findByUsername("testuser")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerUser(user);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
        assertNotNull(result.getCreatedAt());
        verify(userRepository).save(result);
    }

    @Test
    void testRegisterUserThrowsExceptionWhenUsernameTaken() {
        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(new User());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(user);
        });

        assertEquals("Username is already taken", exception.getMessage());
    }

    @Test
    void testValidateUserLoginSuccess() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        boolean result = userService.validateUserLogin("testuser", "password");

        assertTrue(result);
    }

    @Test
    void testValidateUserLoginFailure() {
        when(userRepository.findByUsername("testuser")).thenReturn(null);

        boolean result = userService.validateUserLogin("testuser", "password");

        assertFalse(result);
    }

    @Test
    void testUpdateUserProfile() {
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setPassword("encodedOldPassword");
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");

        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();
        updateRequest.setName("New Name");
        updateRequest.setEmail("new@example.com");
        updateRequest.setCurrentPassword("oldPassword");
        updateRequest.setNewPassword("newPassword");

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        userService.updateUserProfile(1, updateRequest);

        assertEquals("New Name", existingUser.getName());
        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals("encodedNewPassword", existingUser.getPassword());
        verify(userRepository).save(existingUser);
    }

    @Test
    void testUpdateUserProfileThrowsExceptionWhenUserNotFound() {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUserProfile(1, updateRequest);
        });

        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void testSearchUsers() {
        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsernameContainingIgnoreCase("test")).thenReturn(List.of(user));

        List<User> result = userService.searchUsers("test");

        assertFalse(result.isEmpty());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    void testGetAllUsers() {
        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.getAllUsers();

        assertFalse(result.isEmpty());
        assertEquals("testuser", result.get(0).getUsername());
    }
}
