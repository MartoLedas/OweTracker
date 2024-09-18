package com.example.owetracker.controller;

import com.example.owetracker.model.ExpensesView;
import com.example.owetracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseRestController {

    @Autowired
    private ExpenseService expenseService;

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

    //@GetMapping
    //public List<ExpensesView> getAllExpenses() {
    //    return expenseService.getAllExpenses();
    //}
}