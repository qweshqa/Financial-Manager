package org.qweshqa.financialmanager.controllers;

import jakarta.validation.Valid;
import org.qweshqa.financialmanager.models.Budget;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.BudgetService;
import org.qweshqa.financialmanager.services.CategoryService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.AmountFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
}
