package com.example.owetracker.controller;

import com.example.owetracker.model.ExpensesView;
import com.example.owetracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseRestController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping
    public List<ExpensesView> getAllExpenses() {
        return expenseService.getAllExpenses();
    }
}