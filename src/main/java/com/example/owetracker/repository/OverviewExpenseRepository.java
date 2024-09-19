package com.example.owetracker.repository;

import com.example.owetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface OverviewExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.paidBy.id = :userId AND e.status = 'pending'")
    BigDecimal getTotalOwedToUser(@Param("userId") Long userId);
}
