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

    @GetMapping("/today")
    public String getIncome(Model model){
        List<Income> incomeList = incomeService.index(LocalDate.now());

        String monthNameLowerCase = LocalDate.now().getMonth().toString().substring(1).toLowerCase();
        String monthNameFirstLetter = LocalDate.now().getMonth().toString().substring(0, 1).toUpperCase();

        model.addAttribute("monthName", monthNameFirstLetter + monthNameLowerCase);
        // date navigation
        model.addAttribute("day", LocalDate.now());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("monthDays", dateService.getMonthDaysInList(LocalDate.now().getMonth()));

        // spending total
        model.addAttribute("income_total", incomeService.getIncomeTotalByDate(LocalDate.now()));

        // index
        model.addAttribute("incomeList", incomeList);

        // spending to create
        model.addAttribute("newIncome", new Income());

        return "income/list";
    }
    @GetMapping("/{date}")
    public String getIncomeByDate(@PathVariable("date") String date, Model model){
        date = dateService.formatDate(date);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);

        List<Income> incomeList = incomeService.index(localDate);

        String monthNameLowerCase = localDate.getMonth().toString().substring(1).toLowerCase();
        String monthNameFirstLetter = localDate.getMonth().toString().substring(0, 1).toUpperCase();
        model.addAttribute("monthName", monthNameFirstLetter + monthNameLowerCase);

        // date navigation
        model.addAttribute("day", localDate);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("monthDays", dateService.getMonthDaysInList(localDate.getMonth()));

        // spending total
        model.addAttribute("income_total", incomeService.getIncomeTotalByDate(localDate));

        // index
        model.addAttribute("incomeList", incomeList);

        // spending to create
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
