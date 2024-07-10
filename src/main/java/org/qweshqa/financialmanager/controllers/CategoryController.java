package org.qweshqa.financialmanager.controllers;

import org.qweshqa.financialmanager.models.Category;
import org.qweshqa.financialmanager.models.Operation;
import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.CategoryService;
import org.qweshqa.financialmanager.services.OperationService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.AmountFormatter;
import org.qweshqa.financialmanager.utils.converters.CategoryTypeStringConverter;
import org.qweshqa.financialmanager.utils.enums.CategoryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/category")
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
        User user = userService.findUserByEmail(authentication.getName()).get();

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);

        CategoryType categoryType = categoryTypeStringConverter.convert(type.toUpperCase());

        List<Category> categories = categoryService.findAllByTypeAndUser(categoryType, user);

        model.addAttribute("categories", categories);

        return "categories/list";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewCategory(@PathVariable("id") int id, Model model){
        Optional<Category> category = categoryService.findById(id);

        if(category.isEmpty()){
            model.addAttribute("errorTitle", "404 Nothing found");
            model.addAttribute("errorMessage", "Category with this id doesn't exist");
            return "error";
        }

        model.addAttribute("category", category.get());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName()).get();

        model.addAttribute("user", user);
        model.addAttribute("amountFormatter", amountFormatter);

        List<Operation> categoryOperations = operationService.findAllByCategory(category.get(), user);

        model.addAttribute("categoryFinances", categoryOperations);

        return "categories/view";
    }
}
