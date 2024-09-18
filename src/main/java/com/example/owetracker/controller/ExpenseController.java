package com.example.owetracker.controller;

import com.example.owetracker.model.*;
import com.example.owetracker.repository.FriendRepository;
import com.example.owetracker.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    private HttpSession session;
    @Autowired
    private FriendService friendService;
    @Autowired
    private GroupService groupService;

    @GetMapping("/create")
    public String showCreateExpenseForm(Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("User not logged in.");
        }

        model.addAttribute("expense", new Expense());
        model.addAttribute("friends", friendService.getUserFriends(userId));
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


    //@GetMapping("/list")
    //public String listExpenses(Model model) {
        //List<ExpensesView> expenses = expenseService.getAllExpenses();
        //model.addAttribute("expenses", expenses);
        //return "expensesview";
    //}

    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
    }

    @GetMapping("/my-expenses")
    public List<ExpensesView> getMyExpenses() {
        // Retrieve the user ID from the session
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("User not logged in.");
        }
        // Pass the user ID to the service layer to get filtered expenses
        return expenseService.getExpensesForUser(userId);
    }

    @GetMapping("/create-group-expense")
    public String showCreateGroupExpenseForm(Model model) {

        Integer userId = (Integer) session.getAttribute("userId");

        List<Group> userGroups = groupService.findGroupsByUserId(userId);

        model.addAttribute("groups", userGroups);
        model.addAttribute("expense", new Expense());

        return "create-group-expense";
    }

    @PostMapping("/save-group-expense")
    public String saveGroupExpense(
            HttpSession session,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) Boolean splitEqually,
            @RequestParam(required = false) BigDecimal totalAmount,
            @RequestParam Integer groupId,  // Group ID
            @RequestParam List<Integer> memberIds,  // User IDs (members of the group)
            @RequestParam(required = false) List<BigDecimal> memberAmounts  // Custom amounts per member
    ) {

        // Get the current logged-in user
        Integer ownerId = (Integer) session.getAttribute("userId");

        // Create the expense entity
        Expense expense = new Expense();
        expense.setTitle(title);
        expense.setDescription(description);
        expense.setPaidBy(userService.findById(ownerId));  // Logged-in user
        expense.setStatus("pending");
        expense.setCreatedAt(LocalDateTime.now());
        expense.setGroupId(groupId);  // Set the group ID

        // Save the expense in the database
        Expense savedExpense = expenseService.save(expense);

        BigDecimal calculatedTotalAmount = BigDecimal.ZERO;

        // Handling split equally scenario
        if (Boolean.TRUE.equals(splitEqually)) {
            if (totalAmount == null) {
                throw new IllegalArgumentException("Total amount is required when splitting equally");
            }

            // Calculate equal share for each member
            BigDecimal equalAmount = totalAmount.divide(BigDecimal.valueOf(memberIds.size()), 2, BigDecimal.ROUND_HALF_UP);
            savedExpense.setAmount(totalAmount);

            // Save each member's share
            for (Integer memberId : memberIds) {
                ExpenseUser expenseUser = new ExpenseUser(savedExpense, userService.findById(memberId), equalAmount, "pending");
                expenseUserService.save(expenseUser);
            }
        } else {
            // Custom amounts scenario
            if (memberAmounts == null || memberAmounts.size() != memberIds.size()) {
                throw new IllegalArgumentException("Custom amounts must be provided for all group members");
            }

            // Calculate and assign each member's custom amount
            for (int i = 0; i < memberIds.size(); i++) {
                BigDecimal memberAmount = memberAmounts.get(i);
                calculatedTotalAmount = calculatedTotalAmount.add(memberAmount);

                // Save each member's specific share
                ExpenseUser expenseUser = new ExpenseUser(savedExpense, userService.findById(memberIds.get(i)), memberAmount, "pending");
                expenseUserService.save(expenseUser);
            }

            // Set the total calculated amount for the expense
            savedExpense.setAmount(calculatedTotalAmount);
            expenseService.save(savedExpense);  // Save the updated amount
        }

        return "redirect:/expensesview";  // or "redirect:/expenses" based on your flow
    }








}
