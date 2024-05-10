package org.qweshqa.financialmanager.controllers;

import org.qweshqa.financialmanager.models.Spending;
import org.qweshqa.financialmanager.services.SpendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

        // index
        model.addAttribute("spendingList", spendingList);

        // spending to create
        model.addAttribute("newSpending", new Spending());

        return "spendingList";
    }

    @PostMapping()
    public String addSpending(@ModelAttribute("newSpending") Spending spending){
        spending.setDate(LocalDate.now());
        spendingService.save(spending);

        return "redirect:/spending";
    }

    @PostMapping("/{id}")
    public String deleteSpending(@PathVariable("id") int id){
        spendingService.delete(id);

        return "redirect:/spending";
    }
}
