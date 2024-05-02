package org.qweshqa.financialmanager.controllers;

import org.qweshqa.financialmanager.models.Spending;
import org.qweshqa.financialmanager.services.SpendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/spending")
public class SpendingController {
    private final SpendingService spendingService;

    @Autowired
    public SpendingController(SpendingService spendingService) {
        this.spendingService = spendingService;
    }

    @GetMapping()
    public String getSpending(Model model){
        List<Spending> spendingList = spendingService.index();

        model.addAttribute("spendingList", spendingList);
        return "spendingList";
    }
}
