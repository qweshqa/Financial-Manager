package org.qweshqa.financialmanager.controllers;

import jakarta.validation.Valid;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.CategoryService;
import org.qweshqa.financialmanager.services.OperationService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.AmountFormatter;
import org.qweshqa.financialmanager.utils.exceptions.OperationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Controller
@RequestMapping("/operations")
public class OperationController {

    private final OperationService operationService;

    private final UserService userService;

    private final AmountFormatter amountFormatter;

    private final CategoryService categoryService;

    @Autowired
    public OperationController(OperationService operationService, UserService userService, AmountFormatter amountFormatter, CategoryService categoryService) {
        this.operationService = operationService;
        this.userService = userService;
        this.amountFormatter = amountFormatter;
        this.categoryService = categoryService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewOperations(@RequestParam(value = "p", defaultValue = "day") String period,
                                 @RequestParam(value = "d", defaultValue = "") String day,
                                 @RequestParam(value = "m", defaultValue = "") String month,
                                 @RequestParam(value = "y", defaultValue = "") String year,
                                 Model model){
        // user info
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("settings", user.getSetting());

        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

        // index
        switch(period){
            case "all-time":
                model.addAttribute("operations", operationService.findAllByUser(user));
                break;

            case "year":
                if(year.isBlank()){
                    model.addAttribute("operations", operationService.findAllByYearAndUser(LocalDate.now().getYear(), user));
                    model.addAttribute("displayDate", LocalDate.now().getYear());
                    break;
                }
                else{
                    if (Integer.parseInt(year) < 0) {
                        return "redirect:/operations?p=year" +
                                "&y=" + LocalDate.now().getYear();
                    }
                    else{
                        date = date.withYear(Integer.parseInt(year));
                        model.addAttribute("operations", operationService.findAllByYearAndUser(Integer.parseInt(year), user));
                        model.addAttribute("displayDate", Integer.parseInt(year));
                        break;
                    }
                }

            case "month":
                model.addAttribute("operations", operationService.findAllByMonthAndUser(LocalDate.now().getMonth(), user));

            case "week":
                model.addAttribute("operations", operationService.findAllByWeekAndUser(LocalDate.now(), user));

            case "day":
                if(day.isBlank()){
                    model.addAttribute("operations", operationService.findAllByDateAndUser(date, user));
                    model.addAttribute("displayDate", date.format(formatter));
                    break;
                }

                int yearValue = year.isBlank() ? date.getYear() : Integer.parseInt(year);
                if(Byte.parseByte(month) > 12){
                    date = date.withYear(yearValue + 1).withMonth(1).withDayOfMonth(1);
                    return "redirect:/operations?p=day" +
                            "&y=" + date.getYear() +
                            "&m=" + date.getMonth().getValue() +
                            "&d=" + date.getDayOfMonth();
                }
                if(Byte.parseByte(month) < 1){
                    date = date.withYear(yearValue - 1).withMonth(12).withDayOfMonth(31);
                    return "redirect:/operations?p=day" +
                            "&y=" + date.getYear() +
                            "&m=" + date.getMonth().getValue() +
                            "&d=" + date.getDayOfMonth();
                }

                Month monthValue = month.isBlank() ? date.getMonth() : Month.of(Byte.parseByte(month));
                if(Integer.parseInt(day) > monthValue.maxLength()){
                    date = date.withMonth(monthValue.getValue() + 1).withDayOfMonth(1);
                    return "redirect:/operations?p=" + period +
                            "&m=" + date.getMonth().getValue() +
                            "&d=" + date.getDayOfMonth();
                }
                else if(Integer.parseInt(day) < 1){
                    date = date.withMonth(monthValue.getValue() - 1).withDayOfMonth(Month.of(Byte.parseByte(month) - 1).maxLength());
                    return "redirect:/operations?p=" + period +
                            "&m=" + date.getMonth().getValue() +
                            "&d=" + date.getDayOfMonth();
                }
                else{
                    date = date.withYear(yearValue).withMonth(monthValue.getValue()).withDayOfMonth(Integer.parseInt(day));
                }

                model.addAttribute("operations", operationService.findAllByDateAndUser(date, user));
                model.addAttribute("displayDate", date.format(formatter));
                break;
        }
        model.addAttribute("amountFormatter", amountFormatter);

        // empty finance for creating a new one
        model.addAttribute("new_operation", new Operation());

        model.addAttribute("period", period);

        model.addAttribute("date", date);

        return "/operations/list";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createOperation(Model model){
        model.addAttribute("new_operation", new Operation());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        model.addAttribute("userAccounts", userService.findUserByEmail(authentication.getName()).getUserAccounts());

        model.addAttribute("userCategories", categoryService.findAllByUser(user));

        return "operations/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createOperation(@ModelAttribute("new_operation") @Valid Operation operation, BindingResult bindingResult, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(bindingResult.hasErrors()){
            model.addAttribute("userAccounts", userService.findUserByEmail(authentication.getName()).getUserAccounts());
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

        model.addAttribute("operation", operation);

        return "/operations/edit";
    }

    @RequestMapping(value = "/edit/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String editOperation(@PathVariable("id") int id, @ModelAttribute("operation") @Valid Operation operation, BindingResult bindingResult,
                                  Model model){

        if(bindingResult.hasErrors()){
            return "/operations/edit";
        }
        Operation operationToUpdate = operationService.findById(id);

        operation.setInvolvedAccount(operationToUpdate.getInvolvedAccount());
        operation.setUser(operationToUpdate.getUser());
        operation.setCategory(operationToUpdate.getCategory());

        operationService.update(id, operation);

        return "redirect:/operations";
    }

    @RequestMapping(value = "/delete/{id}", method = {RequestMethod.DELETE, RequestMethod.POST})
    public String deleteOperation(@PathVariable("id") int id){

        Operation operation = operationService.findById(id);

        operationService.delete(id);

        return "redirect:/operations";
    }
}
