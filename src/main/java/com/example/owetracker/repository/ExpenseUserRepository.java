package com.example.owetracker.repository;

import com.example.owetracker.model.ExpenseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseUserRepository extends JpaRepository<ExpenseUser, Long> {

    Optional<ExpenseUser> findByExpenseIdAndUserId(Long expenseId, Long userId);

    List<ExpenseUser> findByExpenseId(Long expenseId);
}
