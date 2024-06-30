package org.qweshqa.financialmanager.controllers;

import org.qweshqa.financialmanager.models.Account;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.AccountService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.AccountType;
import org.qweshqa.financialmanager.utils.AmountFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    private final UserService userService;

    private final AmountFormatter amountFormatter;

    @Autowired
    public AccountController(AccountService accountService, UserService userService, AmountFormatter amountFormatter) {
        this.accountService = accountService;
        this.userService = userService;
        this.amountFormatter = amountFormatter;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewAccounts(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName()).get();

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);
        model.addAttribute("currency", user.getSetting().getCurrencyUnit());

        List<Account> financialAccounts = accountService.findAllUserAccountsByType(user, AccountType.FINANCIAL);
        List<Account> savingsAccounts = accountService.findAllUserAccountsByType(user, AccountType.SAVINGS);

        model.addAttribute("financialAccounts", financialAccounts);
        model.addAttribute("savingsAccounts", savingsAccounts);

        return "accounts/list";
    }
}
