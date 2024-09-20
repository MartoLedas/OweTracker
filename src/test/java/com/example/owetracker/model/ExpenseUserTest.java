package com.example.owetracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpenseUserTest {

    private ExpenseUser expenseUser;
    private Expense expense;
    private User user;

    @BeforeEach
    void setUp() {
        // Create dummy Expense and User instances
        expense = new Expense();
        expense.setId(1L);
        expense.setTitle("Test Expense");

        user = new User();
        user.setId(1);
        user.setUsername("testuser");

        // Initialize ExpenseUser with mock data
        expenseUser = new ExpenseUser(expense, user, new BigDecimal("50.00"), "pending");
    }

    @Test
    void testExpenseUserConstructor() {
        // Validate the constructor sets values correctly
        assertThat(expenseUser.getExpense()).isEqualTo(expense);
        assertThat(expenseUser.getUser()).isEqualTo(user);
        assertThat(expenseUser.getAmountOwed()).isEqualByComparingTo("50.00");
        assertThat(expenseUser.getStatus()).isEqualTo("pending");
    }

    @Test
    void testSettersAndGetters() {
        // Test setting new values
        Expense newExpense = new Expense();
        newExpense.setId(2L);
        newExpense.setTitle("New Expense");

        User newUser = new User();
        newUser.setId(2);
        newUser.setUsername("newuser");

        expenseUser.setExpense(newExpense);
        expenseUser.setUser(newUser);
        expenseUser.setAmountOwed(new BigDecimal("100.00"));
        expenseUser.setStatus("paid");

        // Validate the getters return updated values
        assertThat(expenseUser.getExpense()).isEqualTo(newExpense);
        assertThat(expenseUser.getUser()).isEqualTo(newUser);
        assertThat(expenseUser.getAmountOwed()).isEqualByComparingTo("100.00");
        assertThat(expenseUser.getStatus()).isEqualTo("paid");
    }

    @Test
    void testNoArgsConstructor() {
        // Test using no-args constructor
        ExpenseUser emptyExpenseUser = new ExpenseUser();

        assertThat(emptyExpenseUser.getExpense()).isNull();
        assertThat(emptyExpenseUser.getUser()).isNull();
        assertThat(emptyExpenseUser.getAmountOwed()).isNull();
        assertThat(emptyExpenseUser.getStatus()).isEqualTo("pending");
    }

    @Test
    void testSetStatus() {
        // Change status and check
        expenseUser.setStatus("paid");
        assertThat(expenseUser.getStatus()).isEqualTo("paid");
    }

    @Test
    void testSetAmountOwed() {
        // Change amount owed and check
        expenseUser.setAmountOwed(new BigDecimal("200.00"));
        assertThat(expenseUser.getAmountOwed()).isEqualByComparingTo("200.00");
    }
}
