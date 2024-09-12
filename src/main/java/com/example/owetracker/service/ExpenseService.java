package com.example.owetracker.service;

import com.example.owetracker.model.Expense;
import com.example.owetracker.model.ExpenseUser;
import com.example.owetracker.repository.ExpenseRepository;
import com.example.owetracker.repository.ExpenseUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.owetracker.model.User;
import com.example.owetracker.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseUserRepository expenseUserRepository;

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense getExpenseById(Long id) {
        Optional<Expense> expense = expenseRepository.findById(id);
        return expense.orElse(null);
    }

    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public Expense updateExpense(Long id, Expense updatedExpense) {
        Optional<Expense> existingExpense = expenseRepository.findById(id);

        if (existingExpense.isPresent()) {
            Expense expense = existingExpense.get();
            expense.setTitle(updatedExpense.getTitle());
            expense.setDescription(updatedExpense.getDescription());
            expense.setAmount(updatedExpense.getAmount());
            expense.setPaidBy(updatedExpense.getPaidBy());
            expense.setGroupId(updatedExpense.getGroupId());
            expense.setStatus(updatedExpense.getStatus());
            return expenseRepository.save(expense);
        } else {
            return null;
        }
    }

    public void deleteExpense(Long id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
        }
    }

    public Expense settleExpense(Long expenseId, Long userId) {
        Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);

        if (optionalExpense.isPresent()) {
            Expense expense = optionalExpense.get();

            Optional<ExpenseUser> expenseUserOpt = expenseUserRepository.findByExpenseIdAndUserId(expenseId, userId);

            if (expenseUserOpt.isPresent()) {
                ExpenseUser expenseUser = expenseUserOpt.get();

                if (expenseUser.getStatus().equalsIgnoreCase("paid")) {
                    return expense;
                }

                expenseUser.setStatus("paid");
                expenseUserRepository.save(expenseUser);

                List<ExpenseUser> participants = expenseUserRepository.findByExpenseId(expenseId);
                boolean allPaid = participants.stream()
                        .allMatch(eu -> eu.getStatus().equalsIgnoreCase("paid"));

                if (allPaid) {
                    expense.setStatus("paid");
                    return expenseRepository.save(expense);
                }

                return expense;
            } else {
                throw new IllegalArgumentException("User is not a participant in this expense.");
            }
        } else {
            return null;
        }
    }


    public List<Expense> getExpensesByGroupId(Long groupId) {
        return expenseRepository.findByGroupId(groupId);
    }

    public List<Expense> getExpensesPaidByUser(Long userId) {
        return expenseRepository.findByPaidById(userId);
    }

    public List<Expense> getExpensesByStatus(String status) {
        return expenseRepository.findByStatus(status);
    }
}