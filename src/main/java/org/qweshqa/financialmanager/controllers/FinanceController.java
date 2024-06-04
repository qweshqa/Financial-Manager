package org.qweshqa.financialmanager.controllers;

import org.qweshqa.financialmanager.models.Finance;
import org.qweshqa.financialmanager.services.DateService;
import org.qweshqa.financialmanager.services.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/finances")
public class FinanceController {

    private final FinanceService financeService;

    private final DateService dateService;

    @Autowired
    public FinanceController(FinanceService financeService, DateService dateService) {
        this.financeService = financeService;
        this.dateService = dateService;
    }

    @GetMapping("/{date}")
    public String getFinancesByDate(@PathVariable("date") String date, Model model){
        date = dateService.formatDate(date);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);

        // date navbar
        model.addAttribute("date", localDate);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("monthDays", dateService.getMonthDaysInList(localDate.getMonth()));

        // finances amount total
        model.addAttribute("finances_amount_total", financeService.getFinanceAmountTotalByDate(localDate));

        // index
        model.addAttribute("finances", financeService.findAllByDate(localDate));

        // empty finance for creating a new one
        model.addAttribute("new_finance", new Finance());

        return "finance/list";
    }

    @PostMapping("/{date}")
    public String addFinance(@ModelAttribute("new_finance") Finance finance, @PathVariable("date") String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);

        finance.setDate(localDate);
        financeService.save(finance);

        return "redirect:/finances/" + localDate;
    }

    @PostMapping("/{date}/{id}")
    public String deleteFinance(@PathVariable("date") String date, @PathVariable("id") int id){
        financeService.delete(id);

        return "redirect:/finances/" + date;
    }
}
