package com.example.owetracker.service;

import com.example.owetracker.model.ExpenseUser;
import com.example.owetracker.repository.ExpenseUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseUserService {

    @Autowired
    private ExpenseUserRepository expenseUserRepository;

    public ExpenseUser save(ExpenseUser expenseUser) {

        return expenseUserRepository.save(expenseUser);
    }

    public List<ExpenseUser> findByExpenseId(Long expenseId) {
        return expenseUserRepository.findByExpenseId(expenseId);
    }

    public Optional<ExpenseUser> findByExpenseIdAndUserId(Long expenseId, Long userId) {
        return expenseUserRepository.findByExpenseIdAndUserId(expenseId, userId);
    }

}
