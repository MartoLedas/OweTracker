package com.example.owetracker.model;

import java.math.BigDecimal;

public class ExpenseGroupView {

    private String title;
    private String description;
    private BigDecimal amount;
    private String status;

    // Constructors, Getters, Setters
    public ExpenseGroupView() {}

    public ExpenseGroupView(String title, String description, BigDecimal amount, String status) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}