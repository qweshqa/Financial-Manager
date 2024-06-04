package org.qweshqa.financialmanager.controllers;

import org.qweshqa.financialmanager.models.Finance;
import org.qweshqa.financialmanager.services.FinanceService;
import org.qweshqa.financialmanager.services.StatisticService;
import org.qweshqa.financialmanager.utils.FinanceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/statistic")
public class StatisticController {

    private final StatisticService statisticService;

    private final FinanceService financeService;

    @Autowired
    public StatisticController(StatisticService statisticService, FinanceService financeService) {
        this.statisticService = statisticService;
        this.financeService = financeService;
    }

    @GetMapping()
    public String getGeneralStatistic(Model model){
        // header
        model.addAttribute("today", LocalDate.now());

        // expense statistic block
        model.addAttribute("general_spending_total", statisticService.getGeneralSpendingTotal());
        model.addAttribute("total_amount_of_spending", financeService.findAll().size());
        if(financeService.findBiggestExpenseOrIncome(FinanceType.EXPENSE).isPresent()){
            model.addAttribute("biggest_expense", financeService.findBiggestExpenseOrIncome(FinanceType.EXPENSE).get());
        }

        // income statistic block
        model.addAttribute("general_income_total", statisticService.getGeneralIncomeTotal());
        model.addAttribute("total_amount_of_incomes", financeService.findAll().size());
        if(financeService.findBiggestExpenseOrIncome(FinanceType.INCOME).isPresent()){
            model.addAttribute("biggest_income", financeService.findBiggestExpenseOrIncome(FinanceType.INCOME).get());
        }

        return "statistic/general";
    }
    @GetMapping("/expenses_index")
    public String getAllExpenses(Model model){

        // header
        model.addAttribute("today", LocalDate.now());

        List<Finance> expenses = financeService.findAllByType(FinanceType.EXPENSE);

        List<Finance> sortedExpenses = expenses.stream()
                .sorted(Comparator.comparing(Finance::getAmount)).toList().reversed();

        model.addAttribute("expenses", sortedExpenses);

        return "statistic/expenses_index";
    }

    @GetMapping("/incomes_index")
    public String getAllIncomes(Model model){

        // header
        model.addAttribute("today", LocalDate.now());

        List<Finance> incomes = financeService.findAllByType(FinanceType.INCOME);

        List<Finance> sortedIncomes = incomes.stream()
                .sorted(Comparator.comparing(Finance::getAmount)).toList().reversed();

        model.addAttribute("incomes", sortedIncomes);

        return "statistic/incomes_index";
    }
}
