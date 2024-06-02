package org.qweshqa.financialmanager.controllers;

import org.qweshqa.financialmanager.models.Expense;
import org.qweshqa.financialmanager.models.Income;
import org.qweshqa.financialmanager.services.ExpenseService;
import org.qweshqa.financialmanager.services.IncomeService;
import org.qweshqa.financialmanager.services.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/statistic")
public class StatisticController {

    private final StatisticService statisticService;

    private final ExpenseService expenseService;

    private final IncomeService incomeService;

    @Autowired
    public StatisticController(StatisticService statisticService, ExpenseService expenseService,
                               IncomeService incomeService) {
        this.statisticService = statisticService;
        this.expenseService = expenseService;
        this.incomeService = incomeService;
    }

    @GetMapping()
    public String getGeneralStatistic(Model model){
        // header
        model.addAttribute("today", LocalDate.now());

        // expense statistic block
        model.addAttribute("general_spending_total", statisticService.getGeneralSpendingTotal());
        model.addAttribute("total_amount_of_spending", expenseService.index().size());
        if(expenseService.findBiggestExpense().isPresent()){
            model.addAttribute("biggest_expense", expenseService.findBiggestExpense().get());
        }

        // income statistic block
        model.addAttribute("general_income_total", statisticService.getGeneralIncomeTotal());
        model.addAttribute("total_amount_of_incomes", incomeService.index().size());
        if(incomeService.findBiggestIncome().isPresent()){
            model.addAttribute("biggest_income", incomeService.findBiggestIncome().get());
        }

        return "statistic/general";
    }
    @GetMapping("/expenses_index")
    public String getAllExpenses(Model model){

        // header
        model.addAttribute("today", LocalDate.now());

        List<Expense> expenses = expenseService.index();

        List<Expense> sortedExpenses = expenses.stream()
                .sorted(Comparator.comparing(Expense::getAmount)).toList().reversed();

        model.addAttribute("expenses", sortedExpenses);

        return "statistic/expenses_index";
    }

    @GetMapping("/incomes_index")
    public String getAllIncomes(Model model){

        // header
        model.addAttribute("today", LocalDate.now());

        List<Income> incomes = incomeService.index();

        List<Income> sortedIncomes = incomes.stream()
                .sorted(Comparator.comparing(Income::getAmount)).toList().reversed();

        model.addAttribute("incomes", sortedIncomes);

        return "statistic/incomes_index";
    }
}
