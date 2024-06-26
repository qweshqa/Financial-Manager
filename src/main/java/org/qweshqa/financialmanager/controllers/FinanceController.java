package org.qweshqa.financialmanager.controllers;

import jakarta.validation.Valid;
import org.qweshqa.financialmanager.models.Finance;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.FinanceService;
import org.qweshqa.financialmanager.services.SettingService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.FinanceType;
import org.qweshqa.financialmanager.utils.FinanceTypeStringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/finances")
public class FinanceController {

    private final FinanceService financeService;

    private final UserService userService;

    private final SettingService settingService;

    private final FinanceTypeStringConverter financeTypeStringConverter;

    @Autowired
    public FinanceController(FinanceService financeService, UserService userService, SettingService settingService, FinanceTypeStringConverter financeTypeStringConverter) {
        this.financeService = financeService;
        this.userService = userService;
        this.settingService = settingService;
        this.financeTypeStringConverter = financeTypeStringConverter;
    }

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public String showFinances(@RequestParam(value = "display", defaultValue = "expense") String financeType,
            @RequestParam(value = "displayPeriod", defaultValue = "day") String displayPeriod, Model model){
        // user info
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName()).get();
        model.addAttribute("user", user);
        model.addAttribute("settings", settingService.findSettingByUser(user).get());

        // index
        switch(displayPeriod){
            case "all-time":
                model.addAttribute("finances", financeService.findAllByType(FinanceType.valueOf(financeType.toUpperCase())));;
                model.addAttribute("expense_total", financeService.getAllTimeFinanceTotalByType(FinanceType.EXPENSE));
                model.addAttribute("income_total", financeService.getAllTimeFinanceTotalByType(FinanceType.INCOME));
                break;
            case "month":
                model.addAttribute("finances", financeService.findAllByMonthAndType(LocalDate.now().getMonth(), FinanceType.valueOf(financeType.toUpperCase())));
                model.addAttribute("expense_total", financeService.getMonthlyFinanceTotalByType(FinanceType.EXPENSE));
                model.addAttribute("income_total", financeService.getMonthlyFinanceTotalByType(FinanceType.INCOME));
                break;
            case "week":
                model.addAttribute("finances", financeService.findAllByWeekAndType(LocalDate.now(), FinanceType.valueOf(financeType.toUpperCase())));
                model.addAttribute("expense_total", financeService.getWeeklyFinanceTotalByType(FinanceType.EXPENSE));
                model.addAttribute("income_total", financeService.getWeeklyFinanceTotalByType(FinanceType.INCOME));
                break;
            case "day":
                model.addAttribute("finances", financeService.findAllByDateAndType(LocalDate.now(), FinanceType.valueOf(financeType.toUpperCase())));
                model.addAttribute("expense_total", financeService.getDailyFinanceTotalByType(FinanceType.EXPENSE));
                model.addAttribute("income_total", financeService.getDailyFinanceTotalByType(FinanceType.INCOME));
                break;
        }

        // empty finance for creating a new one
        model.addAttribute("new_finance", new Finance());

        // add request params
        model.addAttribute("displayPeriod", displayPeriod);
        model.addAttribute("financeType", financeType);

        return "finance/list";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createFinance(@RequestParam("type") String type, Model model){
        model.addAttribute("new_finance", new Finance());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("user", userService.findUserByEmail(authentication.getName()).get());
        model.addAttribute("financeType", type);

        return "finance/create";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addFinance(@RequestParam("type") String type, @ModelAttribute("new_finance") @Valid Finance finance, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "finance/create";
        }
        finance.setType(financeTypeStringConverter.convert(type));
        if(finance.getComment().isEmpty()){
            finance.setComment("No comment.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(finance.getType() == FinanceType.INCOME){
            userService.findUserByEmail(authentication.getName()).get().plusBalance(finance.getAmount());
        } else userService.findUserByEmail(authentication.getName()).get().minusBalance(finance.getAmount());

        financeService.save(finance);

        return "redirect:/finances/show?display=" + type;
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editFinance(@PathVariable("id") int id, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<Finance> finance = financeService.findById(id);
        if(finance.isEmpty()){
            model.addAttribute("user", userService.findUserByEmail(authentication.getName()).get());
            model.addAttribute("errorTitle", "Page not found");
            model.addAttribute("errorMessage", "404. Nothing was found.");
            return "/error";
        }

        model.addAttribute("finance", finance.get());
        model.addAttribute("financeType", finance.get().getType());
        model.addAttribute("user", userService.findUserByEmail(authentication.getName()).get());

        return "/finance/edit";
    }

    @RequestMapping(value = "/edit/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String updateFinance(@PathVariable("id") int id, @ModelAttribute("finance") @Valid Finance finance, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "/finance/edit";
        }
        Finance financeToUpdate = financeService.findById(id).get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName()).get();

        if(finance.getType() == FinanceType.EXPENSE){
            user.plusBalance(financeToUpdate.getAmount());
            user.minusBalance(finance.getAmount());
        }
        else {
            user.plusBalance(finance.getAmount());
            user.minusBalance(financeToUpdate.getAmount());
        }

        financeService.update(financeToUpdate, finance);

        return "redirect:/finances/show?display=" + financeToUpdate.getType().toString().toLowerCase();
    }

    @RequestMapping(value = "/delete/{id}", method = {RequestMethod.DELETE, RequestMethod.POST})
    public String deleteFinance(@PathVariable("id") int id){

        Finance finance = financeService.findById(id).get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(finance.getType() == FinanceType.INCOME){
            userService.findUserByEmail(authentication.getName()).get().minusBalance(finance.getAmount());
        } else userService.findUserByEmail(authentication.getName()).get().plusBalance(finance.getAmount());

        financeService.delete(id);

        return "redirect:/finances/show";
    }
}
