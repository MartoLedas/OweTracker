package com.example.owetracker.repository;

import com.example.owetracker.model.ExpenseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface FriendAmountRepository extends JpaRepository<ExpenseUser, Integer> {

    @Query("SELECT COALESCE(SUM(eu.amountOwed), 0) FROM ExpenseUser eu WHERE eu.user.id = :userId AND eu.expense.paidBy.id = :friendId AND eu.status != 'paid'")
    BigDecimal findTotalOwedToFriend(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

    @Query("SELECT COALESCE(SUM(eu.amountOwed), 0) FROM ExpenseUser eu WHERE eu.user.id = :friendId AND eu.expense.paidBy.id = :userId AND eu.status!= 'paid'")
    BigDecimal findTotalOwedByFriend(@Param("userId") Integer userId, @Param("friendId") Integer friendId);
}