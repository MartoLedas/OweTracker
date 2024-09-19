package com.example.owetracker.controller;

import com.example.owetracker.model.Expense;
import com.example.owetracker.model.ExpensesView;
import com.example.owetracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.Map;
import java.util.List;


@RestController
@RequestMapping("/api/expenses")
public class ExpenseRestController {


    @Autowired
    private ExpenseService expenseService;

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateExpenseStatus(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        String newStatus = updates.get("status");

        try {
            expenseService.updateExpenseStatus(id, newStatus);  // Use the updated service method
            return ResponseEntity.ok("Status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update status");
        }
    }


    @GetMapping
    public List<ExpensesView> getExpensesForCurrentUser(HttpSession session) {
        // Retrieve userId from the session
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("User not logged in.");
        }

        // Fetch expenses for the logged-in user
        return expenseService.getExpensesForUser(userId);
    }

    // Delete expense endpoint
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        Expense expense = expenseService.getExpenseById(id);

        if (expense == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense not found");
        }

        // Check if the user is allowed to delete (if they created the expense)
        if (!expense.getPaidBy().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete expenses you created.");
        }

        // Delete expense from database
        expenseService.deleteExpense(id);
        return ResponseEntity.ok("Expense deleted successfully");
    }

    //@GetMapping
    //public List<ExpensesView> getAllExpenses() {
    //    return expenseService.getAllExpenses();
    //}
}