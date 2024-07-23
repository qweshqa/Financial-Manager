package org.qweshqa.financialmanager.controllers;

import jakarta.validation.Valid;
import org.qweshqa.financialmanager.models.Account;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.AccountService;
import org.qweshqa.financialmanager.services.OperationService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.DateWrapper;
import org.qweshqa.financialmanager.utils.enums.AccountType;
import org.qweshqa.financialmanager.utils.converters.AccountTypeStringConverter;
import org.qweshqa.financialmanager.utils.AmountFormatter;
import org.qweshqa.financialmanager.utils.enums.CategoryType;
import org.qweshqa.financialmanager.utils.exceptions.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    private final UserService userService;

    private final AmountFormatter amountFormatter;

    private final AccountTypeStringConverter accountTypeStringConverter;

    private final OperationService operationService;

    @Autowired
    public AccountController(AccountService accountService, UserService userService, AmountFormatter amountFormatter, AccountTypeStringConverter accountTypeStringConverter, OperationService operationService) {
        this.accountService = accountService;
        this.userService = userService;
        this.amountFormatter = amountFormatter;
        this.accountTypeStringConverter = accountTypeStringConverter;
        this.operationService = operationService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewAccount(@PathVariable("id") int id,
                              @RequestParam(value = "p", defaultValue = "day") String operationDisplayPeriod,
                              @RequestParam(value = "d", defaultValue = "") String day,
                              @RequestParam(value = "m", defaultValue = "") String month,
                              @RequestParam(value = "y", defaultValue = "") String year,
                              Model model){
        Account account;

        try{
            account = accountService.findById(id);
        } catch (AccountNotFoundException e){
            model.addAttribute("errorTitle", "404 Nothing found");
            model.addAttribute("errorMessage", "Account with this id doesn't exist");
            return "error";
        }

        DateWrapper dateWrapper = new DateWrapper(LocalDate.now());

        try{
            operationService.configureStringDateValues(year, month, day, operationDisplayPeriod, dateWrapper);
        } catch (DateTimeException e){
            switch (e.getMessage()){
                case "Year period error":
                    return "redirect:/accounts/" + id + "?p=year" +
                            "&y=" + dateWrapper.getDate().getYear();

                case "Month period error":
                    return "redirect:/accounts/" + id + "?p=month" +
                            "&y=" + dateWrapper.getDate().getYear() +
                            "&m=" + dateWrapper.getDate().getMonth().getValue();

                case "Day period error":
                    return "redirect:/accounts/" + id + "?p=day" +
                            "&y=" + dateWrapper.getDate().getYear() +
                            "&m=" + dateWrapper.getDate().getMonth().getValue() +
                            "&d=" + dateWrapper.getDate().getDayOfMonth();
            }
        }
        model.addAttribute("account", account);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);
        model.addAttribute("settings", user.getSetting());

        LocalDate date = dateWrapper.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

        List<Operation> accountOperations = new ArrayList<>();
        switch(operationDisplayPeriod){
            case "all-time":
                accountOperations = accountService.findAllOperationsByUser(account, user);
                model.addAttribute("displayDate", "All time");
                break;

            case "year":
                if(!year.isBlank()){
                    date = date.withYear(Integer.parseInt(year));
                }
                accountOperations = accountService.findAllOperationsByUserAndYear(account, user, date.getYear());
                model.addAttribute("displayDate", date.getYear());
                break;

            case "month":
                if(!year.isBlank()){
                    date = date.withYear(Integer.parseInt(year));
                }
                if(!month.isBlank()){
                    date = date.withMonth(Integer.parseInt(month));
                }

                accountOperations = accountService.findAllOperationsByUserAndMonth(account, user, date);
                model.addAttribute("displayDate", (date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ", " + date.getYear()));
                break;

            case "day":
                if(!year.isBlank()){
                    date = date.withYear(Integer.parseInt(year));
                }
                if(!month.isBlank()){
                    date = date.withMonth(Integer.parseInt(month));
                }
                if(!day.isBlank()){
                    date = date.withDayOfMonth(Integer.parseInt(day));
                }

                accountOperations = accountService.findAllOperationsByUserAndDate(account, user, date);
                model.addAttribute("displayDate", date.format(formatter));
                break;
        }
        model.addAttribute("accountOperations", accountOperations);
        float account_balance = (float) accountOperations.stream().filter(operation -> operation.getCategory().getCategoryType() == CategoryType.INCOME).mapToDouble(Operation::getAmount).sum()
                - (float) accountOperations.stream().filter(operation -> operation.getCategory().getCategoryType() == CategoryType.EXPENSE).mapToDouble(Operation::getAmount).sum();

        model.addAttribute("account_balance", account_balance);

        model.addAttribute("period", operationDisplayPeriod);

        model.addAttribute("date", date);

        return "/accounts/view";
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewAccounts(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);
        model.addAttribute("currency", user.getSetting().getCurrencyUnit());

        List<Account> financialAccounts = accountService.findAllByUserAndArchiveAndType(user, false, AccountType.FINANCIAL);
        List<Account> savingsAccounts = accountService.findAllByUserAndArchiveAndType(user, false, AccountType.SAVINGS);

        model.addAttribute("financialAccounts", financialAccounts);
        model.addAttribute("savingsAccounts", savingsAccounts);

        return "accounts/list";
    }

    @RequestMapping(value = "/archived", method = RequestMethod.GET)
    public String viewArchivedAccounts(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);
        model.addAttribute("currency", user.getSetting().getCurrencyUnit());

        List<Account> archivedAccounts = accountService.findAllByUserAndArchive(user, true);

        model.addAttribute("archivedAccounts", archivedAccounts);

        return "accounts/archivedList";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createAccount(@RequestParam("accType") String strAccountType, Model model){
        model.addAttribute("accountType", strAccountType);
        model.addAttribute("account", new Account());

        return "accounts/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createAccount(@ModelAttribute("account") @Valid Account account, BindingResult bindingResult, @RequestParam("accType") String accountType, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("accountType", accountType);
            return "accounts/create";
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        account.setType(accountTypeStringConverter.convert(accountType));
        account.setOwner(user);

        accountService.save(account);

        return "redirect:/accounts";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editAccount(@PathVariable("id") int id, Model model){
        Account account;

        try{
            account = accountService.findById(id);
        } catch (AccountNotFoundException e){
            model.addAttribute("errorTitle", "404 Nothing found");
            model.addAttribute("errorMessage", "Account with this id doesn't exist");
            return "error";
        }

        model.addAttribute("account", account);

        return "accounts/edit";
    }

    @RequestMapping(value = "/edit/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String editAccount(@ModelAttribute("account") @Valid Account account, BindingResult bindingResult, Model model,
                              @PathVariable("id") int id){
        if(bindingResult.hasErrors()){
            return "accounts/edit";
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        account.setOwner(user);

        accountService.update(id, account);

        return "redirect:/accounts/" + id;
    }

    @RequestMapping(value = "/delete/{id}", method = {RequestMethod.DELETE, RequestMethod.POST})
    public String deleteAccount(@PathVariable("id") int id){
        Account account = accountService.findById(id);

        accountService.delete(account);

        return "redirect:/accounts";
    }

    @RequestMapping(value = "/replenish/{id}", method = RequestMethod.GET)
    public String replenishAccount(@PathVariable("id") int id, Model model){
        Account account;

        try{
            account = accountService.findById(id);
        } catch (AccountNotFoundException e){
            model.addAttribute("errorTitle", "404 Nothing found");
            model.addAttribute("errorMessage", "Account with this id doesn't exist");
            return "error";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        List<Account> userAccounts = accountService.findAllByUser(user);

        userAccounts.remove(account);

        model.addAttribute("account", account);
        model.addAttribute("userAccounts", userAccounts);

        return "accounts/replenish";
    }

    @RequestMapping(value = "/replenish/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String replenishAccount(@PathVariable("id") int id, @RequestParam("fromAcc") int accountId, @RequestParam("amount") float amount){
        Account toAccount = accountService.findById(id);
        Account fromAccount = accountService.findById(accountId);

        accountService.replenish(fromAccount, toAccount, amount);

        return "redirect:/accounts/" + id;
    }

    @RequestMapping(value = "/archive/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String archiveAccount(@PathVariable("id") int id){
        Account account = accountService.findById(id);

        accountService.archiveAccount(account);

        return "redirect:/accounts";
    }

    @RequestMapping(value = "/unzip/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String unzipAccount(@PathVariable("id") int id){
        Account account = accountService.findById(id);

        accountService.unzipAccount(account);

        return "redirect:/accounts";
    }

}