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

    @GetMapping("/show")
    public String showFinances(@RequestParam(value = "display", defaultValue = "expense") String financeType,
            @RequestParam(value = "displayPeriod", defaultValue = "day") String displayPeriod, Model model){
        // user info
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("user", userService.findUserByEmail(authentication.getName()).get());

        // index
        switch(displayPeriod){
            case "all-time":
                model.addAttribute("finances", financeService.findAllByType(FinanceType.valueOf(financeType.toUpperCase())));;
                break;
            case "month":
                model.addAttribute("finances", financeService.findAllByMonthAndType(LocalDate.now().getMonth(), FinanceType.valueOf(financeType.toUpperCase())));
                break;
            case "week":
                model.addAttribute("finances", financeService.findAllByWeekAndType(LocalDate.now(), FinanceType.valueOf(financeType.toUpperCase())));
                break;
            case "day":
                model.addAttribute("finances", financeService.findAllByDateAndType(LocalDate.now(), FinanceType.valueOf(financeType.toUpperCase())));
                break;
        }

        // empty finance for creating a new one
        model.addAttribute("new_finance", new Finance());

        model.addAttribute("displayPeriod", displayPeriod);

        return "finance/list";
    }

    @GetMapping("/create")
    public String createFinance(Model model){
        model.addAttribute("new_finance", new Finance());

        return "finance/create";
    }

    @PostMapping("/add")
    public String addFinance(@ModelAttribute("new_finance") Finance finance){

        if(finance.getComment().isEmpty()){
            finance.setComment("No comment.");
        }
        finance.setDate(LocalDate.now());
        finance.setMonth(LocalDate.now().getMonth());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        finance.setCurrency(settingService.findSettingByUser(userService.findUserByEmail(authentication.getName()).get()).get().getCurrencyUnit());
        if(finance.getType() == FinanceType.INCOME){
            userService.findUserByEmail(authentication.getName()).get().plusBalance(Float.parseFloat(finance.getAmount().toString()));
        } else userService.findUserByEmail(authentication.getName()).get().minusBalance((Float.parseFloat(finance.getAmount().toString())));

        financeService.save(finance);

        return "redirect:/finances/show";
    }

    @PostMapping("/delete/{id}")
    public String deleteFinance(@PathVariable("id") int id){

        Finance finance = financeService.findById(id).get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(finance.getType() == FinanceType.INCOME){
            userService.findUserByEmail(authentication.getName()).get().minusBalance(Float.parseFloat(finance.getAmount().toString()));
        } else userService.findUserByEmail(authentication.getName()).get().plusBalance((Float.parseFloat(finance.getAmount().toString())));

        financeService.delete(id);

        return "redirect:/finances/show";
    }
}
