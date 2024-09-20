package com.example.owetracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpenseTest {

    private Expense expense;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // Initialize some mock data
        user1 = new User();
        user1.setId(1);
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(2);
        user2.setUsername("user2");

        List<User> participants = Arrays.asList(user1, user2);

        // Create an instance of Expense
        expense = new Expense("Dinner", "Team dinner at a restaurant",
                new BigDecimal("100.50"), user1, 1,
                "PENDING", LocalDateTime.now(), participants);
    }

    @Test
    void testExpenseConstructor() {
        assertThat(expense.getTitle()).isEqualTo("Dinner");
        assertThat(expense.getDescription()).isEqualTo("Team dinner at a restaurant");
        assertThat(expense.getAmount()).isEqualByComparingTo(new BigDecimal("100.50"));
        assertThat(expense.getPaidBy()).isEqualTo(user1);
        assertThat(expense.getGroupId()).isEqualTo(1L);
        assertThat(expense.getStatus()).isEqualTo("PENDING");
        assertThat(expense.getParticipants()).hasSize(2).contains(user1, user2);
    }

    @Test
    void testSettersAndGetters() {
        // Test title
        expense.setTitle("Lunch");
        assertThat(expense.getTitle()).isEqualTo("Lunch");

        // Test description
        expense.setDescription("Lunch with friends");
        assertThat(expense.getDescription()).isEqualTo("Lunch with friends");

        // Test amount
        expense.setAmount(new BigDecimal("150.75"));
        assertThat(expense.getAmount()).isEqualByComparingTo("150.75");

        // Test paidBy
        expense.setPaidBy(user2);
        assertThat(expense.getPaidBy()).isEqualTo(user2);

        // Test groupId
        expense.setGroupId(2);
        assertThat(expense.getGroupId()).isEqualTo(2L);

        // Test status
        expense.setStatus("COMPLETED");
        assertThat(expense.getStatus()).isEqualTo("COMPLETED");

        // Test participants
        User user3 = new User();
        user3.setId(3);
        user3.setUsername("user3");

        expense.setParticipants(Arrays.asList(user3));
        assertThat(expense.getParticipants()).hasSize(1).contains(user3);
    }

    @Test
    void testNoArgsConstructor() {
        // Create a new Expense instance using the no-args constructor
        Expense emptyExpense = new Expense();
        assertThat(emptyExpense.getTitle()).isNull();
        assertThat(emptyExpense.getAmount()).isNull();
        assertThat(emptyExpense.getPaidBy()).isNull();
        assertThat(emptyExpense.getParticipants()).isNull();
    }

    @Test
    void testCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        expense.setCreatedAt(now);
        assertThat(expense.getCreatedAt()).isEqualTo(now);
    }
}
