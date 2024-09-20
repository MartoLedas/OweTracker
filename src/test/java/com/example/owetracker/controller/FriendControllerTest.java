package com.example.owetracker.controller;

import com.example.owetracker.model.User;
import com.example.owetracker.service.FriendService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FriendControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FriendService friendService;

    @InjectMocks
    private FriendController friendController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(friendController).build();
    }

    @Test
    void testAddFriend() throws Exception {
        // Create a mock session and set the userId attribute
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1);

        Map<String, Integer> payload = Map.of("friendId", 2);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/friends/add")
                        .contentType("application/json")
                        .content("{\"friendId\": 2}")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend added successfully"));

        verify(friendService, times(1)).addFriendRequest(1, 2);
    }

    @Test
    void testAddFriendWithoutSession() throws Exception {
        Map<String, Integer> payload = Map.of("friendId", 2);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/friends/add")
                        .contentType("application/json")
                        .content("{\"friendId\": 2}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User not logged in"));

        verify(friendService, never()).addFriendRequest(anyInt(), anyInt());
    }

    @Test
    void testGetUserFriends() throws Exception {
        // Create a mock session and set the userId attribute
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1);

        User user = new User();
        user.setId(2);
        user.setUsername("frienduser");

        when(friendService.getUserFriends(1)).thenReturn(List.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/friends/list")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("frienduser"));

        verify(friendService, times(1)).getUserFriends(1);
    }
}
