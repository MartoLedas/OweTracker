package com.example.owetracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private double totalAmount;

    private LocalDate date;

    @ManyToMany
    @JoinTable(
            name = "expense_user",
            joinColumns = @JoinColumn(name = "expense_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants;

    @ManyToOne
    @JoinColumn(name = "paid_by")
    private User paidBy;

    @ElementCollection
    @CollectionTable(name = "expense_splits", joinColumns = @JoinColumn(name = "expense_id"))
    @Column(name = "amount")
    private List<Double> amountsOwed;

    public Expense() {

    }

    public Expense(String description, double totalAmount, LocalDate date, List<User> participants, User paidBy, List<Double> amountsOwed) {
        this.description = description;
        this.totalAmount = totalAmount;
        this.date = date;
        this.participants = participants;
        this.paidBy = paidBy;
        this.amountsOwed = amountsOwed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public User getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(User paidBy) {
        this.paidBy = paidBy;
    }

    public List<Double> getAmountsOwed() {
        return amountsOwed;
    }

    public void setAmountsOwed(List<Double> amountsOwed) {
        this.amountsOwed = amountsOwed;
    }
}
