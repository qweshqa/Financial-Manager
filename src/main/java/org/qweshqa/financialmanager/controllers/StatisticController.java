package org.qweshqa.financialmanager.controllers;

import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.services.CategoryService;
import org.qweshqa.financialmanager.services.OperationService;
import org.qweshqa.financialmanager.services.StatisticService;
import org.qweshqa.financialmanager.services.UserService;
import org.qweshqa.financialmanager.utils.AmountFormatter;
import org.qweshqa.financialmanager.utils.DateWrapper;
import org.qweshqa.financialmanager.utils.enums.CategoryType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Controller
@RequestMapping("/statistics")
public class StatisticController {

    private final OperationService operationService;

    private final UserService userService;

    private final CategoryService categoryService;

    private final AmountFormatter amountFormatter;

    public StatisticController(OperationService operationService, UserService userService, CategoryService categoryService, AmountFormatter amountFormatter) {
        this.operationService = operationService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.amountFormatter = amountFormatter;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewStatistic(@RequestParam(value = "p", defaultValue = "all-time") String period,
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
                    return "redirect:/statistics?p=year" +
                            "&y=" + dateWrapper.getDate().getYear();

                case "Month period error":
                    return "redirect:/statistics?p=month" +
                            "&y=" + dateWrapper.getDate().getYear() +
                            "&m=" + dateWrapper.getDate().getMonth().getValue();

                case "Day period error":
                    return "redirect:/statistics?p=day" +
                            "&y=" + dateWrapper.getDate().getYear() +
                            "&m=" + dateWrapper.getDate().getMonth().getValue() +
                            "&d=" + dateWrapper.getDate().getDayOfMonth();
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        LocalDate date = dateWrapper.getDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

        float cash_flow = 0.0f;
        switch(period){
            case "all-time":
                cash_flow = categoryService.getCategoriesTotalByUserAndType(user, CategoryType.INCOME)
                        - categoryService.getCategoriesTotalByUserAndType(user, CategoryType.EXPENSE);

                model.addAttribute("displayDate", "All time");
                break;

            case "year":
                if(!year.isBlank()){
                    date = date.withYear(Integer.parseInt(year));
                }

                cash_flow = categoryService.getCategoriesTotalByUserAndYearAndType(user, date.getYear(), CategoryType.INCOME)
                        - categoryService.getCategoriesTotalByUserAndYearAndType(user, date.getYear(), CategoryType.EXPENSE);

                model.addAttribute("displayDate", date.getYear());
                break;

            case "month":
                if(!year.isBlank()){
                    date = date.withYear(Integer.parseInt(year));
                }
                if(!month.isBlank()){
                    date = date.withMonth(Integer.parseInt(month));
                }

                cash_flow = categoryService.getCategoriesTotalByUserAndYearAndMonthAndType(user, date.getYear(), date.getMonthValue(), CategoryType.INCOME)
                        - categoryService.getCategoriesTotalByUserAndYearAndMonthAndType(user, date.getYear(), date.getMonthValue(), CategoryType.EXPENSE);

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

                cash_flow = categoryService.getCategoriesTotalByUserAndDateAndType(user, date, CategoryType.INCOME)
                        - categoryService.getCategoriesTotalByUserAndDateAndType(user, date, CategoryType.EXPENSE);

                model.addAttribute("displayDate", date.format(formatter));
                break;

        }

        model.addAttribute("cash_flow", cash_flow);

        model.addAttribute("user", user);
        model.addAttribute("settings", user.getSetting());

        model.addAttribute("date", date);
        model.addAttribute("period", period);

        model.addAttribute("amountFormatter", amountFormatter);

        return "/statistic/main";
    }
}
