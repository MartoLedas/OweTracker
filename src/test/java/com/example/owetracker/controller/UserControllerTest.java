package com.example.owetracker.controller;

import com.example.owetracker.model.User;
import com.example.owetracker.model.UserProfileUpdateRequest;
import com.example.owetracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.RedirectView;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetUserById() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        when(userService.getUserById(1)).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testGetUserByUsername() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testRegisterUser() throws Exception {
        User user = new User();
        user.setUsername("testuser");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType("application/json")
                        .content("{\"username\": \"testuser\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));

        verify(userService, times(1)).registerUser(any(User.class));
    }

    @Test
    void testLoginUser() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(userService.validateUserLogin("testuser", "password")).thenReturn(true);
        when(userService.getUserByUsername("testuser")).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                        .contentType("application/json")
                        .content("{\"username\": \"testuser\", \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));

        verify(userService, times(1)).validateUserLogin("testuser", "password");
    }

    @Test
    void testLogoutUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testUpdateUserProfile() throws Exception {
        // Create a mock session and set the userId attribute
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1);

        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();
        updateRequest.setName("New Name");
        updateRequest.setEmail("newemail@example.com");

        // Mock the service method call
        doNothing().when(userService).updateUserProfile(anyInt(), any(UserProfileUpdateRequest.class));
        // Perform the update request with the mock session
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New Name\", \"email\": \"newemail@example.com\", \"currentPassword\": \"password\", \"newPassword\": \"newpassword\"}")
                        .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Profile updated successfully"));

        // Verify that updateUserProfile was called with the correct arguments
        verify(userService).updateUserProfile(anyInt(), any(UserProfileUpdateRequest.class));
    }



    @Test
    void testSearchUsers() throws Exception {
        User user = new User();
        user.setUsername("testuser");

        when(userService.searchUsers("test")).thenReturn(List.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/search")
                        .param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }
}
