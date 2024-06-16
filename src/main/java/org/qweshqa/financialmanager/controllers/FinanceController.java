package org.qweshqa.financialmanager.controllers;

import org.qweshqa.financialmanager.models.Finance;
import org.qweshqa.financialmanager.services.DateService;
import org.qweshqa.financialmanager.services.FinanceService;
import org.qweshqa.financialmanager.services.SettingService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.FinanceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final UserService userService;

    private final SettingService settingService;

    @Autowired
    public FinanceController(FinanceService financeService, DateService dateService, UserService userService, SettingService settingService) {
        this.financeService = financeService;
        this.dateService = dateService;
        this.userService = userService;
        this.settingService = settingService;
    }

    @GetMapping("/{date}")
    public String getFinancesByDate(@RequestParam(value = "show_only", defaultValue = "all") String showOnly, @PathVariable("date") String date, Model model){
        date = dateService.formatDate(date);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);

        // date navbar
        model.addAttribute("date", localDate);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("monthDays", dateService.getMonthDaysInList(localDate.getMonth()));

        // user info
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("user", userService.findUserByEmail(authentication.getName()).get());

        // finances amount total
        model.addAttribute("finances_amount_total", financeService.getFinanceAmountTotalByDate(localDate));

        // index
        model.addAttribute("finances", financeService.findAllByDate(localDate));

        // empty finance for creating a new one
        model.addAttribute("new_finance", new Finance());

        return "finance/list";
    }

    @GetMapping("{date}/create")
    public String createFinance(@PathVariable("date") String date, Model model){
        model.addAttribute("new_finance", new Finance());
        model.addAttribute("date", date);

        return "finance/create";
    }

    @PostMapping("/{date}")
    public String addFinance(@ModelAttribute("new_finance") Finance finance, @PathVariable("date") String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);

        if(finance.getComment().isEmpty()){
            finance.setComment("No comment.");
        }
        finance.setDate(localDate);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        finance.setCurrency(settingService.findSettingByUser(userService.findUserByEmail(authentication.getName()).get()).get().getCurrencyUnit());
        if(finance.getType() == FinanceType.INCOME){
            userService.findUserByEmail(authentication.getName()).get().plusBalance(Float.parseFloat(finance.getAmount().toString()));
        } else userService.findUserByEmail(authentication.getName()).get().minusBalance((Float.parseFloat(finance.getAmount().toString())));

        financeService.save(finance);

        return "redirect:/finances/" + localDate;
    }

    @PostMapping("/{date}/{id}")
    public String deleteFinance(@PathVariable("date") String date, @PathVariable("id") int id){
        Finance finance = financeService.findById(id).get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(finance.getType() == FinanceType.INCOME){
            userService.findUserByEmail(authentication.getName()).get().minusBalance(Float.parseFloat(finance.getAmount().toString()));
        } else userService.findUserByEmail(authentication.getName()).get().plusBalance((Float.parseFloat(finance.getAmount().toString())));

        financeService.delete(id);

        return "redirect:/finances/" + date;
    }
}
