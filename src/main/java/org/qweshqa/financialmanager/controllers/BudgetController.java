package org.qweshqa.financialmanager.controllers;

import jakarta.validation.Valid;
import org.qweshqa.financialmanager.models.Budget;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.BudgetService;
import org.qweshqa.financialmanager.services.CategoryService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.AmountFormatter;
import org.qweshqa.financialmanager.utils.exceptions.BudgetNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/budgets")
public class BudgetController {

    private final UserService userService;

    private final BudgetService budgetService;

    private final CategoryService categoryService;

    private final AmountFormatter amountFormatter;

    @Autowired
    public BudgetController(UserService userService, BudgetService budgetService, AmountFormatter amountFormatter, CategoryService categoryService) {
        this.userService = userService;
        this.budgetService = budgetService;
        this.categoryService = categoryService;
        this.amountFormatter = amountFormatter;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewBudgets(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);
        model.addAttribute("currency", user.getSetting().getCurrencyUnit());

        List<Budget> budgets = budgetService.findAllByUserId(user.getId());

        model.addAttribute("budgets", budgets);

        model.addAttribute("categoryService", categoryService);

        return "budgets/list";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createBudget(@RequestParam("on") String budgetOn, Model model){
        if(!budgetOn.equals("expenses") && !budgetOn.equals("category")){
            return "redirect:/budgets/create?on=" + budgetOn;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);
        model.addAttribute("currency", user.getSetting().getCurrencyUnit());

        if(budgetOn.equals("category")){
            model.addAttribute("all_categories", categoryService.findAllByUser(user));
        }

        model.addAttribute("budget_on", budgetOn);

        Budget budget = new Budget();
        budget.setUserId(user.getId());
        if(budgetOn.equals("expenses")){
            budget.setOnAllExpenses(true);
            budget.setCategoryId(-1);
        }

        model.addAttribute("budget", budget);

        return "budgets/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createBudget(@ModelAttribute("budget") @Valid Budget budget, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "redirect:/budgets/create";
        }

        budgetService.save(budget);

        return "redirect:/budgets";
    }

    @RequestMapping(value = "/edit/{uuid}", method = RequestMethod.GET)
    public String editBudget(@PathVariable("uuid") String uuid, @RequestParam(value = "on", defaultValue = "expenses") String budgetOn, Model model){
        Budget budget;

        try{
            budget = budgetService.findByUuid(uuid);
        } catch (BudgetNotFoundException e){
            model.addAttribute("errorTitle", "404 Nothing found");
            model.addAttribute("errorMessage", "Budget with this uuid doesn't exist");
            return "error";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);
        model.addAttribute("currency", user.getSetting().getCurrencyUnit());

        model.addAttribute("budget", budget);

        if(budgetOn.equals("category")){
            model.addAttribute("all_categories", categoryService.findAllByUser(user));
        }

        model.addAttribute("on", budgetOn);

        return "budgets/edit";
    }

    @RequestMapping(value = "/edit/{uuid}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String editBudget(@PathVariable("uuid") String uuid, @RequestParam("on") String budgetOn, @ModelAttribute("budget") @Valid Budget updatedBudget, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "budgets/edit";
        }

        Budget budget = budgetService.findByUuid(uuid);

        if(budgetOn.equals("category")){
            updatedBudget.setOnAllExpenses(false);
        }
        else{
            updatedBudget.setOnAllExpenses(true);
        }

        budgetService.update(budget, updatedBudget);

        return "redirect:/budgets";
    }
}
