package org.qweshqa.financialmanager.controllers;

import jakarta.validation.Valid;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.CategoryService;
import org.qweshqa.financialmanager.services.OperationService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.AmountFormatter;
import org.qweshqa.financialmanager.utils.DateWrapper;
import org.qweshqa.financialmanager.utils.exceptions.OperationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
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
        DateWrapper dateWrapper = new DateWrapper(LocalDate.now());

        try{
            operationService.configureStringDateValues(year, month, day, period, dateWrapper);
        } catch (DateTimeException e){
            switch (e.getMessage()){
                case "Year period error":
                    return "redirect:/operations?p=year" +
                            "&y=" + dateWrapper.getDate().getYear();

                case "Month period error":
                    return "redirect:/operations?p=month" +
                            "&y=" + dateWrapper.getDate().getYear() +
                            "&m=" + dateWrapper.getDate().getMonth().getValue();

                case "Day period error":
                    return "redirect:/operations?p=day" +
                            "&y=" + dateWrapper.getDate().getYear() +
                            "&m=" + dateWrapper.getDate().getMonth().getValue() +
                            "&d=" + dateWrapper.getDate().getDayOfMonth();
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("settings", user.getSetting());

        LocalDate date = dateWrapper.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

        switch(period){
            case "all-time":
                model.addAttribute("operations", operationService.findAllByUser(user));
                model.addAttribute("displayDate", "All time");
                break;

            case "year":
                if(!year.isBlank()){
                    date = date.withYear(Integer.parseInt(year));
                }
                model.addAttribute("operations", operationService.findAllByUserAndYear(user, date.getYear()));
                model.addAttribute("displayDate", date.getYear());
                break;

            case "month":
                if(!year.isBlank()){
                    date = date.withYear(Integer.parseInt(year));
                }
                if(!month.isBlank()){
                    date = date.withMonth(Integer.parseInt(month));
                }

                model.addAttribute("displayDate", (date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ", " + date.getYear()));
                model.addAttribute("operations", operationService.findAllByUserAndMonth(user, date));
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

                model.addAttribute("operations", operationService.findAllByUserAndDate(user, date));
                model.addAttribute("displayDate", date.format(formatter));
                break;
        }
        model.addAttribute("amountFormatter", amountFormatter);

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

        List<Integer> years = new ArrayList<>();
        for(int i = LocalDate.now().getYear(); i <= 2100; i++){
            years.add(i);
        }
        List<Integer> months = new ArrayList<>();
        for(int i = 1; i <= 12; i++){
            months.add(i);
        }
        List<Integer> days = new ArrayList<>();
        for(int i = 1; i <= 31; i++){
            days.add(i);
        }

        model.addAttribute("years", years);
        model.addAttribute("months", months);
        model.addAttribute("days", days);

        model.addAttribute("now", LocalDate.now());
        return "operations/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createOperation(@ModelAttribute("new_operation") @Valid Operation operation, BindingResult bindingResult,
                                  @RequestParam("y") int year,
                                  @RequestParam("m") int month,
                                  @RequestParam("d") int day,
                                  Model model) {
        LocalDate date;

        try{
            date = LocalDate.of(year, month, day);
        } catch (DateTimeParseException e){
            return "redirect:/operations/create?de";
        }
        if(date.isBefore(LocalDate.now())){
            return "redirect:/operations/create?de";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(bindingResult.hasErrors()){
            model.addAttribute("userAccounts", userService.findUserByEmail(authentication.getName()).getUserAccounts());
            return "operations/create";
        }

        User user = userService.findUserByEmail(authentication.getName());
        operation.setUser(user);
        operation.setDate(date);

        if(!operation.isScheduled()){
            operationService.prepareForSave(operation);
        }
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
    public String editOperation(@PathVariable("id") int id, @ModelAttribute("operation") @Valid Operation operation, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return "/operations/edit";
        }
        Operation operationToUpdate = operationService.findById(id);

        operation.setInvolvedAccount(operationToUpdate.getInvolvedAccount());
        operation.setUser(operationToUpdate.getUser());
        operation.setCategory(operationToUpdate.getCategory());

        operationService.prepareForUpdate(operationToUpdate, operation);
        operationService.update(operationToUpdate, operation);

        return "redirect:/operations";
    }

    @RequestMapping(value = "/delete/{id}", method = {RequestMethod.DELETE, RequestMethod.POST})
    public String deleteOperation(@PathVariable("id") int id) {

        Operation operation = operationService.findById(id);

        operationService.prepareForDelete(operation);
        operationService.delete(operation);

        return "redirect:/operations";
    }
}
