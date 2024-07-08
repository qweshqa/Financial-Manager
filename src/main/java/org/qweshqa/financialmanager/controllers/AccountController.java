package org.qweshqa.financialmanager.controllers;

import jakarta.validation.Valid;
import org.qweshqa.financialmanager.models.Account;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.AccountService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.enums.AccountType;
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

        List<Account> financialAccounts = accountService.findAllUserAccountsByTypeAndArchive(user, AccountType.FINANCIAL, false);
        List<Account> savingsAccounts = accountService.findAllUserAccountsByTypeAndArchive(user, AccountType.SAVINGS, false);
        List<Account> archivedAccounts = accountService.findAllUserAccountsByArchive(user, true);

        model.addAttribute("financialAccounts", financialAccounts);
        model.addAttribute("savingsAccounts", savingsAccounts);
        model.addAttribute("archivedAccounts", archivedAccounts);

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
        account.setOwner(user);

        accountService.save(account);

        return "redirect:/accounts";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editAccount(@PathVariable("id") int id, Model model){
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

        return "accounts/edit";
    }

    @RequestMapping(value = "/edit/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String editAccount(@ModelAttribute("account") @Valid Account account, BindingResult bindingResult, Model model,
                              @PathVariable("id") int id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName()).get();

        if(bindingResult.hasErrors()){
            model.addAttribute("user", user);
            model.addAttribute("amountFormatter", amountFormatter);
            return "accounts/edit";
        }
        account.setOwner(user);

        accountService.update(id, account);

        return "redirect:/accounts/" + id;
    }

    @RequestMapping(value = "/delete/{id}", method = {RequestMethod.DELETE, RequestMethod.POST})
    public String deleteAccount(@PathVariable("id") int id){
        Account account = accountService.findById(id).get();

        accountService.delete(account);

        return "redirect:/accounts";
    }

    @RequestMapping(value = "/replenish/{id}", method = RequestMethod.GET)
    public String replenishAccount(@PathVariable("id") int id, Model model){
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

        List<Account> userAccounts = accountService.findAllByUser(user);

        userAccounts.remove(account.get());

        model.addAttribute("account", account.get());
        model.addAttribute("userAccounts", userAccounts);

        return "accounts/replenish";
    }

    @RequestMapping(value = "/replenish/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String replenishAccount(@PathVariable("id") int id, @RequestParam("fromAcc") int accountId, @RequestParam("amount") float amount){
        Account toAccount = accountService.findById(id).get();
        Account fromAccount = accountService.findById(accountId).get();

        accountService.replenish(fromAccount, toAccount, amount);

        return "redirect:/accounts/" + id;
    }

    @RequestMapping(value = "/archive/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String archiveAccount(@PathVariable("id") int id){
        Account account = accountService.findById(id).get();

        accountService.archiveAccount(account);

        return "redirect:/accounts";
    }

    @RequestMapping(value = "/unzip/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String unzipAccount(@PathVariable("id") int id){
        Account account = accountService.findById(id).get();

        accountService.unzipAccount(account);

        return "redirect:/accounts";
    }

}