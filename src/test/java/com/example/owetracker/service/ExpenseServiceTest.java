package com.example.owetracker.service;

import com.example.owetracker.model.Expense;
import com.example.owetracker.model.ExpenseUser;
import com.example.owetracker.model.User;
import com.example.owetracker.repository.ExpenseRepository;
import com.example.owetracker.repository.ExpenseUserRepository;
import com.example.owetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseUserRepository expenseUserRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /*@Test
    void testCreateExpenseSplitEqually() {
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setTitle("Dinner");
        expense.setDescription("Dinner with friends");
        expense.setAmount(BigDecimal.valueOf(100));
        expense.setPaidBy(new User());  // Mocked user
        expense.setGroupId(1L);

        List<Integer> participantIds = List.of(1, 2, 3);
        List<BigDecimal> amountsOwed = List.of(BigDecimal.valueOf(30), BigDecimal.valueOf(30), BigDecimal.valueOf(40));
        BigDecimal totalAmount = BigDecimal.valueOf(100);

        // Mock repository behavior
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new User()));
        when(expenseUserRepository.save(any(ExpenseUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method
        Expense createdExpense = expenseService.createExpense(expense, participantIds, amountsOwed, true, totalAmount);

        // Verify interactions and results
        verify(expenseRepository).save(expense);
        verify(expenseUserRepository, times(3)).save(any(ExpenseUser.class));
        assertNotNull(createdExpense);
        assertEquals("pending", createdExpense.getStatus());
    }*/

    @Test
    void testCreateExpenseSplitUnequally() {
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setTitle("Dinner");
        expense.setDescription("Dinner with friends");
        expense.setAmount(BigDecimal.valueOf(100));
        expense.setPaidBy(new User());  // Mocked user
        expense.setGroupId(1);

        List<Integer> participantIds = List.of(1, 2, 3);
        List<BigDecimal> amountsOwed = List.of(BigDecimal.valueOf(30), BigDecimal.valueOf(30), BigDecimal.valueOf(40));
        BigDecimal totalAmount = BigDecimal.valueOf(100);

        // Mock repository behavior
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new User()));
        when(expenseUserRepository.save(any(ExpenseUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method
        Expense createdExpense = expenseService.createExpense(expense, participantIds, amountsOwed, false, totalAmount);

        // Verify interactions and results
        verify(expenseRepository).save(expense);
        verify(expenseUserRepository, times(3)).save(any(ExpenseUser.class));
        assertNotNull(createdExpense);
        assertEquals("pending", createdExpense.getStatus());
    }

    @Test
    void testGetAllExpenses() {
        List<Expense> expenses = List.of(new Expense(), new Expense());

        // Mock repository behavior
        when(expenseRepository.findAll()).thenReturn(expenses);

        // Call the method
        List<Expense> result = expenseService.getAllExpenses();

        // Verify interactions and results
        assertEquals(2, result.size());
        verify(expenseRepository).findAll();
    }

    @Test
    void testGetExpenseById() {
        Expense expense = new Expense();
        expense.setId(1L);

        // Mock repository behavior
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        // Call the method
        Expense result = expenseService.getExpenseById(1L);

        // Verify interactions and results
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testUpdateExpense() {
        Expense existingExpense = new Expense();
        existingExpense.setId(1L);
        existingExpense.setTitle("Old Title");

        Expense updatedExpense = new Expense();
        updatedExpense.setTitle("New Title");

        // Mock repository behavior
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(existingExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(updatedExpense);

        // Call the method
        Expense result = expenseService.updateExpense(1L, updatedExpense);

        // Verify interactions and results
        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
    }

    @Test
    void testUpdateExpenseNotFound() {
        Expense updatedExpense = new Expense();
        updatedExpense.setTitle("New Title");

        // Mock repository behavior
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the method
        Expense result = expenseService.updateExpense(1L, updatedExpense);

        // Verify interactions and results
        assertNull(result);
    }

    @Test
    void testDeleteExpense() {
        // Mock repository behavior
        when(expenseRepository.existsById(1L)).thenReturn(true);

        // Call the method
        expenseService.deleteExpense(1L);

        // Verify interactions
        verify(expenseRepository).deleteById(1L);
    }

    @Test
    void testSettleExpense() {
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setStatus("pending");

        User user = new User();
        user.setId(1);

        ExpenseUser expenseUser = new ExpenseUser();
        expenseUser.setStatus("pending");

        // Mock repository behavior
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(expenseUserRepository.findByExpenseIdAndUserId(1L, 1L)).thenReturn(Optional.of(expenseUser));
        when(expenseUserRepository.findByExpenseId(1L)).thenReturn(List.of(expenseUser));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(expenseUserRepository.save(any(ExpenseUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method
        Expense result = expenseService.settleExpense(1L, 1L);

        // Verify interactions and results
        assertNotNull(result);
        assertEquals("paid", result.getStatus());
    }

    @Test
    void testSettleExpenseUserNotParticipant() {
        // Mock repository behavior
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(new Expense()));
        when(expenseUserRepository.findByExpenseIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        // Call the method and verify exception
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            expenseService.settleExpense(1L, 1L);
        });

        assertEquals("User is not a participant in this expense.", thrown.getMessage());
    }

    @Test
    void testGetExpensesByGroupId() {
        List<Expense> expenses = List.of(new Expense(), new Expense());

        // Mock repository behavior
        when(expenseRepository.findByGroupId(1)).thenReturn(expenses);

        // Call the method
        List<Expense> result = expenseService.getExpensesByGroupId(1);

        // Verify interactions and results
        assertEquals(2, result.size());
        verify(expenseRepository).findByGroupId(1);
    }

    @Test
    void testGetExpensesPaidByUser() {
        List<Expense> expenses = List.of(new Expense(), new Expense());

        // Mock repository behavior
        when(expenseRepository.findByPaidById(1)).thenReturn(expenses);

        // Call the method
        List<Expense> result = expenseService.getExpensesPaidByUser(1);

        // Verify interactions and results
        assertEquals(2, result.size());
        verify(expenseRepository).findByPaidById(1);
    }

    @Test
    void testGetExpensesByStatus() {
        List<Expense> expenses = List.of(new Expense(), new Expense());

        // Mock repository behavior
        when(expenseRepository.findByStatus("paid")).thenReturn(expenses);

        // Call the method
        List<Expense> result = expenseService.getExpensesByStatus("paid");

        // Verify interactions and results
        assertEquals(2, result.size());
        verify(expenseRepository).findByStatus("paid");
    }
}
