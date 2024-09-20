package com.example.owetracker.service;

import com.example.owetracker.model.ExpenseUser;
import com.example.owetracker.repository.ExpenseUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ExpenseUserServiceTest {

    @Mock
    private ExpenseUserRepository expenseUserRepository;

    @InjectMocks
    private ExpenseUserService expenseUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave() {
        ExpenseUser expenseUser = new ExpenseUser();
        expenseUser.setId(1L);

        // Mock the save behavior
        when(expenseUserRepository.save(any(ExpenseUser.class))).thenReturn(expenseUser);

        // Call the method
        ExpenseUser savedExpenseUser = expenseUserService.save(expenseUser);

        // Verify interactions and results
        verify(expenseUserRepository).save(expenseUser);
        assertNotNull(savedExpenseUser);
        assertEquals(1L, savedExpenseUser.getId());
    }

    @Test
    void testFindByExpenseId() {
        Long expenseId = 1L;
        List<ExpenseUser> expenseUsers = List.of(new ExpenseUser(), new ExpenseUser());

        // Mock the findByExpenseId behavior
        when(expenseUserRepository.findByExpenseId(expenseId)).thenReturn(expenseUsers);

        // Call the method
        List<ExpenseUser> result = expenseUserService.findByExpenseId(expenseId);

        // Verify interactions and results
        verify(expenseUserRepository).findByExpenseId(expenseId);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindByExpenseIdAndUserId() {
        Long expenseId = 1L;
        Long userId = 2L;
        ExpenseUser expenseUser = new ExpenseUser();
        expenseUser.setId(1L);

        // Mock the findByExpenseIdAndUserId behavior
        when(expenseUserRepository.findByExpenseIdAndUserId(expenseId, userId)).thenReturn(Optional.of(expenseUser));

        // Call the method
        Optional<ExpenseUser> result = expenseUserService.findByExpenseIdAndUserId(expenseId, userId);

        // Verify interactions and results
        verify(expenseUserRepository).findByExpenseIdAndUserId(expenseId, userId);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testFindByExpenseIdAndUserIdNotFound() {
        Long expenseId = 1L;
        Long userId = 2L;

        // Mock the findByExpenseIdAndUserId behavior to return empty
        when(expenseUserRepository.findByExpenseIdAndUserId(expenseId, userId)).thenReturn(Optional.empty());

        // Call the method
        Optional<ExpenseUser> result = expenseUserService.findByExpenseIdAndUserId(expenseId, userId);

        // Verify interactions and results
        verify(expenseUserRepository).findByExpenseIdAndUserId(expenseId, userId);
        assertFalse(result.isPresent());
    }
}
