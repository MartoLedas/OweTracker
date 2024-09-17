package com.example.owetracker.controller;

import com.example.owetracker.model.Expense;
import com.example.owetracker.model.ExpenseUser;
import com.example.owetracker.model.ExpensesView;
import com.example.owetracker.model.User;
import com.example.owetracker.service.ExpenseService;
import com.example.owetracker.service.ExpenseUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.owetracker.service.UserService;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
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

//    @PostMapping("/create")
//    public String createExpense(
//            @ModelAttribute Expense expense,
//            @RequestParam List<Long> participantsIds,
//            @RequestParam List<BigDecimal> amountsOwed,
//            Model model) {
//
//        expenseService.createExpense(expense, participantsIds, amountsOwed);
//
//        return "redirect:/expenses/list";
//    }

    @PostMapping("/create")
    public String createExpense(
            @ModelAttribute Expense expense,
            @RequestParam List<Integer> participantsIds,
            @RequestParam List<BigDecimal> amountsOwed,
            @RequestParam(required = false) Boolean splitEqually,
            @RequestParam(required = false) BigDecimal totalAmount,
            Model model) {

        expenseService.createExpense(expense, participantsIds, amountsOwed, Boolean.TRUE.equals(splitEqually), totalAmount);

        return "redirect:/expenses/list";
    }


    @PostMapping("/save")
    public String saveExpense(
            HttpSession session,
            //@RequestParam Integer ownerId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) Boolean splitEqually,
            @RequestParam(required = false) BigDecimal totalAmount,
            @RequestParam List<Integer> users,  // User IDs
            @RequestParam(required = false) List<BigDecimal> userAmounts  // Custom amounts per user
            ) {

        Integer ownerId = (Integer) session.getAttribute("userId");

        Expense expense = new Expense();
        expense.setTitle(title);
        expense.setDescription(description);
        expense.setPaidBy(userService.findById(ownerId));  // Logged-in user
        expense.setStatus("pending");
        expense.setCreatedAt(LocalDateTime.now());

        Expense savedExpense = expenseService.save(expense);

        BigDecimal calculatedTotalAmount = BigDecimal.ZERO;

        if (Boolean.TRUE.equals(splitEqually)) {
            if (totalAmount == null) {
                throw new IllegalArgumentException("Total amount is required when splitting equally");
            }
            BigDecimal equalAmount = totalAmount.divide(BigDecimal.valueOf(users.size()), 2, BigDecimal.ROUND_HALF_UP);
            savedExpense.setAmount(totalAmount);

            for (Integer userId : users) {
                ExpenseUser expenseUser = new ExpenseUser(savedExpense, userService.findById(userId), equalAmount, "pending");
                expenseUserService.save(expenseUser);
            }
        } else {
            if (userAmounts == null || userAmounts.size() != users.size()) {
                throw new IllegalArgumentException("Custom amounts must be provided for all users");
            }

            for (int i = 0; i < users.size(); i++) {
                BigDecimal userAmount = userAmounts.get(i);
                calculatedTotalAmount = calculatedTotalAmount.add(userAmount);

                ExpenseUser expenseUser = new ExpenseUser(savedExpense, userService.findById(users.get(i)), userAmount, "pending");
                expenseUserService.save(expenseUser);
            }

            savedExpense.setAmount(calculatedTotalAmount);
            expenseService.save(savedExpense);
        }

        return "redirect:/home"; //change to "redirect:/expenses" later or create success message page
    }


    @GetMapping("/list")
    public String listExpenses(Model model) {
        List<ExpensesView> expenses = expenseService.getAllExpenses();
        model.addAttribute("expenses", expenses);
        return "expensesview";
    }

    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id);
    }


    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
    }

}


