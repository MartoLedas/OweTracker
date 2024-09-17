package com.example.owetracker.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExpensesView {
    private LocalDateTime createdAt;
    private String title;
    private String description;
    private String amount; // Change from BigDecimal to String
    private String paidBy;
    private String status;
    private Long id;
    private boolean createdByUser; // Added this field

    // Updated constructor to accept createdByUser
    public ExpensesView(LocalDateTime createdAt, String title, String description, String amount, String paidBy, String status, Long id, boolean createdByUser) {
        this.createdAt = createdAt;
        this.title = title;
        this.description = description;
        this.amount = amount; // Store amount as String for formatted value
        this.paidBy = paidBy;
        this.status = status;
        this.id = id;
        this.createdByUser = createdByUser; // Initialize createdByUser
    }

    // Getters and Setters for all fields
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter and setter for createdByUser
    public boolean isCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(boolean createdByUser) {
        this.createdByUser = createdByUser;
    }
}