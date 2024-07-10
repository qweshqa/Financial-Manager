package org.qweshqa.financialmanager.controllers;

import jakarta.validation.Valid;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.CategoryService;
import org.qweshqa.financialmanager.services.OperationService;
import org.qweshqa.financialmanager.services.SettingService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.AmountFormatter;
import org.qweshqa.financialmanager.utils.enums.OperationType;
import org.qweshqa.financialmanager.utils.converters.OperationTypeStringConverter;
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
@RequestMapping("/operations")
public class OperationController {

    private final OperationService operationService;

    private final UserService userService;

    private final SettingService settingService;

    private final OperationTypeStringConverter operationTypeStringConverter;

    private final AmountFormatter amountFormatter;
    private final CategoryService categoryService;

    @Autowired
    public OperationController(OperationService operationService, UserService userService, SettingService settingService, OperationTypeStringConverter operationTypeStringConverter, AmountFormatter amountFormatter, CategoryService categoryService) {
        this.operationService = operationService;
        this.userService = userService;
        this.settingService = settingService;
        this.operationTypeStringConverter = operationTypeStringConverter;
        this.amountFormatter = amountFormatter;
        this.categoryService = categoryService;
    }

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public String viewOperations(@RequestParam(value = "display", defaultValue = "expense") String operationType,
            @RequestParam(value = "displayPeriod", defaultValue = "day") String displayPeriod, Model model){
        // user info
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName()).get();
        model.addAttribute("user", user);
        model.addAttribute("settings", settingService.findSettingByUser(user).get());

        // index
        switch(displayPeriod){
            case "all-time":
                model.addAttribute("operations", operationService.findAllByType(OperationType.valueOf(operationType.toUpperCase()), user));;
                model.addAttribute("expense_total", operationService.getAllTimeOperationTotalByType(OperationType.EXPENSE, user));
                model.addAttribute("income_total", operationService.getAllTimeOperationTotalByType(OperationType.INCOME, user));
                break;
            case "month":
                model.addAttribute("operations", operationService.findAllByMonthAndType(LocalDate.now().getMonth(), OperationType.valueOf(operationType.toUpperCase()), user));
                model.addAttribute("expense_total", operationService.getMonthlyOperationTotalByType(OperationType.EXPENSE, user));
                model.addAttribute("income_total", operationService.getMonthlyOperationTotalByType(OperationType.INCOME, user));
                break;
            case "week":
                model.addAttribute("operations", operationService.findAllByWeekAndType(LocalDate.now(), OperationType.valueOf(operationType.toUpperCase()), user));
                model.addAttribute("expense_total", operationService.getWeeklyOperationTotalByType(OperationType.EXPENSE, user));
                model.addAttribute("income_total", operationService.getWeeklyOperationTotalByType(OperationType.INCOME, user));
                break;
            case "day":
                model.addAttribute("operations", operationService.findAllByDateAndType(LocalDate.now(), OperationType.valueOf(operationType.toUpperCase()), user));
                model.addAttribute("expense_total", operationService.getDailyOperationTotalByType(OperationType.EXPENSE, user));
                model.addAttribute("income_total", operationService.getDailyOperationTotalByType(OperationType.INCOME, user));
                break;
        }
        model.addAttribute("amountFormatter", amountFormatter);

        // empty finance for creating a new one
        model.addAttribute("new_operation", new Operation());

        // add request params
        model.addAttribute("displayPeriod", displayPeriod);
        model.addAttribute("operationType", operationType);

        return "/operations/list";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createOperation(@RequestParam("type") String type, Model model){
        model.addAttribute("new_operation", new Operation());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName()).get();

        model.addAttribute("user", user);
        model.addAttribute("userAccounts", userService.findUserByEmail(authentication.getName()).get().getUserAccounts());

        model.addAttribute("operationType", type);

        model.addAttribute("amountFormatter", amountFormatter);

        model.addAttribute("userCategories", categoryService.findAllByUser(user));

        return "operations/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String addOperation(@ModelAttribute("new_operation") @Valid Operation operation, BindingResult bindingResult, @RequestParam("type") String type, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(bindingResult.hasErrors()){
            model.addAttribute("amountFormatter", amountFormatter);
            model.addAttribute("operationType", type);
            model.addAttribute("userAccounts", userService.findUserByEmail(authentication.getName()).get().getUserAccounts());
            model.addAttribute("user", userService.findUserByEmail(authentication.getName()).get());
            return "operations/create";
        }

        User user = userService.findUserByEmail(authentication.getName()).get();

        operationService.processOperationSetup(operation, user, operationTypeStringConverter.convert(type));

        operationService.save(operation);

        return "redirect:/operations/show?display=" + type;
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editOperation(@PathVariable("id") int id, Model model){

        Optional<Operation> operation = operationService.findById(id);

        if(operation.isEmpty()){
            model.addAttribute("errorTitle", "Page not found");
            model.addAttribute("errorMessage", "404. Nothing was found.");
            return "/error";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("user", userService.findUserByEmail(authentication.getName()).get());

        model.addAttribute("amountFormatter", amountFormatter);

        model.addAttribute("operation", operation.get());

        model.addAttribute("operationType", operation.get().getType());

        return "/operations/edit";
    }

    @RequestMapping(value = "/edit/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String updateOperation(@PathVariable("id") int id, @ModelAttribute("operation") @Valid Operation operation, BindingResult bindingResult,
                                  Model model){

        if(bindingResult.hasErrors()){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            model.addAttribute("user", userService.findUserByEmail(authentication.getName()).get());
            model.addAttribute("amountFormatter", amountFormatter);
            return "/operations/edit";
        }
        Operation operationToUpdate = operationService.findById(id).get();

        operation.setInvolvedAccount(operationToUpdate.getInvolvedAccount());
        operation.setUser(operationToUpdate.getUser());
        operation.setCategory(operationToUpdate.getCategory());

        operationService.processOperationEdit(operation, operationToUpdate);

        operationService.update(id, operation);

        return "redirect:/operations/show?display=" + operationToUpdate.getType().toString().toLowerCase();
    }

    @RequestMapping(value = "/delete/{id}", method = {RequestMethod.DELETE, RequestMethod.POST})
    public String deleteOperation(@PathVariable("id") int id){

        Operation operation = operationService.findById(id).get();

        operationService.processOperationDelete(operation);

        operationService.delete(id);

        return "redirect:/operations/show?display=" + operation.getType().toString().toLowerCase();
    }
}
