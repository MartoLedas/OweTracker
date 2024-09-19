package com.example.owetracker.service;

import com.example.owetracker.repository.OverviewExpenseRepository;
import com.example.owetracker.repository.OverviewExpenseUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OverviewService {

    @Autowired
    private OverviewExpenseRepository overviewExpenseRepository;

    @Autowired
    private OverviewExpenseUserRepository overviewExpenseUserRepository;

    public BigDecimal getTotalOwedToUser(Long userId) {
        return overviewExpenseRepository.getTotalOwedToUser(userId);
    }

    public BigDecimal getTotalAmountOwedByUser(Long userId) {
        return overviewExpenseUserRepository.getTotalAmountOwedByUser(userId);
    }
}
