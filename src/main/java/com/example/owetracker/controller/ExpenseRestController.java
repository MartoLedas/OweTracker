package com.example.owetracker.controller;

import com.example.owetracker.model.Expense;
import com.example.owetracker.model.ExpensesView;
import com.example.owetracker.model.ExpenseGroupView;
import com.example.owetracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseRestController {

    @Autowired
    private ExpenseService expenseService;

    // Update status of an expense
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateExpenseStatus(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        String newStatus = updates.get("status");

        try {
            expenseService.updateExpenseStatus(id, newStatus);
            return ResponseEntity.ok("Status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update status");
        }
    }

    // Get all expenses for the logged-in user
    @GetMapping
    public ResponseEntity<List<ExpensesView>> getExpensesForCurrentUser(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<ExpensesView> expenses = expenseService.getExpensesForUser(userId);

        if (expenses.isEmpty()) {
            return ResponseEntity.noContent().build(); // Returns 204 No Content if no expenses found
        }

        return ResponseEntity.ok(expenses);
    }

    // Correct version of the grouped expenses method (only one version now)
    @GetMapping("/grouped")
    public ResponseEntity<List<ExpenseGroupView>> getGroupedExpenses(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // Fetch grouped expenses for the logged-in user
        List<ExpenseGroupView> groupedExpenses = expenseService.getGroupedExpensesForUser(userId);

        if (groupedExpenses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(groupedExpenses);
    }

    // Delete expense endpoint
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        Expense expense = expenseService.getExpenseById(id);

        if (expense == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense not found");
        }

        // Check if the user is allowed to delete the expense
        if (!expense.getPaidBy().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete expenses you created.");
        }

        // Delete the expense
        expenseService.deleteExpense(id);
        return ResponseEntity.ok("Expense deleted successfully");
    }
}