package org.qweshqa.financialmanager.controllers;

import jakarta.validation.Valid;
import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.CategoryService;
import org.qweshqa.financialmanager.services.OperationService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.AmountFormatter;
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

import java.util.List;

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
    public String viewCategories(@RequestParam(value = "t", defaultValue = "expense") String type, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);

        CategoryType categoryType = categoryTypeStringConverter.convert(type.toUpperCase());

        List<Category> categories = categoryService.findAllByTypeAndUser(categoryType, user);

        model.addAttribute("categories", categories);

        return "categories/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewCategory(@PathVariable("id") int id, Model model){
        Category category;

        try{
            category = categoryService.findById(id);
        } catch (CategoryNotFoundException e){
            model.addAttribute("errorTitle", "404 Nothing found");
            model.addAttribute("errorMessage", "Category with this id doesn't exist");
            return "error";
        }

        model.addAttribute("category", category);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);

        List<Operation> categoryOperations = operationService.findAllByCategory(category, user);

        model.addAttribute("categoryOperations", categoryOperations);

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
}
