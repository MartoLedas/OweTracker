package com.example.owetracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "friends")
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "friend_id", nullable = false)
    private Integer friendId;

    @Column(name = "status", nullable = false)  // New column
    private String status;  // e.g., "PENDING", "ACCEPTED", "DECLINED"

    // Constructor
    public Friend() {}

    public Friend(Integer userId, Integer friendId, String status) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
    }

    // Getters and Setters (include status getter and setter)
    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }

    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getFriendId() { return friendId; }

    public void setFriendId(Integer friendId) { this.friendId = friendId; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}

