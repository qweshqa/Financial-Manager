package org.qweshqa.financialmanager.controllers;

import org.qweshqa.financialmanager.models.Expense;
import org.qweshqa.financialmanager.services.DateService;
import org.qweshqa.financialmanager.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    private final DateService dateService;

    @Autowired
    public ExpenseController(ExpenseService expenseService, DateService dateService) {
        this.expenseService = expenseService;
        this.dateService = dateService;
    }

    @GetMapping("/today")
    public String getExpense(Model model){
        List<Expense> expenseList = expenseService.index(LocalDate.now());

        String monthNameLowerCase = LocalDate.now().getMonth().toString().substring(1).toLowerCase();
        String monthNameFirstLetter = LocalDate.now().getMonth().toString().substring(0, 1).toUpperCase();

        model.addAttribute("monthName", monthNameFirstLetter + monthNameLowerCase);
        // date navigation
        model.addAttribute("day", LocalDate.now());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("monthDays", dateService.getMonthDaysInList(LocalDate.now().getMonth()));

        // spending total
        model.addAttribute("expense_total", expenseService.getExpensesTotalByDate(LocalDate.now()));

        // index
        model.addAttribute("expenseList", expenseList);

        // spending to create
        model.addAttribute("newExpense", new Expense());

        return "expense/list";
    }
    @GetMapping("/{date}")
    public String getSpendingByDate(@PathVariable("date") String date, Model model){
        date = dateService.formatDate(date);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);

        List<Expense> expenseList = expenseService.index(localDate);

        String monthNameLowerCase = localDate.getMonth().toString().substring(1).toLowerCase();
        String monthNameFirstLetter = localDate.getMonth().toString().substring(0, 1).toUpperCase();
        model.addAttribute("monthName", monthNameFirstLetter + monthNameLowerCase);

        // date navigation
        model.addAttribute("day", localDate);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("monthDays", dateService.getMonthDaysInList(localDate.getMonth()));

        // spending total
        model.addAttribute("expense_total", expenseService.getExpensesTotalByDate(localDate));

        // index
        model.addAttribute("expenseList", expenseList);

        // spending to create
        model.addAttribute("newExpense", new Expense());

        return "expense/list";
    }

    @PostMapping("/today")
    public String addExpense(@ModelAttribute("newSpending") Expense expense){
        expense.setDate(LocalDate.now());
        expenseService.save(expense);

        return "redirect:/expenses/today";
    }

    @PostMapping("/{id}")
    public String deleteSpending(@PathVariable("id") int id){
        expenseService.delete(id);

        return "redirect:/expenses/today";
    }
}
