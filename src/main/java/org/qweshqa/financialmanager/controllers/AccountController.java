package org.qweshqa.financialmanager.controllers;

import jakarta.validation.Valid;
import org.qweshqa.financialmanager.models.Account;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.AccountService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.AccountType;
import org.qweshqa.financialmanager.utils.AccountTypeStringConverter;
import org.qweshqa.financialmanager.utils.AmountFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    private final UserService userService;

    private final AmountFormatter amountFormatter;

    private final AccountTypeStringConverter accountTypeStringConverter;

    @Autowired
    public AccountController(AccountService accountService, UserService userService, AmountFormatter amountFormatter, AccountTypeStringConverter accountTypeStringConverter) {
        this.accountService = accountService;
        this.userService = userService;
        this.amountFormatter = amountFormatter;
        this.accountTypeStringConverter = accountTypeStringConverter;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewAccount(@PathVariable("id") int id, Model model){
        Optional<Account> account = accountService.findById(id);

        if(account.isEmpty()){
            model.addAttribute("errorTitle", "404 Nothing found");
            model.addAttribute("errorMessage", "Account with this id doesn't exist");
            return "error";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName()).get();

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);

        model.addAttribute("account", account.get());

        return "accounts/view";
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

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createAccount(@RequestParam("accType") String strAccountType, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName()).get();

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);

        model.addAttribute("accountType", strAccountType);
        model.addAttribute("account", new Account());

        return "accounts/create";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addAccount(@ModelAttribute("account") @Valid Account account, BindingResult bindingResult, @RequestParam("accType") String accountType, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName()).get();

        if(bindingResult.hasErrors()){
            model.addAttribute("accountType", accountType);
            model.addAttribute("user", user);
            model.addAttribute("amountFormatter", amountFormatter);
            return "accounts/create";
        }

        account.setType(accountTypeStringConverter.convert(accountType));

        accountService.save(account, user);

        return "redirect:/accounts";
    }

    @RequestMapping(value = "/delete/{id}", method = {RequestMethod.DELETE, RequestMethod.POST})
    public String deleteAccount(@PathVariable("id") int id){
        Account account = accountService.findById(id).get();

        accountService.delete(account);

        return "redirect:/accounts";
    }
}