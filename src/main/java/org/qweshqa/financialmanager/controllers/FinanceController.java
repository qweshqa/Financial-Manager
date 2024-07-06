package org.qweshqa.financialmanager.controllers;

import jakarta.validation.Valid;
import org.qweshqa.financialmanager.models.Finance;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.FinanceService;
import org.qweshqa.financialmanager.services.SettingService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.AmountFormatter;
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

    private final AmountFormatter amountFormatter;

    @Autowired
    public FinanceController(FinanceService financeService, UserService userService, SettingService settingService, FinanceTypeStringConverter financeTypeStringConverter, AmountFormatter amountFormatter) {
        this.financeService = financeService;
        this.userService = userService;
        this.settingService = settingService;
        this.financeTypeStringConverter = financeTypeStringConverter;
        this.amountFormatter = amountFormatter;
    }

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public String viewFinances(@RequestParam(value = "display", defaultValue = "expense") String financeType,
            @RequestParam(value = "displayPeriod", defaultValue = "day") String displayPeriod, Model model){
        // user info
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName()).get();
        model.addAttribute("user", user);
        model.addAttribute("settings", settingService.findSettingByUser(user).get());

        // index
        switch(displayPeriod){
            case "all-time":
                model.addAttribute("finances", financeService.findAllByType(FinanceType.valueOf(financeType.toUpperCase()), user));;
                model.addAttribute("expense_total", financeService.getAllTimeFinanceTotalByType(FinanceType.EXPENSE, user));
                model.addAttribute("income_total", financeService.getAllTimeFinanceTotalByType(FinanceType.INCOME, user));
                break;
            case "month":
                model.addAttribute("finances", financeService.findAllByMonthAndType(LocalDate.now().getMonth(), FinanceType.valueOf(financeType.toUpperCase()), user));
                model.addAttribute("expense_total", financeService.getMonthlyFinanceTotalByType(FinanceType.EXPENSE, user));
                model.addAttribute("income_total", financeService.getMonthlyFinanceTotalByType(FinanceType.INCOME, user));
                break;
            case "week":
                model.addAttribute("finances", financeService.findAllByWeekAndType(LocalDate.now(), FinanceType.valueOf(financeType.toUpperCase()), user));
                model.addAttribute("expense_total", financeService.getWeeklyFinanceTotalByType(FinanceType.EXPENSE, user));
                model.addAttribute("income_total", financeService.getWeeklyFinanceTotalByType(FinanceType.INCOME, user));
                break;
            case "day":
                model.addAttribute("finances", financeService.findAllByDateAndType(LocalDate.now(), FinanceType.valueOf(financeType.toUpperCase()), user));
                model.addAttribute("expense_total", financeService.getDailyFinanceTotalByType(FinanceType.EXPENSE, user));
                model.addAttribute("income_total", financeService.getDailyFinanceTotalByType(FinanceType.INCOME, user));
                break;
        }
        model.addAttribute("amountFormatter", amountFormatter);

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
        model.addAttribute("userAccounts", userService.findUserByEmail(authentication.getName()).get().getUserAccounts());

        model.addAttribute("financeType", type);

        model.addAttribute("amountFormatter", amountFormatter);

        return "finance/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String addFinance(@ModelAttribute("new_finance") @Valid Finance finance, BindingResult bindingResult, @RequestParam("type") String type, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(bindingResult.hasErrors()){
            model.addAttribute("amountFormatter", amountFormatter);
            model.addAttribute("financeType", type);
            model.addAttribute("userAccounts", userService.findUserByEmail(authentication.getName()).get().getUserAccounts());
            model.addAttribute("user", userService.findUserByEmail(authentication.getName()).get());
            return "finance/create";
        }

        User user = userService.findUserByEmail(authentication.getName()).get();

        financeService.processFinanceSetup(finance, user, financeTypeStringConverter.convert(type));

        financeService.save(finance);

        return "redirect:/finances/show?display=" + type;
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editFinance(@PathVariable("id") int id, @RequestParam("displayPeriod") String displayPeriod,
                              Model model){

        Optional<Finance> finance = financeService.findById(id);

        if(finance.isEmpty()){
            model.addAttribute("errorTitle", "Page not found");
            model.addAttribute("errorMessage", "404. Nothing was found.");
            return "/error";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("user", userService.findUserByEmail(authentication.getName()).get());

        model.addAttribute("amountFormatter", amountFormatter);

        model.addAttribute("finance", finance.get());

        model.addAttribute("financeType", finance.get().getType());
        model.addAttribute("displayPeriod", displayPeriod);

        return "/finance/edit";
    }

    @RequestMapping(value = "/edit/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String updateFinance(@PathVariable("id") int id, @ModelAttribute("finance") @Valid Finance finance, BindingResult bindingResult,
                                @RequestParam("displayPeriod") String displayPeriod, Model model){

        if(bindingResult.hasErrors()){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            model.addAttribute("user", userService.findUserByEmail(authentication.getName()).get());
            model.addAttribute("amountFormatter", amountFormatter);
            return "/finance/edit";
        }
        Finance financeToUpdate = financeService.findById(id).get();

        finance.setInvolvedAccount(financeToUpdate.getInvolvedAccount());
        finance.setUser(financeToUpdate.getUser());

        financeService.processFinanceEdit(finance, financeToUpdate);

        financeService.update(id, finance);

        return "redirect:/finances/show?display=" + financeToUpdate.getType().toString().toLowerCase() +
                "&displayPeriod=" + displayPeriod;
    }

    @RequestMapping(value = "/delete/{id}", method = {RequestMethod.DELETE, RequestMethod.POST})
    public String deleteFinance(@PathVariable("id") int id, @RequestParam("display") String display, @RequestParam("displayPeriod") String displayPeriod){

        Finance finance = financeService.findById(id).get();

        financeService.processFinanceDelete(finance);

        financeService.delete(id);

        return "redirect:/finances/show?display=" + display +
                "&displayPeriod=" + displayPeriod ;
    }
}
