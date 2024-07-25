package org.qweshqa.financialmanager.controllers;

import jakarta.validation.Valid;
import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.CategoryService;
import org.qweshqa.financialmanager.services.OperationService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.AmountFormatter;
import org.qweshqa.financialmanager.utils.DateWrapper;
import org.qweshqa.financialmanager.utils.converters.CategoryTypeStringConverter;
import org.qweshqa.financialmanager.utils.enums.CategoryType;
import org.qweshqa.financialmanager.utils.exceptions.CategoryNotFoundException;
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
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final UserService userService;

    private final OperationService operationService;

    private final CategoryTypeStringConverter categoryTypeStringConverter;

    private final AmountFormatter amountFormatter;

    @Autowired
    public CategoryController(CategoryService categoryService, UserService userService, CategoryTypeStringConverter categoryTypeStringConverter, AmountFormatter amountFormatter, OperationService operationService) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.categoryTypeStringConverter = categoryTypeStringConverter;
        this.amountFormatter = amountFormatter;
        this.operationService = operationService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewCategories(@RequestParam(value = "p", defaultValue = "day") String period,
                                 @RequestParam(value = "d", defaultValue = "") String day,
                                 @RequestParam(value = "m", defaultValue = "") String month,
                                 @RequestParam(value = "y", defaultValue = "") String year,
                                 @RequestParam(value = "t", defaultValue = "expense") String type,
                                 Model model){
        DateWrapper dateWrapper = new DateWrapper(LocalDate.now());

        try{
            operationService.configureStringDateValues(year, month, day, period, dateWrapper);
        } catch (DateTimeException e){
            switch (e.getMessage()){
                case "Year period error":
                    return "redirect:/categories?p=year" +
                            "&y=" + dateWrapper.getDate().getYear();

                case "Month period error":
                    return "redirect:/categories?p=month" +
                            "&y=" + dateWrapper.getDate().getYear() +
                            "&m=" + dateWrapper.getDate().getMonth().getValue();

                case "Day period error":
                    return "redirect:/categories?p=day" +
                            "&y=" + dateWrapper.getDate().getYear() +
                            "&m=" + dateWrapper.getDate().getMonth().getValue() +
                            "&d=" + dateWrapper.getDate().getDayOfMonth();
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);

        LocalDate date = dateWrapper.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

        switch (period){
            case "all-time":
                model.addAttribute("expense_total", categoryService.getCategoriesTotalByUserAndType(user, CategoryType.EXPENSE));
                model.addAttribute("income_total", categoryService.getCategoriesTotalByUserAndType(user, CategoryType.INCOME));

                model.addAttribute("displayDate", "All time");

                break;

            case "year":
                if(!year.isBlank()){
                    date = date.withYear(Integer.parseInt(year));
                }

                model.addAttribute("expense_total", categoryService.getCategoriesTotalByUserAndYearAndType(user, date.getYear(), CategoryType.EXPENSE));
                model.addAttribute("income_total", categoryService.getCategoriesTotalByUserAndYearAndType(user, date.getYear(), CategoryType.INCOME));

                model.addAttribute("displayDate", date.getYear());

                break;

            case "month":
                if(!year.isBlank()){
                    date = date.withYear(Integer.parseInt(year));
                }
                if(!month.isBlank()){
                    date = date.withMonth(Integer.parseInt(month));
                }

                model.addAttribute("expense_total", categoryService.getCategoriesTotalByUserAndYearAndMonthAndType(user, date.getYear(), date.getMonthValue(), CategoryType.EXPENSE));
                model.addAttribute("income_total", categoryService.getCategoriesTotalByUserAndYearAndMonthAndType(user, date.getYear(), date.getMonthValue(), CategoryType.INCOME));

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

                model.addAttribute("expense_total", categoryService.getCategoriesTotalByUserAndDateAndType(user, date, CategoryType.EXPENSE));
                model.addAttribute("income_total", categoryService.getCategoriesTotalByUserAndDateAndType(user, date, CategoryType.INCOME));

                model.addAttribute("displayDate", date.format(formatter));

                break;
        }

        CategoryType categoryType = categoryTypeStringConverter.convert(type.toUpperCase());
        
        List<Category> categories = categoryService.findAllByUserAndArchivedAndType(user, false, categoryType);
        model.addAttribute("categories", categories);

        model.addAttribute("categoryService", categoryService);
        model.addAttribute("type", type);

        model.addAttribute("date", date);
        model.addAttribute("period", period);

        return "categories/list";
    }

    @RequestMapping(value = "/archive", method = RequestMethod.GET)
    public String viewArchive(@RequestParam(value = "t", defaultValue = "expense") String type,
                              Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);

        model.addAttribute("expense_total", categoryService.getCategoriesTotalByUserAndType(user, CategoryType.EXPENSE));
        model.addAttribute("income_total", categoryService.getCategoriesTotalByUserAndType(user, CategoryType.INCOME));

        CategoryType categoryType = categoryTypeStringConverter.convert(type.toUpperCase());

        List<Category> categories = categoryService.findAllByUserAndArchivedAndType(user, true, categoryType);

        model.addAttribute("categories", categories);

        return "categories/archive";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewCategory(@PathVariable("id") int id,
                               @RequestParam(value = "p", defaultValue = "all-time") String operationDisplayPeriod,
                               @RequestParam(value = "d", defaultValue = "") String day,
                               @RequestParam(value = "m", defaultValue = "") String month,
                               @RequestParam(value = "y", defaultValue = "") String year, Model model){
        Category category;

        try{
            category = categoryService.findById(id);
        } catch (CategoryNotFoundException e){
            model.addAttribute("errorTitle", "404 Nothing found");
            model.addAttribute("errorMessage", "Category with this id doesn't exist");
            return "error";
        }

        DateWrapper dateWrapper = new DateWrapper(LocalDate.now());

        try{
            operationService.configureStringDateValues(year, month, day, operationDisplayPeriod, dateWrapper);
        } catch (DateTimeException e){
            switch (e.getMessage()){
                case "Year period error":
                    return "redirect:/categories/" + id + "?p=year" +
                            "&y=" + dateWrapper.getDate().getYear();

                case "Month period error":
                    return "redirect:/categories/" + id + "?p=month" +
                            "&y=" + dateWrapper.getDate().getYear() +
                            "&m=" + dateWrapper.getDate().getMonth().getValue();

                case "Day period error":
                    return "redirect:/categories/" + id + "?p=day" +
                            "&y=" + dateWrapper.getDate().getYear() +
                            "&m=" + dateWrapper.getDate().getMonth().getValue() +
                            "&d=" + dateWrapper.getDate().getDayOfMonth();
            }
        }

        model.addAttribute("category", category);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);

        LocalDate date = dateWrapper.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

        List<Operation> categoryOperations = new ArrayList<>();
        switch(operationDisplayPeriod){
            case "all-time":
                categoryOperations = categoryService.findAllOperationsByUser(category, user);
                model.addAttribute("displayDate", "All time");
                break;

            case "year":
                if(!year.isBlank()){
                    date = date.withYear(Integer.parseInt(year));
                }
                categoryOperations = categoryService.findAllOperationsByUserAndYear(category, user, date.getYear());
                model.addAttribute("displayDate", date.getYear());
                break;

            case "month":
                if(!year.isBlank()){
                    date = date.withYear(Integer.parseInt(year));
                }
                if(!month.isBlank()){
                    date = date.withMonth(Integer.parseInt(month));
                }

                categoryOperations = categoryService.findAllOperationsByUserAndMonth(category, user, date);
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

                categoryOperations = categoryService.findAllOperationsByUserAndDate(category, user, date);
                model.addAttribute("displayDate", date.format(formatter));
                break;
        }
        model.addAttribute("categoryOperations", categoryOperations);

        model.addAttribute("period", operationDisplayPeriod);

        model.addAttribute("date", date);

        return "categories/view";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createCategory(Model model){
        model.addAttribute("category", new Category());

        return "categories/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createCategory(@ModelAttribute("category") @Valid Category category, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){
            return "categories/create";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        category.setUser(user);

        categoryService.save(category);

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);

        return "redirect:/categories?t=" + category.getCategoryType().toString().toLowerCase();
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editCategory(@PathVariable("id") int id, Model model){
        Category category;

        try{
            category = categoryService.findById(id);
        } catch (CategoryNotFoundException e){
            model.addAttribute("errorTitle", "404 Nothing found");
            model.addAttribute("errorMessage", "Category with this id doesn't exist");
            return "error";
        }

        model.addAttribute("category", category);

        return "categories/edit";
    }

    @RequestMapping(value = "/edit/{id}", method =  {RequestMethod.PATCH, RequestMethod.POST})
    public String editCategory(@PathVariable("id") int id, @ModelAttribute("category") @Valid Category updatedCategory,
                               BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "categories/edit";
        }

        Category categoryToUpdate = categoryService.findById(id);

        updatedCategory.setUser(categoryToUpdate.getUser());

        categoryService.update(categoryToUpdate, updatedCategory);

        return "redirect:/categories/" + id;
    }

    @RequestMapping(value = "/delete/{id}", method = {RequestMethod.DELETE, RequestMethod.POST})
    public String deleteCategory(@PathVariable("id") int id){
        categoryService.delete(id);

        return "redirect:/categories";
    }

    @RequestMapping(value = "/archive/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String archiveCategory(@PathVariable("id") int id){
        Category category = categoryService.findById(id);

        categoryService.archive(category);

        return "redirect:/categories/archive?t=" + category.getCategoryType().toString().toLowerCase();
    }

    @RequestMapping(value = "/unzip/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String unzipCategory(@PathVariable("id") int id){
        Category category = categoryService.findById(id);

        categoryService.unzip(category);

        return "redirect:/categories/archive?t=" + category.getCategoryType().toString().toLowerCase();
    }
}
