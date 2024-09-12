package com.example.owetracker.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "expense_users")
public class ExpenseUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    private User user;

    @Column(name = "amount", nullable = false)
    private BigDecimal amountOwed;

    @Column(name = "status", nullable = false)
    private String status = "pending"; // Can be "pending" or "paid"


    public ExpenseUser() {
    }

    public ExpenseUser(Expense expense, User user, BigDecimal amountOwed, String status) {
        this.expense = expense;
        this.user = user;
        this.amountOwed = amountOwed;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getAmountOwed() {
        return amountOwed;
    }

    public void setAmountOwed(BigDecimal amountOwed) {
        this.amountOwed = amountOwed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
