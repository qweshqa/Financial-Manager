package org.qweshqa.financialmanager.controllers;

import jakarta.validation.Valid;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.CategoryService;
import org.qweshqa.financialmanager.services.OperationService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.AmountFormatter;
import org.qweshqa.financialmanager.utils.enums.OperationType;
import org.qweshqa.financialmanager.utils.converters.OperationTypeStringConverter;
import org.qweshqa.financialmanager.utils.exceptions.OperationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/operations")
public class OperationController {

    private final OperationService operationService;

    private final UserService userService;

    private final OperationTypeStringConverter operationTypeStringConverter;

    private final AmountFormatter amountFormatter;

    private final CategoryService categoryService;

    @Autowired
    public OperationController(OperationService operationService, UserService userService, OperationTypeStringConverter operationTypeStringConverter, AmountFormatter amountFormatter, CategoryService categoryService) {
        this.operationService = operationService;
        this.userService = userService;
        this.operationTypeStringConverter = operationTypeStringConverter;
        this.amountFormatter = amountFormatter;
        this.categoryService = categoryService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewOperations(@RequestParam(value = "p", defaultValue = "day") String period, Model model){
        // user info
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("settings", user.getSetting());

        // index
        switch(period){
            case "all-time":
                model.addAttribute("operations", operationService.findAllByUser(user));
                break;
            case "month":
                model.addAttribute("operations", operationService.findAllByMonthAndUser(LocalDate.now().getMonth(), user));
                break;
            case "week":
                model.addAttribute("operations", operationService.findAllByWeekAndUser(LocalDate.now(), user));
                break;
            case "day":
                model.addAttribute("operations", operationService.findAllByDateAndUser(LocalDate.now(), user));
                break;
        }
        model.addAttribute("amountFormatter", amountFormatter);

        // empty finance for creating a new one
        model.addAttribute("new_operation", new Operation());

        // add request params
        model.addAttribute("displayPeriod", period);

        return "/operations/list";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createOperation(Model model){
        model.addAttribute("new_operation", new Operation());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        model.addAttribute("user", user);
        model.addAttribute("userAccounts", userService.findUserByEmail(authentication.getName()).getUserAccounts());

        model.addAttribute("amountFormatter", amountFormatter);

        model.addAttribute("userCategories", categoryService.findAllByUser(user));

        return "operations/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createOperation(@ModelAttribute("new_operation") @Valid Operation operation, BindingResult bindingResult, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(bindingResult.hasErrors()){
            model.addAttribute("amountFormatter", amountFormatter);
            model.addAttribute("userAccounts", userService.findUserByEmail(authentication.getName()).getUserAccounts());
            model.addAttribute("user", userService.findUserByEmail(authentication.getName()));
            return "operations/create";
        }

        User user = userService.findUserByEmail(authentication.getName());
        operation.setUser(user);

        operationService.processOperationSetup(operation);

        operationService.save(operation);

        return "redirect:/operations";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editOperation(@PathVariable("id") int id, Model model){
        Operation operation;

        try{
            operation = operationService.findById(id);
        } catch (OperationNotFoundException e){
            model.addAttribute("errorTitle", "Page not found");
            model.addAttribute("errorMessage", "404. Nothing was found.");
            return "/error";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("user", userService.findUserByEmail(authentication.getName()));

        model.addAttribute("amountFormatter", amountFormatter);

        model.addAttribute("operation", operation);

        model.addAttribute("operationType", operation.getType());

        return "/operations/edit";
    }

    @RequestMapping(value = "/edit/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String editOperation(@PathVariable("id") int id, @ModelAttribute("operation") @Valid Operation operation, BindingResult bindingResult,
                                  Model model){

        if(bindingResult.hasErrors()){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            model.addAttribute("user", userService.findUserByEmail(authentication.getName()));
            model.addAttribute("amountFormatter", amountFormatter);
            return "/operations/edit";
        }
        Operation operationToUpdate = operationService.findById(id);

        operation.setInvolvedAccount(operationToUpdate.getInvolvedAccount());
        operation.setUser(operationToUpdate.getUser());
        operation.setCategory(operationToUpdate.getCategory());

        operationService.processOperationEdit(operation, operationToUpdate);

        operationService.update(id, operation);

        return "redirect:/operations";
    }

    @RequestMapping(value = "/delete/{id}", method = {RequestMethod.DELETE, RequestMethod.POST})
    public String deleteOperation(@PathVariable("id") int id){

        Operation operation = operationService.findById(id);

        operationService.processOperationDelete(operation);

        operationService.delete(id);

        return "redirect:/operations";
    }
}
