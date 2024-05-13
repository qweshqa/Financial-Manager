package org.qweshqa.financialmanager.controllers;

import org.qweshqa.financialmanager.models.Spending;
import org.qweshqa.financialmanager.services.DateService;
import org.qweshqa.financialmanager.services.SpendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/spending")
public class SpendingController {
    private final SpendingService spendingService;

    private final DateService dateService;

    @Autowired
    public SpendingController(SpendingService spendingService, DateService dateService) {
        this.spendingService = spendingService;
        this.dateService = dateService;
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
    @GetMapping("/{date}")
    public String getSpendingByDate(@PathVariable("date") String date, Model model){
        date = dateService.formatDate(date);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);

        List<Spending> spendingList = spendingService.index(localDate);

        // date navigation
        model.addAttribute("month", localDate);
        model.addAttribute("monthDays", dateService.getMonthDaysInList(localDate.getMonth()));

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
