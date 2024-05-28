package org.qweshqa.financialmanager.controllers;

import org.qweshqa.financialmanager.services.DateService;
import org.qweshqa.financialmanager.services.EarningService;
import org.qweshqa.financialmanager.services.SpendingService;
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

    private final SpendingService spendingService;

    @Autowired
    public StatisticController(StatisticService statisticService, SpendingService spendingService) {
        this.statisticService = statisticService;
        this.spendingService = spendingService;
    }

    @GetMapping()
    public String getGeneralStatistic(Model model){

        // statistic block
        model.addAttribute("general_spending_total", statisticService.getGeneralSpendingTotal());
        model.addAttribute("total_amount_of_spending", spendingService.index().size());
        if(spendingService.findBiggestExpense().isPresent()){
            model.addAttribute("biggest_expense", spendingService.findBiggestExpense().get());
        };

        return "statistic/general";
    }
}
