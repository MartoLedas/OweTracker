package com.example.owetracker.repository;

import com.example.owetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByGroupId(Integer groupId);

    List<Expense> findByPaidById(Integer userId);

    List<Expense> findByStatus(String status);

    List<Expense> findByTitleContaining(String title);

   // List<Expense> findByUserId(Integer userId);

}