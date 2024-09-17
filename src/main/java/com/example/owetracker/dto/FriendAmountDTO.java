package com.example.owetracker.dto;

import java.math.BigDecimal;

public class FriendAmountDTO {

    private Integer friendId;
    private String friendUsername;
    private BigDecimal totalOwedToFriend;
    private BigDecimal totalOwedByFriend;

    // Constructors, getters, and setters

    public FriendAmountDTO(Integer friendId, String friendUsername, BigDecimal totalOwedToFriend, BigDecimal totalOwedByFriend) {
        this.friendId = friendId;
        this.friendUsername = friendUsername;
        this.totalOwedToFriend = totalOwedToFriend;
        this.totalOwedByFriend = totalOwedByFriend;
    }

    public Integer getFriendId() {
        return friendId;
    }

    public void setFriendId(Integer friendId) {
        this.friendId = friendId;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public BigDecimal getTotalOwedToFriend() {
        return totalOwedToFriend;
    }

    public void setTotalOwedToFriend(BigDecimal totalOwedToFriend) {
        this.totalOwedToFriend = totalOwedToFriend;
    }

    public BigDecimal getTotalOwedByFriend() {
        return totalOwedByFriend;
    }

    public void setTotalOwedByFriend(BigDecimal totalOwedByFriend) {
        this.totalOwedByFriend = totalOwedByFriend;
    }
}
