package com.example.owetracker.controller;

import com.example.owetracker.model.Expense;
import com.example.owetracker.model.ExpenseUser;
import com.example.owetracker.model.User;
import com.example.owetracker.service.ExpenseService;
import com.example.owetracker.service.ExpenseUserService;
import com.example.owetracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private UserService userService;

    @Mock
    private ExpenseUserService expenseUserService;

    @InjectMocks
    private ExpenseController expenseController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(expenseController).build();
    }



    @Test
    void testCreateExpense() throws Exception {
        Expense expense = new Expense();
        expense.setTitle("Dinner");
        expense.setDescription("Dinner with friends");

        mockMvc.perform(MockMvcRequestBuilders.post("/expenses/create")
                        .param("title", "Dinner")
                        .param("description", "Dinner with friends")
                        .param("participantsIds", "1", "2")
                        .param("amountsOwed", "50.00", "25.00")
                        .param("splitEqually", "true")
                        .param("totalAmount", "75.00")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/expenses/list"));

        verify(expenseService).createExpense(any(Expense.class), anyList(), anyList(), eq(true), eq(new BigDecimal("75.00")));
    }

    @Test
    void testSaveExpense() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1);

        User user = new User();
        user.setId(1);
        when(userService.findById(1)).thenReturn(user);

        Expense expense = new Expense();
        expense.setId(1L);
        when(expenseService.save(any(Expense.class))).thenReturn(expense);

        mockMvc.perform(MockMvcRequestBuilders.post("/expenses/save")
                        .session(session)
                        .param("title", "Dinner")
                        .param("description", "Dinner with friends")
                        .param("splitEqually", "true")
                        .param("totalAmount", "100.00")
                        .param("users", "1", "2")
                        .param("userAmounts", "50.00", "50.00")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/expensesview"));

        verify(expenseService).save(any(Expense.class));
        verify(expenseUserService, times(2)).save(any(ExpenseUser.class));
    }

/*    @Test
    void testGetAllExpenses() throws Exception {
        Expense expense = new Expense();
        expense.setTitle("Dinner");
        List<Expense> expenses = Arrays.asList(expense);

        when(expenseService.getAllExpenses()).thenReturn(expenses);

        mockMvc.perform(MockMvcRequestBuilders.get("/expenses")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Dinner"));
    }

    @Test
    void testGetExpenseById() throws Exception {

        Expense expense = new Expense();
        expense.setId(1L);
        expense.setTitle("Dinner");

        // Mock the service method call to return the created Expense object
        when(expenseService.getExpenseById(1L)).thenReturn(expense);

        // Perform the GET request to the endpoint
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/expenses/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Response Body: " + responseBody);


        mockMvc.perform(MockMvcRequestBuilders.get("/expenses/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Dinner"));
    }*/

    @Test
    void testDeleteExpense() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/expenses/1"))
                .andExpect(status().isOk()); // now returns 200 should be 204 for sucessful deletion

        verify(expenseService).deleteExpense(1L);
    }
}
