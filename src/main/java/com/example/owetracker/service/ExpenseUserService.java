package com.example.owetracker.service;

import com.example.owetracker.model.ExpenseUser;
import com.example.owetracker.repository.ExpenseUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenseUserService {

    @Autowired
    private ExpenseUserRepository expenseUserRepository;

    public ExpenseUser save(ExpenseUser expenseUser) {
        return expenseUserRepository.save(expenseUser);
    }

}
