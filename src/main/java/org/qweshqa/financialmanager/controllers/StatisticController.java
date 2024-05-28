package org.qweshqa.financialmanager.controllers;

import org.qweshqa.financialmanager.services.ExpenseService;
import org.qweshqa.financialmanager.services.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/statistic")
public class StatisticController {

    private final StatisticService statisticService;

    private final ExpenseService expenseService;

    @Autowired
    public StatisticController(StatisticService statisticService, ExpenseService expenseService) {
        this.statisticService = statisticService;
        this.expenseService = expenseService;
    }

    @GetMapping()
    public String getGeneralStatistic(Model model){

        // statistic block
        model.addAttribute("general_spending_total", statisticService.getGeneralSpendingTotal());
        model.addAttribute("total_amount_of_spending", expenseService.index().size());
        if(expenseService.findBiggestExpense().isPresent()){
            model.addAttribute("biggest_expense", expenseService.findBiggestExpense().get());
        };

        return "statistic/general";
    }
}
