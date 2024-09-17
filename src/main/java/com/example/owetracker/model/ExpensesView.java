package com.example.owetracker.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExpensesView {
    private LocalDateTime createdAt;
    private String title;
    private String description;
    private BigDecimal amount;
    private String paidBy;
    private String status;
    private Long id;

    // Constructor
    public ExpensesView(LocalDateTime createdAt, String title, String description, BigDecimal amount, String paidBy, String status, Long id) {
        this.createdAt = createdAt;
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.paidBy = paidBy;
        this.status = status;
        this.id = id;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
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
}