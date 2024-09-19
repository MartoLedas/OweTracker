package com.example.owetracker.repository;

import com.example.owetracker.model.ExpenseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface OverviewExpenseUserRepository extends JpaRepository<ExpenseUser, Long> {

    @Query("SELECT SUM(eu.amountOwed) FROM ExpenseUser eu WHERE eu.user.id = :userId AND eu.status = 'pending'")
    BigDecimal getTotalAmountOwedByUser(@Param("userId") Long userId);
}

