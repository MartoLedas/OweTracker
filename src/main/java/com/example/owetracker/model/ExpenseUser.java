package com.example.owetracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "expense_users")
public class ExpenseUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "expense_id")
    private Expense expense;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private double amountOwed;

    // need getters, setters
}
