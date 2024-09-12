package com.example.owetracker.controller;

import com.example.owetracker.model.Expense;
import com.example.owetracker.model.ExpenseUser;
import com.example.owetracker.service.ExpenseService;
import com.example.owetracker.service.ExpenseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.owetracker.service.UserService;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @Autowired
    private ExpenseUserService expenseUserService;


    @GetMapping("/create")
    public String showCreateExpenseForm(Model model) {
        model.addAttribute("expense", new Expense());
        model.addAttribute("allUsers", userService.getAllUsers());
        return "create-expense";
    }

    @PostMapping("/create")
    public String createExpense(
            @ModelAttribute Expense expense,
            @RequestParam List<Long> participantsIds,
            @RequestParam List<BigDecimal> amountsOwed,
            Model model) {

        expenseService.createExpense(expense, participantsIds, amountsOwed);

        return "redirect:/expenses/list";
    }

    @PostMapping("/expenses/save")
    public String saveExpense(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) Boolean splitEqually,
            @RequestParam(required = false) BigDecimal totalAmount,
            @RequestParam List<Integer> users,  // User IDs
            @RequestParam(required = false) List<BigDecimal> userAmounts,  // Custom amounts per user
            @RequestParam Integer ownerId) {

        Expense expense = new Expense();
        expense.setTitle(title);
        expense.setDescription(description);
        expense.setPaidBy(userService.findById(ownerId));  // Logged-in user
        expense.setStatus("pending");

        Expense savedExpense = expenseService.save(expense);

        if (Boolean.TRUE.equals(splitEqually)) {
            BigDecimal equalAmount = totalAmount.divide(BigDecimal.valueOf(users.size()));
            for (Integer userId : users) {
                ExpenseUser expenseUser = new ExpenseUser(savedExpense, userService.findById(userId), equalAmount, "pending");
                expenseUserService.save(expenseUser);
            }
        } else {
            for (int i = 0; i < users.size(); i++) {
                ExpenseUser expenseUser = new ExpenseUser(savedExpense, userService.findById(users.get(i)), userAmounts.get(i), "pending");
                expenseUserService.save(expenseUser);
            }
        }

        return "redirect:/expenses";
    }



    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id);
    }

    @PostMapping
    public Expense createExpense(@RequestBody Expense expense) {
        return expenseService.createExpense(expense);
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
    }
}
