package com.example.owetracker.service;

import com.example.owetracker.model.Group;
import com.example.owetracker.model.Expense;
import com.example.owetracker.model.ExpenseUser;
import com.example.owetracker.model.ExpensesView;
import com.example.owetracker.model.ExpenseGroupView;
import com.example.owetracker.repository.GroupRepository;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseUserRepository expenseUserRepository;

    @Autowired
    private GroupRepository groupRepository;

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

    public List<ExpenseGroupView> getGroupedExpensesForUser(Integer userId) {
        List<Expense> allExpenses = expenseRepository.findByPaidById(userId);

        // Map for aggregating group expenses
        Map<Integer, ExpenseGroupView> groupExpenseMap = new HashMap<>();

        for (Expense expense : allExpenses) {
            Integer groupId = expense.getGroupId();
            if (groupId != null) {  // This is a group expense
                // If group ID is not yet processed, add it
                if (!groupExpenseMap.containsKey(groupId)) {
                    Group group = groupRepository.findById(groupId)
                            .orElseThrow(() -> new RuntimeException("Group not found"));

                    // Only process if group was created by the logged-in user
                    if (group.getCreatedBy().equals(userId)) {
                        ExpenseGroupView groupView = new ExpenseGroupView();
                        groupView.setTitle(group.getTitle()); // Group title
                        groupView.setDescription(expense.getDescription()); // Take description from any record
                        groupView.setAmount(expense.getAmount());  // Set amount directly
                        groupView.setStatus(expense.getStatus());  // Set group status
                        groupExpenseMap.put(groupId, groupView);
                    }
                }
            }
        }

        return new ArrayList<>(groupExpenseMap.values());
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

        // Group expenses by group_id and accumulate amounts
        Map<Integer, ExpensesView> groupedExpenses = new HashMap<>();

        // List to collect all expense views (individual and group)
        List<ExpensesView> expenseViews = new ArrayList<>();

        for (Expense expense : allRelevantExpenses) {
            String paymentFromTo = "N/A";
            boolean createdByUser = expense.getPaidBy().equals(currentUser);
            String formattedAmount;

            // Handle null amount gracefully
            BigDecimal amount = expense.getAmount();
            formattedAmount = (amount != null ? (createdByUser ? "+" + amount.toString() : "-" + amount.toString()) : "N/A");

            if (expense.getGroupId() != null && expense.getAmount() != null) {
                // This is a group expense
                Group group = groupRepository.findById(expense.getGroupId())
                        .orElseThrow(() -> new RuntimeException("Group not found"));

                // Only show group expenses for the group creator
                if (group.getCreatedBy().equals(userId)) {
                    // Check if this group is already processed
                    if (!groupedExpenses.containsKey(expense.getGroupId())) {
                        // First time seeing this group, create a new entry
                        ExpensesView groupView = new ExpensesView(
                                expense.getCreatedAt(),
                                expense.getTitle(),
                                expense.getDescription(),
                                formattedAmount,
                                "<b>" + group.getTitle() + "</b>", // Group title in bold
                                expense.getStatus(),
                                expense.getId(),
                                createdByUser
                        );
                        groupedExpenses.put(expense.getGroupId(), groupView);
                    }
                }
            } else {
                // This is an individual expense
                if (createdByUser) {
                    List<ExpenseUser> participants = expenseUserRepository.findByExpenseId(expense.getId());
                    if (!participants.isEmpty()) {
                        paymentFromTo = participants.get(0).getUser().getName();  // Get the first participant's name
                    }
                } else {
                    paymentFromTo = expense.getPaidBy().getName();
                }

                ExpensesView individualView = new ExpensesView(
                        expense.getCreatedAt(),
                        expense.getTitle(),
                        expense.getDescription(),
                        formattedAmount,
                        paymentFromTo,
                        expense.getStatus(),
                        expense.getId(),
                        createdByUser
                );

                // Add individual expenses directly
                expenseViews.add(individualView);
            }
        }

        // Add all group expenses to the final list
        expenseViews.addAll(groupedExpenses.values());

        // Sort the final list by creation date, descending
        expenseViews.sort(Comparator.comparing(ExpensesView::getCreatedAt).reversed());

        return expenseViews;
    }

    public void updateExpenseStatus(Long expenseId, String newStatus) {
        // Update the status in the 'expenses' table
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("Expense not found"));
        expense.setStatus(newStatus);
        expenseRepository.save(expense);

        // Update the status in the 'expense_users' table for the same expense
        List<ExpenseUser> expenseUsers = expenseUserRepository.findByExpenseId(expenseId);
        for (ExpenseUser expenseUser : expenseUsers) {
            expenseUser.setStatus(newStatus);  // Assuming you want to set the same status for each user
            expenseUserRepository.save(expenseUser);
        }
    }
}