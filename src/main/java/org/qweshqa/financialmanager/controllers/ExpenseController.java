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
        // date navigation
        model.addAttribute("monthName", dateService.getMonthNameInCamelCase(LocalDate.now().getMonth().toString()));
        model.addAttribute("day", LocalDate.now());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("monthDays", dateService.getMonthDaysInList(LocalDate.now().getMonth()));

        // expense total
        model.addAttribute("expense_total", expenseService.getExpensesTotalByDate(LocalDate.now()));

        // index
        List<Expense> expenseList = expenseService.index(LocalDate.now());
        model.addAttribute("expenseList", expenseList);

        // expense to create
        model.addAttribute("newExpense", new Expense());

        return "expense/list";
    }
    @GetMapping("/{date}")
    public String getSpendingByDate(@PathVariable("date") String date, Model model){
        date = dateService.formatDate(date);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);

        // date navigation
        model.addAttribute("monthName", dateService.getMonthNameInCamelCase(localDate.getMonth().toString()));
        model.addAttribute("day", localDate);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("monthDays", dateService.getMonthDaysInList(localDate.getMonth()));

        // expense total
        model.addAttribute("expense_total", expenseService.getExpensesTotalByDate(localDate));

        // index
        List<Expense> expenseList = expenseService.index(localDate);
        model.addAttribute("expenseList", expenseList);

        // expense to create
        model.addAttribute("newExpense", new Expense());

        return "expense/list";
    }

    @PostMapping("/today")
    public String addExpense(@ModelAttribute("newExpense") Expense expense){
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
