package com.example.owetracker.controller;

import com.example.owetracker.service.OverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;

@Controller
public class OverviewController {

    @Autowired
    private OverviewService overviewService;

    @GetMapping("/overview")
    public String showOverviewPage(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        BigDecimal totalOwedToUser = overviewService.getTotalOwedToUser(userId.longValue());
        BigDecimal totalAmountOwedByUser = overviewService.getTotalAmountOwedByUser(userId.longValue());

        model.addAttribute("totalOwedToUser", totalOwedToUser != null ? totalOwedToUser : BigDecimal.ZERO);
        model.addAttribute("totalAmountOwedByUser", totalAmountOwedByUser != null ? totalAmountOwedByUser : BigDecimal.ZERO);

        return "overview"; // This should be the name of your Thymeleaf template
    }
}

