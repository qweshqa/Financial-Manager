package org.qweshqa.financialmanager.controllers;

import org.qweshqa.financialmanager.models.Income;
import org.qweshqa.financialmanager.services.DateService;
import org.qweshqa.financialmanager.services.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/incomes")
public class IncomeController {
    private final DateService dateService;

    private final IncomeService incomeService;

    @Autowired
    public IncomeController(IncomeService incomeService, DateService dateService) {
        this.incomeService = incomeService;
        this.dateService = dateService;
    }

    @GetMapping("/{date}")
    public String getIncomesByDate(@PathVariable("date") String date, Model model){
        date = dateService.formatDate(date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);

        // date navigation
        model.addAttribute("monthName", dateService.getMonthNameInCamelCase(localDate.getMonth().toString()));
        model.addAttribute("day", localDate);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("monthDays", dateService.getMonthDaysInList(localDate.getMonth()));

        // income total
        model.addAttribute("income_total", incomeService.getIncomeTotalByDate(localDate));

        // index
        List<Income> incomeList = incomeService.index(localDate);
        model.addAttribute("incomeList", incomeList);

        // income to create
        model.addAttribute("newIncome", new Income());

        return "income/list";
    }

    @PostMapping("/today")
    public String addIncome(@ModelAttribute("newIncome") Income income){
        income.setDate(LocalDate.now());
        incomeService.save(income);

        return "redirect:/incomes/today";
    }

    @PostMapping("/{id}")
    public String deleteIncome(@PathVariable("id") int id){
        incomeService.delete(id);

        return "redirect:/incomes/today";
    }
}
