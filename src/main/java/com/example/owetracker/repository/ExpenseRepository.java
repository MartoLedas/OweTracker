package com.example.owetracker.repository;

import com.example.owetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByGroupId(Long groupId);

    List<Expense> findByPaidById(Long userId);

    List<Expense> findByStatus(String status);

    List<Expense> findByTitleContaining(String title);

}
