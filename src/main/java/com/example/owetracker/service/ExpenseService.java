package com.example.owetracker.service;

import com.example.owetracker.model.Expense;
import com.example.owetracker.model.ExpenseUser;
import com.example.owetracker.model.ExpensesView;
import com.example.owetracker.repository.ExpenseRepository;
import com.example.owetracker.repository.ExpenseUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.owetracker.model.User;
import com.example.owetracker.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Transactional
    public Expense createExpense(Expense expense, List<Integer> participantIds, List<BigDecimal> amountsOwed, boolean splitEqually, BigDecimal totalAmount) {
        expense.setCreatedAt(LocalDateTime.now());
        expense.setStatus("pending");
        Expense savedExpense = expenseRepository.save(expense);

        if (splitEqually) {
            BigDecimal equalAmount = totalAmount.divide(BigDecimal.valueOf(participantIds.size()));
            for (Integer userId : participantIds) {
                User participant = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
                ExpenseUser expenseUser = new ExpenseUser(savedExpense, participant, equalAmount, "pending");
                expenseUserRepository.save(expenseUser);
            }
        } else {
            for (int i = 0; i < participantIds.size(); i++) {
                Integer userId = participantIds.get(i);
                BigDecimal amountOwed = amountsOwed.get(i);

                User participant = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

                ExpenseUser expenseUser = new ExpenseUser(savedExpense, participant, amountOwed, "pending");
                expenseUserRepository.save(expenseUser);
            }
        }

        return savedExpense;
    }


    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense getExpenseById(Long id) {
        Optional<Expense> expense = expenseRepository.findById(id);
        return expense.orElse(null);
    }

    public Expense save(Expense expense) {
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


    public List<Expense> getExpensesByGroupId(Integer groupId) {
        return expenseRepository.findByGroupId(groupId);
    }

    public List<Expense> getExpensesPaidByUser(Integer userId) {
        return expenseRepository.findByPaidById(userId);
    }

    public List<Expense> getExpensesByStatus(String status) {
        return expenseRepository.findByStatus(status);
    }

    public List<Expense> findByGroupId(Integer groupId) {
        return expenseRepository.findByGroupId(groupId);
    }

    public List<ExpensesView> getExpensesForUser(Integer userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Expense> expensesCreatedByUser = expenseRepository.findByPaidById(currentUser.getId());
        List<ExpenseUser> expensesWhereUserMustPay = expenseUserRepository.findByUserId(currentUser.getId().longValue());
        List<Expense> allRelevantExpenses = new ArrayList<>(expensesCreatedByUser);
        allRelevantExpenses.addAll(
                expensesWhereUserMustPay.stream()
                        .map(ExpenseUser::getExpense)  // Convert ExpenseUser to Expense
                        .collect(Collectors.toList())
        );

        allRelevantExpenses.sort(Comparator.comparing(Expense::getCreatedAt).reversed());
        return allRelevantExpenses.stream().map(expense -> {
            String paymentFromTo = "N/A";
            boolean createdByUser = expense.getPaidBy().equals(currentUser);
            String formattedAmount;

            if (createdByUser) {
                formattedAmount = "+" + expense.getAmount().toString();
                List<ExpenseUser> participants = expenseUserRepository.findByExpenseId(expense.getId());
                if (!participants.isEmpty()) {
                    paymentFromTo = participants.get(0).getUser().getName();  // Get the first participant's name
                }
            } else {
                formattedAmount = "-" + expense.getAmount().toString();
                paymentFromTo = expense.getPaidBy().getName();
            }
            return new ExpensesView(
                    expense.getCreatedAt(),
                    expense.getTitle(),
                    expense.getDescription(),
                    formattedAmount,
                    paymentFromTo,
                    expense.getStatus(),
                    expense.getId(),
                    createdByUser
            );
        }).collect(Collectors.toList());
    }

}